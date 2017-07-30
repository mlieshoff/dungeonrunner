package dungeonrunner.v2;

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

import dungeonrunner.BlockBuilder;
import dungeonrunner.system.di.Inject;
import dungeonrunner.system.di.MiniDI;
import dungeonrunner.system.util.Log;
import dungeonrunner.v2.structures.Blueprints;
import dungeonrunner.v2.structures.StructureHolder;
import dungeonrunner.v2.structures.StructurePopulator;
import org.bukkit.Server;
import org.bukkit.World;
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
    }};

    private File serverFolder;

    @Inject
    private BuildWorker buildWorker;

    @Inject
    private StructurePopulator structurePopulator;

    @Inject
    private StructureHolder structureHolder;

    @Inject
    private Teleporter teleporter;

    public DrPlugin() {
        //
    }

    public DrPlugin(PluginLoader pluginLoader, Server mockServer, PluginDescriptionFile pdf, File pluginDirectory, File pluginFile) {
        super(pluginLoader, mockServer, pdf, pluginDirectory, pluginFile);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Log.init(this);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        return DB_CLASSES;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);

        MiniDI.register(World.class, getServer().getWorld("world"));
        MiniDI.register(
                Teleporter.class,
                Blueprints.class,
                BlockBuilder.class,
                BuildWorker.class,
                StructurePopulator.class,
                StructureHolder.class
        );
        MiniDI.register(DrPlugin.class, this);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                buildWorker.build();
            }
        }, 20, 20);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                structurePopulator.checkAndPopulate();
            }
        }, 20, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Entering glass house...");
        if (!structureHolder.exists("GLASSHOUSE")) {
            for (;;) {
                if (structureHolder.exists("GLASSHOUSE")) {
                    break;
                }
                event.getPlayer().sendMessage("Please wait for glass house...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        teleporter.teleportPlayer(event.getPlayer(), structureHolder.get("GLASSHOUSE"));
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {

    }

    public void setServerFolder(File serverFolder) {
        this.serverFolder = serverFolder;
    }

    public File getServerFolder() {
        return serverFolder;
    }

}
