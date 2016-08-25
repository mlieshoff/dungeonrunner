package dungeonrunner.model;

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

import dungeonrunner.Config;
import dungeonrunner.player.PlayerCharacter;
import dungeonrunner.system.util.Log;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Lieshoff
 */
public class World extends PlayerContainer {

    private Entrance entrance;

    private Map<Integer, Arena> arenas = new ConcurrentHashMap<>();

    private Map<Integer, PlayerCharacter> id2Characters = new ConcurrentHashMap<>();
    private Map<String, PlayerCharacter> uuid2Characters = new ConcurrentHashMap<>();

    private Map<PlayerCharacter, PlayerContainer> characters2Container = new ConcurrentHashMap<>();

    public World() {
        super(1);
    }

    public void enterEntrance(PlayerCharacter playerCharacter) {
        enter(playerCharacter);
        characters2Container.put(playerCharacter, entrance);
        entrance.increment();
    }

    public FreeObject<Arena> findFreeArena() {
        return findFree(Config.MAX_ARENAS, arenas);
    }

    public void enterArena(PlayerCharacter playerCharacter, Arena arena) {
        arenas.put(arena.getId(), arena);
        addToTargetLocation(playerCharacter, arena);
    }

    private void addToTargetLocation(PlayerCharacter playerCharacter, PlayerContainer newPlayerContainer) {
        PlayerContainer oldPlayerContainer = characters2Container.get(playerCharacter);
        if (oldPlayerContainer != null) {
            oldPlayerContainer.decrement();
        }
        if (newPlayerContainer != null) {
            characters2Container.put(playerCharacter, newPlayerContainer);
            newPlayerContainer.increment();
        }
        Log.info(this, "addToTargetLocation", "from %s to %s", oldPlayerContainer, newPlayerContainer);
    }

    public void enterPlayerLounge(PlayerCharacter playerCharacter, Lounge playerLounge) {
        addToTargetLocation(playerCharacter, playerLounge);
    }

    public void enterAdminLounge(PlayerCharacter playerCharacter, Lounge adminLounge) {
        addToTargetLocation(playerCharacter, adminLounge);
    }

    public void enterVault(PlayerCharacter playerCharacter, Vault vault) {
        addToTargetLocation(playerCharacter, vault);
    }

    public void enterDungeon(PlayerCharacter playerCharacter, Dungeon dungeon) {
        addToTargetLocation(playerCharacter, dungeon);
    }

    public void setEntrance(Entrance entrance) {
        this.entrance = entrance;
    }

    public Entrance getEntrance() {
        return entrance;
    }

    public void enter(PlayerCharacter playerCharacter) {
        id2Characters.put(playerCharacter.getCharacter().getId(), playerCharacter);
        uuid2Characters.put(playerCharacter.getPlayer().getUniqueId().toString(), playerCharacter);
    }

    public void leave(PlayerCharacter playerCharacter) {
        id2Characters.remove(playerCharacter.getCharacter().getId());
        uuid2Characters.remove(playerCharacter.getCharacter().getUuid());
        PlayerContainer oldPlayerContainer = characters2Container.remove(playerCharacter);
        if (oldPlayerContainer != null) {
            oldPlayerContainer.decrement();
        }
    }

    public PlayerCharacter getPlayerCharacter(Player player) {
        return uuid2Characters.get(player.getUniqueId().toString());
    }

    public Set<PlayerContainer> destroy() {
        Set<PlayerContainer> set = super.destroy();

        for (PlayerContainer playerContainer : arenas.values()) {
            if (playerContainer.isEmpty()) {
                Set<PlayerContainer> subContainers = playerContainer.destroy();
                if (subContainers.size() > 0) {
                    set.addAll(subContainers);
                }
            }
        }

        for (PlayerContainer playerContainer : set) {
            if (playerContainer instanceof Arena) {
                arenas.remove(playerContainer.getId());
            }
        }

        if (arenas.size() == 0) {
//            set.add(this);
        }

        return set;
    }

}
