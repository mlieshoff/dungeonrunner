package dungeonrunner.entrance;

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

import dungeonrunner.location.LocationDao;
import dungeonrunner.location.LocationType;
import dungeonrunner.system.Inject;
import dungeonrunner.system.dao.DaoException;
import org.bukkit.entity.Player;

/**
 * @author Michael Lieshoff
 */
public class EntranceManager {

    @Inject
    private LocationDao locationDao;

    public void enter(Player player) {

    }

    public boolean exists() throws DaoException {
        return locationDao.exists(LocationType.ENTRANCE);
    }

    public void create() throws DaoException {
        locationDao.create(LocationType.ENTRANCE, 1);
    }

}
