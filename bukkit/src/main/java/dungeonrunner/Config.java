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

import dungeonrunner.model.StructureInfo;

/**
 * @author Michael Lieshoff
 */
public class Config {

    public static final long CLEAN_TIMEOUT = 5000;

    public static final int MAX_ARENAS = 1;
    public static final int MAX_PLAYER_LOUNGES_PER_ARENA = 4;
    public static final int MAX_ADMIN_LOUNGES_PER_ARENA = 1;
    public static final int MAX_VAULTS_PER_ARENA = 4;
    public static final int MAX_DUNGEONS_PER_VAULT = 4;

    public static final StructureInfo STRUCTURE_WORLD = new StructureInfo(
            new Point3D(-100, -64, -100),
            new Point3D(100, 319, 100));

    public static final StructureInfo STRUCTURE_ENTRANCE = new StructureInfo(
            new Point3D(45, 200, 45),
            new Point3D(55, 201, 55));

    public static final StructureInfo STRUCTURE_PLAYER_LOUNGE = new StructureInfo(
            new Point3D(45, 20, 45),
            new Point3D(55, 21, 55));

    public static final StructureInfo STRUCTURE_ARENA = new StructureInfo(
            new Point3D(-100, 10, -100),
            new Point3D(-50, 14, -50));

}
