package net.gahvila.gahvilacore;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.gahvila.gahvilacore.AFK.AfkCommand;
import net.gahvila.gahvilacore.AFK.AfkEvents;
import net.gahvila.gahvilacore.AFK.AfkManager;
import net.gahvila.gahvilacore.Config.ConfigManager;
import net.gahvila.gahvilacore.Core.CoreCommand;
import net.gahvila.gahvilacore.Essentials.Commands.*;
import net.gahvila.gahvilacore.Panilla.Panilla;
import net.gahvila.gahvilacore.Music.MusicCommand;
import net.gahvila.gahvilacore.Music.MusicEvents;
import net.gahvila.gahvilacore.Music.MusicManager;
import net.gahvila.gahvilacore.Music.MusicMenu;
import net.gahvila.gahvilacore.Placeholder.Placeholders;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeCommand;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeListener;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeManager;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.PrefixManager;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixColorMenu;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixMainMenu;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixTypeMenu;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.PrefixCommand;
import net.gahvila.gahvilacore.Teleport.Spawn.SpawnCommand;
import net.gahvila.gahvilacore.Teleport.Spawn.SpawnTeleport;
import net.gahvila.gahvilacore.Teleport.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class GahvilaCore extends JavaPlugin {
    public static GahvilaCore instance;
    private PrefixManager prefixManager;
    private PrefixTypeMenu prefixTypeMenu;
    private PrefixMainMenu prefixMainMenu;
    private PrefixColorMenu prefixColorMenu;
    private PlaytimeManager playtimeManager;
    private AfkManager afkManager;
    private PluginManager pluginManager;
    private TeleportManager teleportManager;
    private Panilla panilla;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        instance = this;
        pluginManager = Bukkit.getPluginManager();
        prefixManager = new PrefixManager();
        prefixTypeMenu = new PrefixTypeMenu(prefixManager);
        prefixMainMenu = new PrefixMainMenu();
        prefixColorMenu = new PrefixColorMenu(prefixManager);
        afkManager = new AfkManager();
        playtimeManager = new PlaytimeManager(Optional.of(afkManager));
        teleportManager = new TeleportManager();

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(false).silentLogs(true));

        //panilla
        initializePanilla();

        //afk
        AfkCommand afkCommand = new AfkCommand(afkManager);
        afkCommand.registerCommands();
        registerListeners(new AfkEvents(afkManager));
        afkManager.startAfkScheduler();

        //music
        MusicManager musicManager = new MusicManager();
        MusicMenu musicMenu = new MusicMenu(musicManager);
        musicManager.loadSongs(executionTime -> {
            this.getLogger().info("Ladattu musiikit " + executionTime + " millisekuntissa.");
        });
        MusicCommand musicCommand = new MusicCommand(musicManager, musicMenu);
        musicCommand.registerCommands();
        registerListeners(new MusicEvents(musicManager));

        //marriage
        /*
        MarriageManager marriageManager = new MarriageManager();
        MarriageMenu marriageMenu = new MarriageMenu(marriageManager);
        MarriageCommand marriageCommand = new MarriageCommand(marriageMenu, marriageManager);
        marriageCommand.registerCommands();
         */

        //prefixmenu
        PrefixCommand prefixCommand = new PrefixCommand(prefixTypeMenu, prefixMainMenu, prefixColorMenu, prefixManager);
        prefixCommand.registerCommands();

        //playtime
        PlaytimeManager playtimeManager = new PlaytimeManager(Optional.of(afkManager));
        PlaytimeCommand playtimeCommand = new PlaytimeCommand(playtimeManager);
        playtimeCommand.registerCommands();
        registerListeners(new PlaytimeListener(playtimeManager));
        playtimeManager.startScheduledSaveTask();

        //spawn
        SpawnCommand spawnCommand = new SpawnCommand(teleportManager);
        spawnCommand.registerCommands();
        Bukkit.getPluginManager().registerEvents(new SpawnTeleport(teleportManager), this);

        //general commands
        InfoCommands infoCommands = new InfoCommands();
        infoCommands.registerCommands();

        GamemodeCommand gamemodeCommand = new GamemodeCommand();
        gamemodeCommand.registerCommands();

        SpeedCommand speedCommand = new SpeedCommand();
        speedCommand.registerCommands();

        TeleportCommands teleportCommands = new TeleportCommands();
        teleportCommands.registerCommands();

        FlyCommand flyCommand = new FlyCommand();
        flyCommand.registerCommands();

        CoreCommand coreCommand = new CoreCommand(teleportManager);
        coreCommand.registerCommands();

        //placeholder
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this, prefixManager, afkManager, playtimeManager).register();
        }
    }

    @Override
    public void onDisable() {
        if (panilla != null) panilla.disablePanilla();
    }

    private void registerListeners(Listener...listeners){
        for(Listener listener : listeners){
            pluginManager.registerEvents(listener, this);
        }
    }

    private void initializePanilla() {
        //only initialize panilla if its enabled
        if (ConfigManager.getPanilla()) {
            getLogger().info("Panilla is being initialized...");
            panilla = new Panilla();
            panilla.loadPanilla(this);
        } else {
            getLogger().info("Panilla not enabled.");
        }
    }

    public PlaytimeManager getPlaytimeManager() {
        return playtimeManager;
    }

    public GahvilaCore getInstance() {
        return instance;
    }
}
