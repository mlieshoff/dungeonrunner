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

import dungeonrunner.model.Entrance;
import dungeonrunner.model.PlayerContainer;
import dungeonrunner.system.util.Log;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

/**
 * @author Michael Lieshoff
 */
public class BlockBuilder {

    public void buildEntrance(Plugin plugin, Entrance entrance) {
    }

    public void destroy(PlayerContainer playerContainer) {
        Log.info(this, "destroy", "playerContainer=%s", playerContainer);
    }

    public void reset(Plugin plugin) {
        Log.info(this, "reset", "starting... plugin=%s", plugin);
        World world = plugin.getServer().getWorld("world");

        for (int x = 0; x < 100; x ++) {
            for (int y = 0; y < 100; y ++) {
                for (int z = 0; z < 100; z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    block.setType(Material.WATER);
                }
            }
        }

        Log.info(this, "reset", "stop");
    }

}
