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
import dungeonrunner.model.World;
import dungeonrunner.observer.Engine;
import dungeonrunner.player.Character;
import dungeonrunner.player.CharacterDao;
import dungeonrunner.player.CharacterManager;
import dungeonrunner.system.di.MiniDI;
import dungeonrunner.system.transaction.TransactionContext;
import dungeonrunner.system.util.Log;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Lieshoff
 */
public class DrPlugin extends JavaPlugin implements Listener {

    private Engine engine;

    private static final List<Class<?>> DB_CLASSES = new ArrayList<Class<?>>(){{
        add(Character.class);
    }};

    @Override
    public void onLoad() {
        super.onLoad();
        Log.init(this);
        TransactionContext.init(getDatabase());

        MiniDI.register(EbeanServer.class, getDatabase());
        MiniDI.register(
                CharacterManager.class,
                CharacterDao.class,
                BlockBuilder.class,
                Engine.class,
                World.class
        );

        installDDL();

        engine = MiniDI.get(Engine.class);
        engine.setPlugin(this);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        return DB_CLASSES;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
        engine.start();
    }

    @Override
    public void onDisable() {
        engine.stop();
        super.onDisable();
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        engine.onLogin(event);
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        engine.onLogout(event);
    }

}
