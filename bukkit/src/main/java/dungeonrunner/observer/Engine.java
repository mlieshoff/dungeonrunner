package dungeonrunner.observer;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import dungeonrunner.BlockBuilder;
import dungeonrunner.Config;
import dungeonrunner.Teleporter;
import dungeonrunner.location.LogicalLocationType;
import dungeonrunner.model.AdminLounge;
import dungeonrunner.model.Arena;
import dungeonrunner.model.Dungeon;
import dungeonrunner.model.Entrance;
import dungeonrunner.model.FreeObject;
import dungeonrunner.model.Lounge;
import dungeonrunner.model.PlayerContainer;
import dungeonrunner.model.PlayerLounge;
import dungeonrunner.model.Vault;
import dungeonrunner.model.World;
import dungeonrunner.player.Character;
import dungeonrunner.player.CharacterManager;
import dungeonrunner.player.PlayerCharacter;
import dungeonrunner.system.di.Inject;
import dungeonrunner.system.manager.ManagerException;
import dungeonrunner.system.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

import static dungeonrunner.location.LogicalLocationType.PLAYER_LOUNGE;

/**
 * @author Michael Lieshoff
 */
public class Engine {

    private Queue<Ticket> tickets = new ArrayDeque<>();

    @Inject
    private CharacterManager characterManager;

    @Inject
    private World world;

    @Inject
    private BlockBuilder blockBuilder;

    @Inject
    private Teleporter teleporter;

    private Plugin plugin;

    private volatile long lastClean = 0;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.info(this, "run", "checking...");

            long now = System.currentTimeMillis();

            Ticket ticket = tickets.poll();
            while(ticket != null) {
                processTicket(ticket);
                ticket = tickets.poll();
            }

            if (now > lastClean + Config.CLEAN_TIMEOUT) {
                lastClean = now;
                cleanUp();
            }

