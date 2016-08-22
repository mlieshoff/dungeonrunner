package dungeonrunner.system.dao;

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
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;
import dungeonrunner.system.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Lieshoff
 */
public class JdbcTemplate {

    private Map<String, SqlQuery> queryCache = new ConcurrentHashMap<>();
    private Map<String, SqlUpdate> updateCache = new ConcurrentHashMap<>();

    protected <T> List<T> query(EbeanServer ebeanServer, RowTransformer<T> rowTransformer, String sql, Object... objects) throws SQLException {
        sql = normalize(sql);
        Log.info(this, "query", ebeanServer.getName() + " - query: " + sql);
        SqlQuery sqlUpdate = queryCache.get(sql);
        if (sqlUpdate == null) {
            sqlUpdate = ebeanServer.createSqlQuery(sql);
            queryCache.put(sql, sqlUpdate);
        }
        fillWithObjects(sqlUpdate, objects);
        List<T> list = new ArrayList<>();
        List<SqlRow> result = sqlUpdate.findList();
        for (SqlRow sqlRow : result) {
            Object value = rowTransformer.transform(sqlRow);
            if (value != null) {
                list.add(rowTransformer.transform(sqlRow));
            }
        }
        Log.info(this, "query", (ebeanServer.getName() + " - size: " + list.size()));
        return list;
    }

    private void fillWithObjects(SqlQuery preparedStatement, Object[] objects) throws SQLException {
        if (objects != null) {
            for (int i = 0; i < objects.length; i ++) {
                Object o = objects[i];
                if (o instanceof String) {
                    o = o.toString().trim();
                }
                preparedStatement.setParameter(i + 1, o);
                Log.info(this, "fillWithObjects", "    " + (i + 1) + " = " + o);
            }
        }
    }

    private void fillWithObjects(SqlUpdate preparedStatement, Object[] objects) throws SQLException {
        if (objects != null) {
            for (int i = 0; i < objects.length; i ++) {
                Object o = objects[i];
                if (o instanceof String) {
                    o = o.toString().trim();
                }
                preparedStatement.setParameter(i + 1, o);
                Log.info(this, "fillWithObjects", "    " + (i + 1) + " = " + o);
            }
        }
    }

    protected <T> T querySingle(EbeanServer ebeanServer, RowTransformer<T> rowTransformer, String sql, Object... objects) throws SQLException {
        List<T> list = query(ebeanServer, rowTransformer, sql, objects);
        int size = list.size();
        if (size == 0) {
            return null;
        } else if (size == 1) {
            return list.get(0);
        } else {
            throw new IllegalStateException("more than 1 result found[" + size + "]!");
        }
    }

    private String normalize(String sql) {
        sql = sql.replace("\r\n", "").replace("\n", "");
        sql = sql.replaceAll("\\s\\s+"," ");
        return sql;
    }

    protected int update(EbeanServer ebeanServer, String sql, Object... objects) throws SQLException {
        sql = normalize(sql);
        Log.info(this, "update", ebeanServer + " - update: " + sql);
        SqlUpdate sqlUpdate = updateCache.get(sql);
        if (sqlUpdate == null) {
            sqlUpdate = ebeanServer.createSqlUpdate(sql);
            updateCache.put(sql, sqlUpdate);
        }
        fillWithObjects(sqlUpdate, objects);
        return sqlUpdate.execute();
    }

    protected int execute(EbeanServer ebeanServer, String sql, Object... objects) throws SQLException {
        sql = normalize(sql);
        Log.info(this, "execute", ebeanServer + " - execute: " + sql);
        SqlUpdate sqlUpdate = updateCache.get(sql);
        if (sqlUpdate == null) {
            sqlUpdate = ebeanServer.createSqlUpdate(sql);
            updateCache.put(sql, sqlUpdate);
        }
        if (objects != null) {
            for (int i = 0; i < objects.length; i ++) {
                Object o = objects[i];
                if (o instanceof String) {
                    o = o.toString().trim();
                }
                sqlUpdate.setParameter(i + 1, o);
            }
        }
        return sqlUpdate.execute();
    }

}