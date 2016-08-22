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
import com.avaje.ebean.SqlRow;
import dungeonrunner.system.Inject;
import dungeonrunner.system.dao.AbstractDao;
import dungeonrunner.system.dao.DaoException;
import dungeonrunner.system.dao.RowTransformer;
import dungeonrunner.system.util.Lambda;

import java.sql.SQLException;

/**
 * @author Michael Lieshoff
 */
public class LocationDao extends AbstractDao {

    public static final String TABLE = "location";

    @Inject
    private EbeanServer ebeanServer;

    public boolean exists(final LocationType locationType) throws DaoException {
        return doInDao(new Lambda<Boolean>() {
            @Override
            public Boolean exec(Object... params) throws SQLException {
                return querySingle(ebeanServer, new RowTransformer<Boolean>() {
                    @Override
                    public Boolean transform(SqlRow sqlRow) throws SQLException {
                        return sqlRow.getInteger("count(*)") > 0;
                    }
                }, "SELECT count(*) FROM " + TABLE + " where type = ?", locationType.getCode());
            }});
    }

    public boolean create(final LocationType locationType, final int id) throws DaoException {
        return doInDao(new Lambda<Boolean>() {
            @Override
            public Boolean exec(Object... params) throws SQLException {
                return update(ebeanServer, "insert into location (id, type) values(?, ?)", id, locationType.getCode()) == 1;
            }});
    }

}
