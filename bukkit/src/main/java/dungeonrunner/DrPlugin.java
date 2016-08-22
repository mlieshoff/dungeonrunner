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
import dungeonrunner.arena.ArenaDao;
import dungeonrunner.arena.ArenaManager;
import dungeonrunner.entrance.EntranceDao;
import dungeonrunner.entrance.EntranceManager;
import dungeonrunner.migration.SchemaVersionDao;
import dungeonrunner.migration.SchemaVersionManager;
import dungeonrunner.observer.Observer;
import dungeonrunner.player.PlayerDao;
import dungeonrunner.player.PlayerManager;
import dungeonrunner.system.dao.DaoException;
import dungeonrunner.system.util.Log;
import dungeonrunner.system.MiniDI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Michael Lieshoff
 */
public class DrPlugin extends JavaPlugin implements Listener {

    private Observer observer;

    private PlayerManager playerManager;

    @Override
    public void onLoad() {
        super.onLoad();
        Log.init(this);
        MiniDI.register(EbeanServer.class, getDatabase());
        MiniDI.register(
                ArenaManager.class,
                ArenaDao.class,
                EntranceManager.class,
                EntranceDao.class,
                Observer.class,
                PlayerManager.class,
                PlayerDao.class,
                SchemaVersionManager.class,
                SchemaVersionDao.class
        );

        try {
            MiniDI.get(SchemaVersionManager.class).migrate(true);
        } catch (Exception e) {
            Log.error(this, "onLoad", "error while migrating", e);
            throw new IllegalStateException(e);
        }

        observer = MiniDI.get(Observer.class);
        playerManager = MiniDI.get(PlayerManager.class);

    }

    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
        observer.start();
    }

    @Override
    public void onDisable() {
        observer.stop();
        super.onDisable();
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Log.info(this, "onLogin", "player=%s", player.getName());

        try {
            playerManager.login(player);
        } catch (DaoException e) {
            e.printStackTrace();
        }

        observer.enterEntrance(event.getPlayer());
    }

}