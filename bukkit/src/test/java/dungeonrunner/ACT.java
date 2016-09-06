package dungeonrunner;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.MockGateway;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Michael Lieshoff
 */
public class ACT {

    public static final File pluginDirectory = new File("bin/test/server/plugins/coretest");
    public static final File serverDirectory = new File("bin/test/server");
    public static final File worldsDirectory = new File("bin/test/server");

    private DrPlugin drPlugin;

    private Server mockServer;
    private CommandSender commandSender;

    @Before
    public void setUp() throws IOException, InvalidDescriptionException {
        pluginDirectory.mkdirs();

        TestPluginLoader pluginLoader = new TestPluginLoader();

        mockServer = mock(Server.class);
        when(mockServer.getName()).thenReturn("TestBukkit");
        Logger.getLogger("Minecraft").setParent(Util.logger);
        when(mockServer.getLogger()).thenReturn(Util.logger);
        when(mockServer.getWorldContainer()).thenReturn(worldsDirectory);

        PluginDescriptionFile pdf = new PluginDescriptionFile(DrPlugin.class.getClassLoader().getResourceAsStream("plugin.yml"));

        drPlugin = PowerMockito.spy(new DrPlugin(pluginLoader, mockServer, pdf, pluginDirectory, new File(pluginDirectory, "testPluginFile")));

        PluginManager mockPluginManager = PowerMockito.mock(PluginManager.class);
        when(mockPluginManager.getPlugins()).thenReturn(new Plugin[]{drPlugin});
        when(mockPluginManager.getPlugin("Multiverse-Core")).thenReturn(drPlugin);
        when(mockPluginManager.getPermission(anyString())).thenReturn(null);

        when(mockServer.getPluginManager()).thenReturn(mockPluginManager);

        drPlugin.setServerFolder(serverDirectory);

        drPlugin.onLoad();
        drPlugin.onEnable();

    }

