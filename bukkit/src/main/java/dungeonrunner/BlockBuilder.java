package dungeonrunner;

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

import dungeonrunner.model.*;
import dungeonrunner.system.di.Inject;
import dungeonrunner.system.util.Log;
import dungeonrunner.v2.BuildAction;
import dungeonrunner.v2.BuildMission;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

/**
 * @author Michael Lieshoff
 */
public class BlockBuilder {

    @Inject
    private World world;

    public void buildEntrance(Entrance entrance) {
        StructureInfo structureInfo = entrance.getStructureInfo();
        Point3D start = structureInfo.getStart();

        buildGround(start.getX(), start.getY(), start.getZ(), structureInfo.getWidth(), structureInfo.getHeight(), Material.BRICK);

//        filledRectangle(entrance.getStructureInfo(), Material.BRICK, 1, 0);

//        buildWallWestOst(48, 200, 48, 20, 4, Material.STONE);
//          buildWallWestOst(48 + 20, 200, 48, 20, 4, Material.STONE);

//        buildWallNorthSouth(48, 200, 48, 21, 4, Material.STONE);
//        buildWallNorthSouth(48, 200, 48 + 20, 21, 4, Material.STONE);
    }

    public void buildPlayerLounge(PlayerLounge playerLounge) {
        //filledRectangle(playerLounge.getStructureInfo(), Material.SANDSTONE, playerLounge.getId(), 5);
    }

    private void filledRectangle(StructureInfo structureWorld, Material material, int factor, int gap) {
        int x0 = structureWorld.getStart().getX() * factor + gap;
        int x1 = structureWorld.getEnd().getX() * factor + gap;
        int y0 = structureWorld.getStart().getY();
        int y1 = structureWorld.getEnd().getY();
        int z0 = structureWorld.getStart().getZ() * factor + gap;
        int z1 = structureWorld.getEnd().getZ() * factor + gap;
        for (int y = y0; y < y1; y++) {
            Log.info(this, "filledRectangle", "row: %s", y);
            for (int x = x0; x < x1; x++) {
                for (int z = z0; z < z1; z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    block.setType(material, true);
                }
            }
        }
    }

    public void buildGround(int x0, int y0, int z0, int width, int height, Material material) {
        for (int y = y0; y < y0 + height; y++) {
            for (int x = x0; x < x0 + width; x++) {
                for (int z = z0; z < z0 + width; z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    block.setType(material);
                }
            }
        }
    }

    public void buildWallWestOst(int x0, int y0, int z0, int width, int height, Material material) {
        for (int y = y0; y < y0 + height; y++) {
            for (int z = z0; z < z0 + width; z++) {
                Location location = new Location(world, x0, y, z);
                Block block = location.getBlock();
                block.setType(material);
            }
        }
    }

    public void buildWallNorthSouth(int x0, int y0, int z0, int width, int height, Material material) {
        for (int y = y0; y < y0 + height; y++) {
            for (int x = x0; x < x0 + width; x++) {
                Location location = new Location(world, x, y, z0);
                Block block = location.getBlock();
                block.setType(material);
            }
        }
    }

    public void destroy(PlayerContainer playerContainer) {
        Log.info(this, "destroy", "playerContainer=%s", playerContainer);
    }

    public void reset(Plugin plugin, dungeonrunner.model.World world) {
        Log.info(this, "reset", "starting... plugin=%s", plugin);

        filledRectangle(world.getStructureInfo(), Material.AIR, 1, 0);

        Log.info(this, "reset", "stop");
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void buildArena(Arena arena) {
        int i = arena.getId();

//        filledRectangle(arena.getStructureInfo(), Material.STONE, i, 0);

    }

    public void build(BuildMission buildMission) {
        Log.info(this, "build", "buildMission: %s starting...", buildMission);
        buildMission.consume(new BuildMission.Consumer() {
            @Override
            public void consume(BuildAction buildAction) {
                int x = buildAction.getX();
                int y = buildAction.getY();
                int z = buildAction.getZ();
                Material material = buildAction.getMaterial();
                Location location = new Location(world, x, y, z);
                Block block = location.getBlock();
                block.setType(material);
            }
        });
        Log.info(this, "build", "buildMission: %s FINISHED.", buildMission);
    }

}
