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

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlRow;
import dungeonrunner.system.Inject;
import dungeonrunner.system.dao.AbstractDao;
import dungeonrunner.system.dao.DaoException;
import dungeonrunner.system.dao.RowTransformer;
import dungeonrunner.system.util.Lambda;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Michael Lieshoff
 */
public class ArenaDao extends AbstractDao {

    public static final String TABLE = "arena";

    @Inject
    private EbeanServer ebeanServer;

    public int readLastSchemaVersion() throws DaoException {
        return doInDao(new Lambda<Integer>() {
            @Override
            public Integer exec(Object... params) throws SQLException {
                List<Integer> list = query(ebeanServer, new RowTransformer<Integer>() {
                    @Override
                    public Integer transform(SqlRow sqlRow) throws SQLException {
                        return sqlRow.getInteger("max(version)");
                    }
                }, "SELECT MAX(version) FROM " + TABLE);
                if (list.size() > 0) {
                    return list.get(0);
                }
                return 0;
            }});
    }

    public void setLastSchemaVersion(final int lastSchemaVersion) throws DaoException {
        doInDao(new Lambda<Void>() {
            @Override
            public Void exec(Object... params) throws SQLException {
                update(ebeanServer, "INSERT INTO schemaversion (version) VALUES(?);", lastSchemaVersion);
                return null;
            }});
    }

}
