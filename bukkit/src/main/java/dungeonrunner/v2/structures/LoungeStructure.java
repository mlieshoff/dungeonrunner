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

import dungeonrunner.Point3D;
import dungeonrunner.v2.BuildMission;
import org.bukkit.Material;

/**
 * @author Michael Lieshoff
 */
public class LoungeStructure extends Structure {

    public LoungeStructure(Point3D start, int id) {
        super("LOUNGE_" + id, start, new Point3D(start.getX() + 5, start.getY() + 5, start.getZ() + 5));
    }

    @Override
    public BuildMission createBuildMission() {
        BuildMission buildMission = new BuildMission();
        int x0 = getStart().getX();
        int y0 = getStart().getY();
        int z0 = getStart().getZ();
        int x1 = getEnd().getX();
        int y1 = getEnd().getY();
        int z1 = getEnd().getZ();
        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                for (int z = z0; z < z1; z++) {
                    buildMission.mutation(Material.GLASS, x, y, z);
                }
            }
        }
        x1 = getEnd().getX() - 1;
        y1 = getEnd().getY() - 1;
        z1 = getEnd().getZ() - 1;
        for (int y = y0 + 1; y < y1; y++) {
            for (int x = x0 + 1; x < x1; x++) {
                for (int z = z0 + 1; z < z1; z++) {
                    buildMission.mutation(Material.AIR, x, y, z);
                }
            }
        }
        return buildMission;
    }

}
