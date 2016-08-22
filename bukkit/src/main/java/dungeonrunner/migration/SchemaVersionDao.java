package dungeonrunner.migration;

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
import dungeonrunner.system.util.Log;

import javax.persistence.PersistenceException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Michael Lieshoff
 */
public class SchemaVersionDao extends AbstractDao {

    public static final String TABLE = "schemaversion";

    @Inject
    private EbeanServer ebeanServer;

    public void init(final boolean dropAndCreate) throws DaoException {
        doInDao(new Lambda<Void>() {
            @Override
            public Void exec(Object... params) throws SQLException {
                boolean mustUpdate = false;
                try {
                    querySingle(ebeanServer, new RowTransformer<Object>() {
                        @Override
                        public Object transform(SqlRow sqlRow) throws SQLException {
                            return null;
                        }
                    }, "SELECT MAX(id) FROM " + TABLE);
                } catch (PersistenceException e) {
                    mustUpdate = true;
                }
                if (mustUpdate || dropAndCreate) {
                    int i = update(ebeanServer, "DROP TABLE IF EXISTS schemaversion;");
                    Log.info(this, "init", "drop table: %s", i);
                    i = update(ebeanServer, "CREATE TABLE schemaversion("
                            + "id INTEGER, "
                            + "version INTEGER NOT NULL, "
                            + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                            + "PRIMARY KEY (id))");
                    Log.info(this, "init", "create table: %s", i);
                }
                return null;
            }});
    }

    public void executeScript(final String script) throws DaoException {
        doInDao(new Lambda<Void>() {
            @Override
            public Void exec(Object... params) throws SQLException {
                String[] split = script.split("[;]");
                for (String sql : split) {
                    if (sql.length() > 2 && !sql.startsWith("--")) {
                        execute(ebeanServer, sql);
                    }
                }
                return null;
            }});
    }

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
