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

import dungeonrunner.BlockBuilder;
import dungeonrunner.system.di.Inject;
import dungeonrunner.system.util.Log;
import dungeonrunner.v2.BuildWorker;

/**
 * @author Michael Lieshoff
 */
public class StructurePopulator {

    @Inject
    private BlockBuilder blockBuilder;

    @Inject
    private StructureHolder structureHolder;

    @Inject
    private BuildWorker buildWorker;

    @Inject
    private Blueprints blueprints;

    public void checkAndPopulate() {
        Log.info(this, "checkAndPopulate", "start");
        if (!structureHolder.exists("GLASSHOUSE")) {
            buildWorker.addStructre(blueprints.createGlasshouse(100, 100, 100));
        }
        int numberOfLounges = structureHolder.getNumberOfLounges();
        if (numberOfLounges < 4) {
            int loungeId = structureHolder.getFreeLoungeId();
            Log.info(this, "checkAndPopulate", "free lounge id: %s", loungeId);
            int x = 100;
            int y = 50;
            int z = 100;
            if (loungeId == 2 || loungeId == 3) {
                z += 25;
            }
            x += 25 * (loungeId % 4);
            buildWorker.addStructre(blueprints.createLounge(loungeId, x, y, z));
        }
        Log.info(this, "checkAndPopulate", "stop");
    }

}
