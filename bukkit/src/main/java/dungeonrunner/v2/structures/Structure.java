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
import org.bukkit.Location;

/**
 * @author Michael Lieshoff
 */
public abstract class Structure {

    private final String id;
    private final Point3D start;
    private final Point3D end;

    public Structure(String id, Point3D start, Point3D end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public Point3D getStart() {
        return start;
    }

    public Point3D getEnd() {
        return end;
    }

    public int getHeight() {
        return Math.abs(start.getY() - end.getY());
    }

    public int getWidth() {
        return Math.abs(start.getZ() - end.getZ());
    }

    public Location centerLocation(org.bukkit.World world) {
        Point3D center = centerPoint();
        return new Location(world, center.getX(), start.getY(), center.getZ());
    }

    public Point3D centerPoint() {
        return start.center(end);
    }

    public abstract BuildMission createBuildMission();

}
