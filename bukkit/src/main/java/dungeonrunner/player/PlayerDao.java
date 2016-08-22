package dungeonrunner.player;

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
import dungeonrunner.system.Inject;
import dungeonrunner.system.dao.AbstractDao;
import dungeonrunner.system.dao.DaoException;
import dungeonrunner.system.util.Lambda;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Michael Lieshoff
 */
public class PlayerDao extends AbstractDao {

    @Inject
    private EbeanServer ebeanServer;

    public DungeonRunner find(final UUID uuid) throws DaoException {
        return doInDao(ebeanServer, new Lambda<DungeonRunner>() {
            @Override
            public DungeonRunner exec(Object... params) throws Exception {
                return ebeanServer
                        .createQuery(DungeonRunner.class)
                        .where("uuid=:uuid")
                        .setParameter("uuid", uuid.toString())
                        .findUnique();
            }
        });
    }

    public DungeonRunner register(final Player player) throws DaoException {
        return doInDao(ebeanServer, new Lambda<DungeonRunner>() {
            @Override
            public DungeonRunner exec(Object... params) throws Exception {
                DungeonRunner dungeonRunner = new DungeonRunner();
                dungeonRunner.setUuid(player.getUniqueId().toString());
                ebeanServer.save(dungeonRunner);
                return dungeonRunner;
            }
        });
    }

}
