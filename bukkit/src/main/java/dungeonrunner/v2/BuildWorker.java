package dungeonrunner.v2;/*
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
import dungeonrunner.system.di.Inject;
import dungeonrunner.system.util.Log;
import dungeonrunner.v2.structures.Structure;
import dungeonrunner.v2.structures.StructureHolder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Michael Lieshoff
 */
public class BuildWorker {

    @Inject
    private BlockBuilder blockBuilder;

    @Inject
    private StructureHolder structureHolder;

    private Queue<Structure> structuresToBuild = new ConcurrentLinkedQueue<>();

    public void build() {
        Log.info(this, "build", "start");
        Structure structure = structuresToBuild.poll();
        if (structure != null) {
            structureHolder.request(structure);
            blockBuilder.build(structure.createBuildMission());
            structureHolder.register(structure);
        }
        Log.info(this, "build", "stop");
    }

    public void addStructre(Structure structure) {
        structuresToBuild.add(structure);
    }

}
