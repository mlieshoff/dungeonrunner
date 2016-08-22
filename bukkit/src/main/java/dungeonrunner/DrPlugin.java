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
import dungeonrunner.arena.ArenaManager;
import dungeonrunner.entrance.EntranceManager;
import dungeonrunner.location.Location;
import dungeonrunner.location.LocationDao;
import dungeonrunner.location.PlayerLocation;
import dungeonrunner.observer.Observer;
import dungeonrunner.player.DungeonRunner;
import dungeonrunner.player.PlayerDao;
import dungeonrunner.player.PlayerManager;
import dungeonrunner.system.MiniDI;
import dungeonrunner.system.dao.DaoException;
import dungeonrunner.system.util.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Lieshoff
 */
public class DrPlugin extends JavaPlugin implements Listener {

    private Observer observer;

    private PlayerManager playerManager;

    private EntranceManager entranceManager;

    private static final List<Class<?>> DB_CLASSES = new ArrayList<Class<?>>(){{
        add(DungeonRunner.class);
        add(Location.class);
        add(PlayerLocation.class);
    }};

    @Override
    public void onLoad() {
        super.onLoad();
        Log.init(this);

        MiniDI.register(EbeanServer.class, getDatabase());
        MiniDI.register(
                ArenaManager.class,
                EntranceManager.class,
                LocationDao.class,
                Observer.class,
                PlayerDao.class,
                PlayerManager.class
        );

        installDDL();

/*        for (Class<?> clazz : DB_CLASSES) {
            try {
                getDatabase().find(clazz);
            } catch (PersistenceException e) {
                installDDL();
                break;
            }
        }
        */

        observer = MiniDI.get(Observer.class);
        playerManager = MiniDI.get(PlayerManager.class);
        entranceManager = MiniDI.get(EntranceManager.class);

        try {
            Location entrance = entranceManager.find();
            if (entrance == null) {
                entranceManager.create();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        return DB_CLASSES;
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
            DungeonRunner dungeonRunner = playerManager.login(player);
            observer.enterEntrance(dungeonRunner);
        } catch (DaoException e) {
            e.printStackTrace();
        }

    }

}
