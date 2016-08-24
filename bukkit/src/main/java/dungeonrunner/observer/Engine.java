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
import dungeonrunner.location.LogicalLocationType;
import dungeonrunner.model.Arena;
import dungeonrunner.model.Entrance;
import dungeonrunner.model.FreeObject;
import dungeonrunner.model.Lounge;
import dungeonrunner.model.PlayerContainer;
import dungeonrunner.model.World;
import dungeonrunner.player.Character;
import dungeonrunner.player.CharacterManager;
import dungeonrunner.player.PlayerCharacter;
import dungeonrunner.system.di.Inject;
import dungeonrunner.system.manager.ManagerException;
import dungeonrunner.system.util.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayDeque;
import java.util.Queue;

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

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while(!running) {
                Log.info(this, "run", "checking...");

                Ticket ticket = tickets.poll();
                while(ticket != null) {
                    processTicket(ticket);
                    ticket = tickets.poll();
                }

                cleanUp();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            Log.info(this, "run", "stopped...");
        }
    };

    private Thread thread = new Thread(runnable);

    private volatile boolean running;

    private void processTicket(Ticket ticket) {
        PlayerCharacter playerCharacter = ticket.getPlayerCharacter();
        Player player = playerCharacter.getPlayer();
        Character character = playerCharacter.getCharacter();
        if (ticket instanceof EnterTicket) {
            EnterTicket enterTicket = (EnterTicket) ticket;
            Log.info(this, "processTicket", "enter: %s, player: %s", ticket.getClass().getSimpleName(), character.getUuid());
            switch (enterTicket.getLogicalLocationType()) {
                case ARENA:
                    Arena arena = findArena();
                    world.enterArena(playerCharacter, arena);
                    onEnterArena(playerCharacter, arena);
                    break;
                case PLAYER_LOUNGE:
                    arena = findArena();
                    FreeObject<Lounge> loungeFreeObject = arena.findFreePlayerLounge();
                    Lounge playerLounge = loungeFreeObject.getObject();
                    if (loungeFreeObject.isMustCreate()) {
                        playerLounge = arena.createPlayerLounge(loungeFreeObject.getId());
                    }
                    world.enterPlayerLounge(playerCharacter, playerLounge);
                    onEnterPlayerLounge(playerCharacter, playerLounge);
                    break;
                case ADMIN_LOUNGE:
                    arena = findArena();
                    FreeObject<Lounge> adminLoungeFreeObject = arena.findFreeAdminLounge();
                    Lounge adminLounge = adminLoungeFreeObject.getObject();
                    if (adminLoungeFreeObject.isMustCreate()) {
                        adminLounge = arena.createAdminLounge(adminLoungeFreeObject.getId());
                    }
                    world.enterAdminLounge(playerCharacter, adminLounge);
                    onEnterAdminLounge(playerCharacter, adminLounge);
                    break;
            }
        }
    }

    private void cleanUp() {
        for (PlayerContainer playerContainer : world.cleanUp()) {
            blockBuilder.destroy(playerContainer);
        }
    }

    public Arena findArena() {
        FreeObject<Arena> arenaFreeObject = world.findFreeArena();
        Arena arena = arenaFreeObject.getObject();
        if (arenaFreeObject.isMustCreate()) {
            arena = createArena(arenaFreeObject.getId());
        }
        return arena;
    }

    public void start() {
        if (!running) {
            Log.info(this, "start", "starting...");
            // reset world buildings
            world.setEntrance(createEntrance());
            thread.start();
        }
    }

    public void stop() {
        if (running) {
            Log.info(this, "stop", "stopping...");
            running = false;
        }
    }

    private Arena createArena(int id) {
        Arena arena = new Arena(id);
        return arena;
    }

    public Entrance createEntrance() {
        return new Entrance();
    }

    public void onEnterEntrance(PlayerCharacter playerCharacter, Entrance entrance) {
        Log.info(this, "onEnterEntrance", "player=%s", playerCharacter.getPlayer().getName());
        tickets.add(new EnterTicket(playerCharacter, LogicalLocationType.ARENA));
    }

    public void onEnterArena(PlayerCharacter playerCharacter, Arena arena) {
        Log.info(this, "onEnterArena", "player=%s", playerCharacter.getPlayer().getName());
        tickets.add(new EnterTicket(playerCharacter, PLAYER_LOUNGE));
    }

    public void onEnterPlayerLounge(PlayerCharacter playerCharacter, Lounge playerLounge) {
        Log.info(this, "onEnterPlayerLounge", "player=%s", playerCharacter.getPlayer().getName());
        //
        playerCharacter.getPlayer().sendMessage(String.format("Welcome to the player lounge '%s' with %s players.", playerLounge.getId(), playerLounge.count()));
    }

    public void onEnterAdminLounge(PlayerCharacter playerCharacter, Lounge adminLounge) {
        Log.info(this, "onEnterAdminLounge", "player=%s", playerCharacter.getPlayer().getName());
        //
    }

    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Log.info(this, "onLogin", "player=%s", player.getName());
        try {
            Character character = characterManager.login(player);
            PlayerCharacter playerCharacter = new PlayerCharacter(player, character);
            world.enterEntrance(playerCharacter);
            onEnterEntrance(playerCharacter, world.getEntrance());
        } catch (ManagerException e) {
            e.printStackTrace();
        }
    }

    public void onLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Log.info(this, "onLogout", "player=%s", player.getName());
        PlayerCharacter playerCharacter = world.getPlayerCharacter(event.getPlayer());
        world.leave(playerCharacter);
    }

}
