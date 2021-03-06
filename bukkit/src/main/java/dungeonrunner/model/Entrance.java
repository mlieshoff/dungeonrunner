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

import dungeonrunner.Point3D;

import java.util.Set;

/**
 * @author Michael Lieshoff
 */
public class Entrance extends Structure {

    private final World world;

    public Entrance(World world) {
        super(1, createStructureInfo(world));
        this.world = world;
    }

    private static StructureInfo createStructureInfo(World world) {
        int width = 4;
        int height = 1;
        Point3D center = world.getStructureInfo().centerPoint();
        return new StructureInfo(
                new Point3D(
                        center.getX() - width / 2,
                        center.getY() - height,
                        center.getZ() - width / 2
                ),
                new Point3D(
                        center.getX() + width / 2,
                        center.getY() + height,
                        center.getZ() + width / 2
                )
        );
    }

    @Override
    public Set<PlayerContainer> destroy() {
        Set<PlayerContainer> set = super.destroy();
        if (count() == 0) {
            set.add(this);
        }
        return set;
    }

    public World getWorld() {
        return world;
    }

}
