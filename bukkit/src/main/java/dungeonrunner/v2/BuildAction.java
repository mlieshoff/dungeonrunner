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

import org.bukkit.Material;

/**
 * @author Michael Lieshoff
 */
public class BuildAction {

    private final Material material;

    private final int x;
    private final int y;
    private final int z;

    public BuildAction(Material material, int x, int y, int z) {
        this.material = material;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Material getMaterial() {
        return material;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

}
