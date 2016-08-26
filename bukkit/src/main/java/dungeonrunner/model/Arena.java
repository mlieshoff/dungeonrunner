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
import dungeonrunner.system.util.Log;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Lieshoff
 */
public class Arena extends PlayerContainer {

    private final World world;

    private final Map<Integer, Lounge> adminLounges = new ConcurrentHashMap<>();
    private final Map<Integer, Lounge> playerLounges = new ConcurrentHashMap<>();
    private final Map<Integer, Vault> vaults = new ConcurrentHashMap<>();

    public Arena(World world, int id) {
        super(id);
        this.world = world;
    }

    public Lounge createPlayerLounge(int id) {
        Lounge lounge = new PlayerLounge(this, id);
        playerLounges.put(id, lounge);
        return lounge;
    }

    public Lounge createAdminLounge(int id) {
        Lounge lounge = new AdminLounge(this, id);
        adminLounges.put(id, lounge);
        return lounge;
    }

    public Vault createVault(int id) {
        Vault vault = new Vault(this, id);
        vaults.put(id, vault);
        return vault;
    }

    public FreeObject<Lounge> findFreePlayerLounge() {
        return findFree(Config.MAX_PLAYER_LOUNGES_PER_ARENA, playerLounges);
    }

    public FreeObject<Lounge> findFreeAdminLounge() {
        return findFree(Config.MAX_ADMIN_LOUNGES_PER_ARENA, playerLounges);
    }

    public FreeObject<Vault> findFreeVault() {
        return findFree(Config.MAX_VAULTS_PER_ARENA, vaults);
    }

    public Set<PlayerContainer> destroy() {
        Set<PlayerContainer> set = super.destroy();
        Set<PlayerContainer> containers = new HashSet<>();

        containers.addAll(adminLounges.values());
        containers.addAll(playerLounges.values());
        containers.addAll(vaults.values());

        for (PlayerContainer playerContainer : containers) {
            Log.info(this, "destroy", "check: %s", playerContainer);
            if (playerContainer.isEmpty()) {
                Set<PlayerContainer> subContainers = playerContainer.destroy();
                Log.info(this, "destroy", "    subcontainers: %s", subContainers);
                if (subContainers.size() > 0) {
                    set.addAll(subContainers);
                }
            }
        }

        for (PlayerContainer playerContainer : set) {
            if (playerContainer instanceof AdminLounge) {
                adminLounges.remove(playerContainer.getId());
            } else if (playerContainer instanceof PlayerLounge) {
                playerLounges.remove(playerContainer.getId());
            } else if (playerContainer instanceof Vault) {
                vaults.remove(playerContainer.getId());
            }
        }

        if (adminLounges.size() == 0 && playerLounges.size() == 0 && vaults.size() == 0) {
            set.add(this);
        }

        return set;
    }

    public World getWorld() {
        return world;
    }

}
