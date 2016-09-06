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
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Lieshoff
 */
public class DrPlugin extends JavaPlugin implements Listener {

    private static final List<Class<?>> DB_CLASSES = new ArrayList<Class<?>>(){{
        add(Character.class);
    }};

    private Engine engine;

    private org.bukkit.World world;

    private File serverFolder;

    public DrPlugin(PluginLoader pluginLoader, Server mockServer, PluginDescriptionFile pdf, File pluginDirectory, File pluginFile) {
        super(pluginLoader, mockServer, pdf, pluginDirectory, pluginFile);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Log.init(this);
        TransactionContext.init(getDatabase());

        MiniDI.register(EbeanServer.class, getDatabase());
        MiniDI.register(
                BlockBuilder.class,
                CharacterManager.class,
                CharacterDao.class,
                Engine.class,
                Teleporter.class,
                World.class
        );

        try {
            installDDL();
            Log.info(this, "onLoad", "database created.");
        } catch (Exception e) {
            Log.info(this, "onLoad", "database exists...");
        }

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
        world = getServer().getWorld("world");
        getServer().getPluginManager().registerEvents(this, this);
        MiniDI.get(BlockBuilder.class).setWorld(world);
        MiniDI.get(Teleporter.class).setWorld(world);
        engine.start();
    }

    @Override
    public void onDisable() {
        engine.stop();
        super.onDisable();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        engine.onJoin(event);
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        engine.onLogout(event);
    }

    public void setServerFolder(File serverFolder) {
        this.serverFolder = serverFolder;
    }

    public File getServerFolder() {
        return serverFolder;
    }

}
