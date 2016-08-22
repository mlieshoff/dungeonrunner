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
import dungeonrunner.system.Inject;
import dungeonrunner.system.MiniDI;
import dungeonrunner.system.dao.DaoException;
import dungeonrunner.system.dao.JdbcTemplate;
import dungeonrunner.system.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Michael Lieshoff
 */
public class SchemaVersionManager extends JdbcTemplate {

    @Inject
    private SchemaVersionDao schemaVersionDao;

    public void migrate(boolean dropAndCreate) throws DaoException, IOException {
        MiniDI.get(EbeanServer.class).beginTransaction();
        schemaVersionDao.init(dropAndCreate);
        boolean fileExists = true;
        while(fileExists) {
            int newSchemaVersion = schemaVersionDao.readLastSchemaVersion() + 1;
            String filename = String.format("/migration_%s.sql", newSchemaVersion);
            Log.info(this, "migrate", "search migration: %s", filename);
            // load script
            InputStream inputStream = getClass().getResourceAsStream(filename);
            fileExists = inputStream != null;
            if (fileExists) {
                Log.warn(this, "migrate", "execute script for: %s", filename);
                String script = IOUtils.toString(inputStream);
                schemaVersionDao.executeScript(script);
                schemaVersionDao.setLastSchemaVersion(newSchemaVersion);
            } else {
                Log.warn(this, "migrate", "migration not found: %s", filename);
            }
        }
        MiniDI.get(EbeanServer.class).endTransaction();
    }

}
