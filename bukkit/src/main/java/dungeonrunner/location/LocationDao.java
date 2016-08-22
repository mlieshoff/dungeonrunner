package dungeonrunner.location;

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

import com.avaje.ebean.EbeanServer;
import dungeonrunner.player.DungeonRunner;
import dungeonrunner.system.Inject;
import dungeonrunner.system.dao.AbstractDao;
import dungeonrunner.system.dao.DaoException;
import dungeonrunner.system.util.Lambda;

/**
 * @author Michael Lieshoff
 */
public class LocationDao extends AbstractDao {

    @Inject
    private EbeanServer ebeanServer;

    public int count(final LocationType locationType) throws DaoException {
        return doInDao(ebeanServer, new Lambda<Integer>() {
            @Override
            public Integer exec(Object... params) throws Exception {
                return ebeanServer
                        .createQuery(Location.class)
                        .where("type=:type")
                        .setParameter("type", locationType.getCode())
                        .findRowCount();
            }
        });
    }

    public Location find(final LocationType locationType, final int id) throws DaoException {
        return doInDao(ebeanServer, new Lambda<Location>() {
            @Override
            public Location exec(Object... params) throws Exception {
                return ebeanServer
                        .createQuery(Location.class)
                        .where("type=:type and id=:id")
                        .setParameter("type", locationType.getCode())
                        .setParameter("id", id)
                        .findUnique();
            }
        });
    }

    public Location create(final LocationType locationType, final int id) throws DaoException {
        return doInDao(ebeanServer, new Lambda<Location>() {
            @Override
            public Location exec(Object... params) throws Exception {
                Location location = new Location();
                location.setType(locationType.getCode());
                location.setId(id);
                ebeanServer.insert(location);
                return location;
            }
        });
    }

    public void assign(final DungeonRunner dungeonRunner, final Location location) throws DaoException {
        doInDao(ebeanServer, new Lambda<Void>() {
            @Override
            public Void exec(Object... params) throws Exception {
                PlayerLocation playerLocation = new PlayerLocation();
                playerLocation.setType(location.getType());
                playerLocation.setLocation(location.getId());
                playerLocation.setPlayer(dungeonRunner.getUuid());
                ebeanServer.insert(playerLocation);
                return null;
            }
        });
    }

}