    @After
    public void tearDown() {
        try {
            Field serverField = Bukkit.class.getDeclaredField("server");
            serverField.setAccessible(true);
            serverField.set(Class.forName("org.bukkit.Bukkit"), null);
        } catch (Exception e) {
            Util.log(Level.SEVERE,
                    "Error while trying to unregister the server from Bukkit. Has Bukkit changed?");
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        drPlugin.onDisable();

        try {
            FileUtils.deleteDirectory(serverDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUp2() {
        try {
            pluginDirectory.mkdirs();
            Assert.assertTrue(pluginDirectory.exists());

            MockGateway.MOCK_STANDARD_METHODS = false;

            TestPluginLoader pluginLoader = new TestPluginLoader();

            // Initialize the Mock server.
            mockServer = mock(Server.class);
            when(mockServer.getName()).thenReturn("TestBukkit");
            Logger.getLogger("Minecraft").setParent(Util.logger);
            when(mockServer.getLogger()).thenReturn(Util.logger);
            when(mockServer.getWorldContainer()).thenReturn(worldsDirectory);

            // Return a fake PDF file.
            PluginDescriptionFile pdf = new PluginDescriptionFile("Multiverse-Core", "2.2-Test", "com.onarandombox.MultiverseCore.MultiverseCore");

            drPlugin = PowerMockito.spy(new DrPlugin(pluginLoader, mockServer, pdf, pluginDirectory, new File(pluginDirectory, "testPluginFile")));

            // Let's let all MV files go to bin/test
//            when(drPlugin.getDataFolder()).thenReturn(pluginDirectory);
            when(drPlugin.isEnabled()).thenReturn(true);
            when(drPlugin.getLogger()).thenReturn(Util.logger);

            drPlugin.setServerFolder(serverDirectory);

            // Add Core to the list of loaded plugins
            JavaPlugin[] plugins = new JavaPlugin[] { drPlugin };

            // Mock the Plugin Manager
            PluginManager mockPluginManager = PowerMockito.mock(PluginManager.class);
            when(mockPluginManager.getPlugins()).thenReturn(plugins);
            when(mockPluginManager.getPlugin("Multiverse-Core")).thenReturn(drPlugin);
            when(mockPluginManager.getPermission(anyString())).thenReturn(null);
            // Tell Buscript Vault is not available.
            when(mockPluginManager.getPermission("Vault")).thenReturn(null);

            // Make some fake folders to fool the fake MV into thinking these worlds exist
            File worldNormalFile = new File(drPlugin.getServerFolder(), "world");
            Util.log("Creating world-folder: " + worldNormalFile.getAbsolutePath());
            worldNormalFile.mkdirs();
            File worldNetherFile = new File(drPlugin.getServerFolder(), "world_nether");
            Util.log("Creating world-folder: " + worldNetherFile.getAbsolutePath());
            worldNetherFile.mkdirs();
            File worldSkylandsFile = new File(drPlugin.getServerFolder(), "world_the_end");
            Util.log("Creating world-folder: " + worldSkylandsFile.getAbsolutePath());
            worldSkylandsFile.mkdirs();



            // Give the server some worlds
            when(mockServer.getWorld(anyString())).thenAnswer(new Answer<World>() {
                @Override
                public World answer(InvocationOnMock invocation) throws Throwable {
                    String arg;
                    try {
                        arg = (String) invocation.getArguments()[0];
                    } catch (Exception e) {
                        return null;
                    }
                    return MockWorldFactory.getWorld(arg);
                }
            });

            when(mockServer.getWorld(any(UUID.class))).thenAnswer(new Answer<World>() {
                @Override
                public World answer(InvocationOnMock invocation) throws Throwable {
                    UUID arg;
                    try {
                        arg = (UUID) invocation.getArguments()[0];
                    } catch (Exception e) {
                        return null;
                    }
                    return MockWorldFactory.getWorld(arg);
                }
            });

            when(mockServer.getWorlds()).thenAnswer(new Answer<List<World>>() {
                @Override
                public List<World> answer(InvocationOnMock invocation) throws Throwable {
                    return MockWorldFactory.getWorlds();
                }
            });

            when(mockServer.getPluginManager()).thenReturn(mockPluginManager);

            when(mockServer.createWorld(Matchers.isA(WorldCreator.class))).thenAnswer(
                    new Answer<World>() {
                        @Override
                        public World answer(InvocationOnMock invocation) throws Throwable {
                            WorldCreator arg;
                            try {
                                arg = (WorldCreator) invocation.getArguments()[0];
                            } catch (Exception e) {
                                return null;
                            }
                            // Add special case for creating null worlds.
                            // Not sure I like doing it this way, but this is a special case
                            if (arg.name().equalsIgnoreCase("nullworld")) {
                                return MockWorldFactory.makeNewNullMockWorld(arg.name(), arg.environment(), arg.type());
                            }
                            return MockWorldFactory.makeNewMockWorld(arg.name(), arg.environment(), arg.type());
                        }
                    });

            when(mockServer.unloadWorld(anyString(), anyBoolean())).thenReturn(true);

            // add mock scheduler
            BukkitScheduler mockScheduler = mock(BukkitScheduler.class);
            when(mockScheduler.scheduleSyncDelayedTask(any(Plugin.class), any(Runnable.class), anyLong())).
                    thenAnswer(new Answer<Integer>() {
                        @Override
                        public Integer answer(InvocationOnMock invocation) throws Throwable {
                            Runnable arg;
                            try {
                                arg = (Runnable) invocation.getArguments()[1];
                            } catch (Exception e) {
                                return null;
                            }
                            arg.run();
                            return null;
                        }});
            when(mockScheduler.scheduleSyncDelayedTask(any(Plugin.class), any(Runnable.class))).
                    thenAnswer(new Answer<Integer>() {
                        @Override
                        public Integer answer(InvocationOnMock invocation) throws Throwable {
                            Runnable arg;
                            try {
                                arg = (Runnable) invocation.getArguments()[1];
                            } catch (Exception e) {
                                return null;
                            }
                            arg.run();
                            return null;
                        }});
            when(mockServer.getScheduler()).thenReturn(mockScheduler);

            // Set server
            Field serverfield = JavaPlugin.class.getDeclaredField("server");
            serverfield.setAccessible(true);
            serverfield.set(drPlugin, mockServer);

            // Init our command sender
            final Logger commandSenderLogger = Logger.getLogger("CommandSender");
            commandSenderLogger.setParent(Util.logger);
            commandSender = mock(CommandSender.class);
            doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    commandSenderLogger.info(ChatColor.stripColor((String) invocation.getArguments()[0]));
                    return null;
                }}).when(commandSender).sendMessage(anyString());
            when(commandSender.getServer()).thenReturn(mockServer);
            when(commandSender.getName()).thenReturn("MockCommandSender");
            when(commandSender.isPermissionSet(anyString())).thenReturn(true);
            when(commandSender.isPermissionSet(Matchers.isA(Permission.class))).thenReturn(true);
            when(commandSender.hasPermission(anyString())).thenReturn(true);
            when(commandSender.hasPermission(Matchers.isA(Permission.class))).thenReturn(true);
            when(commandSender.addAttachment(drPlugin)).thenReturn(null);
            when(commandSender.isOp()).thenReturn(true);

            Bukkit.setServer(mockServer);

            // Load Multiverse Core
            drPlugin.onLoad();

            // Enable it.
            drPlugin.onEnable();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {

    }

}
