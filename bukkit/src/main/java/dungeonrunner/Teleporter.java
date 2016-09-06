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

import dungeonrunner.model.Structure;
import dungeonrunner.system.util.Log;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * @author Michael Lieshoff
 */
public class Teleporter {

    private World world;

    public void teleportPlayer(Player player, Structure structure) {
        Location target = structure.getStructureInfo().centerLocation(world);
        target.setY(target.getBlockY() + 4);
        Log.info(this, "teleportPlayer", "teleport to: %s", target);
        player.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN);
        Log.info(this, "teleportPlayer", "teleported");
    }

    public void setWorld(World world) {
        this.world = world;
    }

}
