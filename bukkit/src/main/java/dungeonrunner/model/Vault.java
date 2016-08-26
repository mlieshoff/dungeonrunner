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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Lieshoff
 */
public class Vault extends PlayerContainer {

    private final Arena arena;

    private Map<Integer, Dungeon> dungeons = new ConcurrentHashMap<>();

    public Vault(Arena arena, int id) {
        super(id);
        this.arena = arena;
    }

    public Dungeon createDungeon(int id) {
        Dungeon dungeon = new Dungeon(this, id);
        dungeons.put(id, dungeon);
        return dungeon;
    }

    public FreeObject<Dungeon> findFreeDungeon() {
        return findFree(Config.MAX_DUNGEONS_PER_VAULT, dungeons);
    }

    public Set<PlayerContainer> destroy() {
        Set<PlayerContainer> set = super.destroy();

        for (PlayerContainer playerContainer : dungeons.values()) {
            if (playerContainer.isEmpty()) {
                Set<PlayerContainer> subContainers = playerContainer.destroy();
                if (subContainers.size() > 0) {
                    set.addAll(subContainers);
                }
            }
        }

        for (PlayerContainer playerContainer : set) {
            if (playerContainer instanceof Dungeon) {
                dungeons.remove(playerContainer.getId());
            }
        }

        if (dungeons.size() == 0) {
            set.add(this);
        }

        return set;
    }

    public Arena getArena() {
        return arena;
    }

}