            Log.info(this, "run", "stopped...");
        }
    };

    private volatile boolean running;

    private void processTicket(Ticket ticket) {
        PlayerCharacter playerCharacter = ticket.getPlayerCharacter();
        Player player = playerCharacter.getPlayer();
        Character character = playerCharacter.getCharacter();
        if (ticket instanceof EnterTicket) {
            EnterTicket enterTicket = (EnterTicket) ticket;
            FreeObject<Vault> vaultFreeObject;
            Vault vault;
            Log.info(this, "processTicket", "enter: %s, player: %s", ticket.getClass().getSimpleName(), character.getUuid());
            switch (enterTicket.getLogicalLocationType()) {
                case ARENA:
                    Arena arena = findArena();
                    world.enterArena(playerCharacter, arena);
                    onEnterArena(playerCharacter, arena);
                    break;
                case PLAYER_LOUNGE:
                    arena = findArena();
                    FreeObject<PlayerLounge> loungeFreeObject = arena.findFreePlayerLounge();
                    PlayerLounge playerLounge = loungeFreeObject.getObject();
                    if (loungeFreeObject.isMustCreate()) {
                        playerLounge = arena.createPlayerLounge(loungeFreeObject.getId());
                    }
                    world.enterPlayerLounge(playerCharacter, playerLounge);
                    onEnterPlayerLounge(playerCharacter, playerLounge);
                    break;
                case ADMIN_LOUNGE:
                    arena = findArena();
                    FreeObject<AdminLounge> adminLoungeFreeObject = arena.findFreeAdminLounge();
                    Lounge adminLounge = adminLoungeFreeObject.getObject();
                    if (adminLoungeFreeObject.isMustCreate()) {
                        adminLounge = arena.createAdminLounge(adminLoungeFreeObject.getId());
                    }
                    world.enterAdminLounge(playerCharacter, adminLounge);
                    onEnterAdminLounge(playerCharacter, adminLounge);
                    break;
                case VAULT:
                    arena = findArena();
                    vaultFreeObject = arena.findFreeVault();
                    vault = vaultFreeObject.getObject();
                    if (vaultFreeObject.isMustCreate()) {
                        vault = arena.createVault(vaultFreeObject.getId());
                    }
                    world.enterVault(playerCharacter, vault);
                    onEnterVault(playerCharacter, vault);
                    break;
                case DUNGEON:
                    arena = findArena();
                    vaultFreeObject = arena.findFreeVault();
                    vault = vaultFreeObject.getObject();
                    if (vaultFreeObject.isMustCreate()) {
                        vault = arena.createVault(vaultFreeObject.getId());
                    }
                    FreeObject<Dungeon> dungeonFreeObject = vault.findFreeDungeon();
                    Dungeon dungeon = dungeonFreeObject.getObject();
                    if (dungeonFreeObject.isMustCreate()) {
                        dungeon = vault.createDungeon(dungeonFreeObject.getId());
                    }
                    world.enterDungeon(playerCharacter, dungeon);
                    onEnterDungeon(playerCharacter, dungeon);
                    break;
            }
        }
    }

    private void cleanUp() {
        Log.debug(this, "cleanUp", "starting...");
        Set<PlayerContainer> cleanUpContainers = world.destroy();
        Log.debug(this, "cleanUp", "    * number of containers to clean: %s", cleanUpContainers.size());
        for (PlayerContainer playerContainer : cleanUpContainers) {
            Log.info(this, "cleanUp", "        * clean: %s", playerContainer);
            blockBuilder.destroy(playerContainer);
        }
        Log.debug(this, "cleanUp", "stop");
    }

    public Arena findArena() {
        FreeObject<Arena> arenaFreeObject = world.findFreeArena();
        Arena arena = arenaFreeObject.getObject();
        if (arenaFreeObject.isMustCreate()) {
            arena = world.createArena(arenaFreeObject.getId());
        }
        return arena;
    }

    public void start() {
        if (!running) {
            Log.info(this, "start", "starting...");
            blockBuilder.reset(plugin, world);
            blockBuilder.buildEntrance(world.getEntrance());
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, 20, 20);
        }
    }

    public void stop() {
        if (running) {
            Log.info(this, "stop", "stopping...");
            running = false;
        }
    }

    public void onEnterEntrance(PlayerCharacter playerCharacter, Entrance entrance) {
        Log.info(this, "onEnterEntrance", "player=%s", playerCharacter.getPlayer().getName());
        tickets.add(new EnterTicket(playerCharacter, LogicalLocationType.ARENA));
        Player player = playerCharacter.getPlayer();
        teleporter.teleportPlayer(player, entrance);
    }

    public void onEnterArena(PlayerCharacter playerCharacter, Arena arena) {
        Log.info(this, "onEnterArena", "player=%s, arena=%s", playerCharacter.getPlayer().getName(), arena);
        blockBuilder.buildArena(arena);
        tickets.add(new EnterTicket(playerCharacter, PLAYER_LOUNGE));
    }

    public void onEnterPlayerLounge(PlayerCharacter playerCharacter, PlayerLounge playerLounge) {
        Log.info(this, "onEnterPlayerLounge", "player=%s", playerCharacter.getPlayer().getName());
        blockBuilder.buildPlayerLounge(playerLounge);
        teleporter.teleportPlayer(playerCharacter.getPlayer(), playerLounge);
        playerCharacter.getPlayer().sendMessage(String.format("Welcome to the player lounge '%s' with %s players.", playerLounge.getId(), playerLounge.count()));
    }

    public void onEnterAdminLounge(PlayerCharacter playerCharacter, Lounge adminLounge) {
        Log.info(this, "onEnterAdminLounge", "player=%s", playerCharacter.getPlayer().getName());
        //
    }

    public void onEnterVault(PlayerCharacter playerCharacter, Vault vault) {
        Log.info(this, "onEnterVault", "player=%s", playerCharacter.getPlayer().getName());
        //
    }

    public void onEnterDungeon(PlayerCharacter playerCharacter, Dungeon dungeon) {
        Log.info(this, "onEnterDungeon", "player=%s", playerCharacter.getPlayer().getName());
        //
    }

    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Log.info(this, "onJoin", "BEGIN");
        Log.info(this, "onJoin", "player=%s", player.getName());
        try {
            Character character = characterManager.login(player);
            PlayerCharacter playerCharacter = new PlayerCharacter(player, character);
            world.enterEntrance(playerCharacter);
            onEnterEntrance(playerCharacter, world.getEntrance());
        } catch (ManagerException e) {
            e.printStackTrace();
        }
        Log.info(this, "onJoin", "END");
    }

    public void onLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Log.info(this, "onLogout", "player=%s", player.getName());
        PlayerCharacter playerCharacter = world.getPlayerCharacter(event.getPlayer());
        world.leave(playerCharacter);
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

}
