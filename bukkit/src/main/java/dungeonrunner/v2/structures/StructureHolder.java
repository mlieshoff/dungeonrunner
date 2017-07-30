package dungeonrunner.v2.structures;/*
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

import dungeonrunner.system.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Michael Lieshoff
 */
public class StructureHolder {

    private final int MAX_LOUNGES = 4;

    public enum State {
        REQUESTED,
        FINISHED
    }

    private ConcurrentHashMap<Class<? extends Structure>, AtomicInteger> structureCounts = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Structure> structures = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, State> structureStates = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Integer, Structure> lounges = new ConcurrentHashMap<>();

    public int getNumberOfLounges() {
        AtomicInteger count = structureCounts.putIfAbsent(LoungeStructure.class, new AtomicInteger(1));
        if (count == null) {
            count = structureCounts.get(LoungeStructure.class);
        }
        return count.get();
    }

    public Integer getFreeLoungeId() {
        for (int i = 0; i < MAX_LOUNGES; i ++) {
            Structure structure = lounges.get(i);
            if (structure == null) {
                return i;
            }
        }
        return null;
    }

    public void request(Structure structure) {
        structureStates.put(structure.getId(), State.REQUESTED);
        AtomicInteger count = structureCounts.putIfAbsent(structure.getClass(), new AtomicInteger(1));
        Log.info(this, "request", "structure=%s, count=%s", structure.getClass(), count);
        if (count != null) {
            structureCounts.get(structure.getClass()).incrementAndGet();
        }
    }

    public void register(Structure structure) {
        structures.put(structure.getId(), structure);
        structureStates.put(structure.getId(), State.FINISHED);
        if (structure instanceof LoungeStructure) {
            lounges.put(getFreeLoungeId(), structure);
        }
    }

    public boolean exists(String id) {
        return structureStates.get(id) == State.FINISHED;
    }

    public Structure get(String id) {
        return structures.get(id);
    }

}
