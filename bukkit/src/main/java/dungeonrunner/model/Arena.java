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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Lieshoff
 */
public class Arena extends PlayerContainer {

    private Map<Integer, Lounge> adminLounges = new ConcurrentHashMap<>();
    private Map<Integer, Lounge> playerLounges = new ConcurrentHashMap<>();
    private Map<Integer, Dungeon> dungeons = new ConcurrentHashMap<>();
    private Map<Integer, Vault> vaults = new ConcurrentHashMap<>();

    public Arena(int id) {
        super(id);
    }

    public Lounge createPlayerLounge(int id) {
        Lounge lounge = new Lounge(this, id);
        playerLounges.put(id, lounge);
        return lounge;
    }

    public Lounge createAdminLounge(int id) {
        Lounge lounge = new Lounge(this, id);
        adminLounges.put(id, lounge);
        return lounge;
    }

    public FreeObject<Lounge> findFreePlayerLounge() {
        return findFree(Config.MAX_PLAYER_LOUNGES_PER_ARENA, playerLounges);
    }

    public FreeObject<Lounge> findFreeAdminLounge() {
        return findFree(Config.MAX_ADMIN_LOUNGES_PER_ARENA, playerLounges);
    }

    public Set<PlayerContainer> destroy() {
        Set<PlayerContainer> set = new HashSet<>();
        add(set, removeAndDestroy(getContainersToDestroy(adminLounges), adminLounges));
        add(set, removeAndDestroy(getContainersToDestroy(playerLounges), playerLounges));
        add(set, removeAndDestroy(getContainersToDestroy(dungeons), dungeons));
        add(set, removeAndDestroy(getContainersToDestroy(vaults), vaults));
        if (isEmpty()) {
            set.add(this);
        }
        return set;
    }

    private void add(Set<PlayerContainer> set, Set<PlayerContainer> containers) {
        if (containers.size() > 0) {
            set.addAll(containers);
        }
    }

    private Set<PlayerContainer> getContainersToDestroy(Map<Integer, ? extends PlayerContainer> containers) {
        Set<PlayerContainer> set = new HashSet<>();
        for (Map.Entry<Integer, ? extends PlayerContainer> entry : containers.entrySet()) {
            PlayerContainer t = entry.getValue();
            if (t.count() == 0) {
                set.add(t);
            }
        }
        return set;
    }

    private Set<PlayerContainer> removeAndDestroy(Set<PlayerContainer> set, Map<Integer, ? extends PlayerContainer> containers) {
        for (PlayerContainer t : set) {
            containers.remove(t.getId());
            t.destroy();
        }
        return set;
    }

    public boolean isEmpty() {
        return adminLounges.size() == 0
                && playerLounges.size() == 0
                && dungeons.size() == 0
                && vaults.size() == 0;
    }

}
