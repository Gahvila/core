package net.gahvila.gahvilacore;

import net.gahvila.gahvilacore.AFK.AfkCommand;
import net.gahvila.gahvilacore.AFK.AfkEvents;
import net.gahvila.gahvilacore.AFK.AfkManager;
import net.gahvila.gahvilacore.Config.ConfigManager;
import net.gahvila.gahvilacore.Core.CoreCommand;
import net.gahvila.gahvilacore.Essentials.Commands.*;
import net.gahvila.gahvilacore.Music.*;
import net.gahvila.gahvilacore.Panilla.Panilla;
import net.gahvila.gahvilacore.Placeholder.Placeholders;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeCommand;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeListener;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeManager;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.PrefixManager;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.DialogMenu.PrefixColorDialog;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.DialogMenu.PrefixMainDialog;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.DialogMenu.PrefixTypeDialog;
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
    private PrefixTypeDialog prefixTypeDialog;
    private PrefixMainDialog prefixMainDialog;
    private PrefixColorDialog prefixColorDialog;
    private PlaytimeManager playtimeManager;
    private AfkManager afkManager;
    private PluginManager pluginManager;
    private TeleportManager teleportManager;
    private Panilla panilla;
    private MusicManager musicManager;
    private MusicDialogMenu musicDialogMenu;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        instance = this;
        pluginManager = Bukkit.getPluginManager();
        prefixManager = new PrefixManager();
        prefixTypeDialog = new PrefixTypeDialog(prefixManager);
        prefixMainDialog = new PrefixMainDialog(prefixManager);
        prefixColorDialog = new PrefixColorDialog(prefixManager);
        afkManager = new AfkManager();
        playtimeManager = new PlaytimeManager(Optional.of(afkManager));
        teleportManager = new TeleportManager();

        //panilla
        initializePanilla();

        //afk
        registerListeners(new AfkEvents(afkManager));
        afkManager.startAfkScheduler();

        //music
        musicManager = new MusicManager();
        musicManager.createRadioPlayer();
        musicDialogMenu = new MusicDialogMenu(musicManager);
        musicManager.loadSongs(executionTime -> {
            this.getLogger().info("Ladattu musiikit " + executionTime + " millisekuntissa.");
        });
        registerListeners(new MusicEvents(musicManager));

        //playtime
        playtimeManager = new PlaytimeManager(Optional.of(afkManager));
        registerListeners(new PlaytimeListener(playtimeManager));
        playtimeManager.startScheduledSaveTask();

        //spawn
        Bukkit.getPluginManager().registerEvents(new SpawnTeleport(teleportManager), this);



        //placeholder
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this, prefixManager, afkManager, playtimeManager).register();
        }

        registerCommands();
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

    private void registerCommands() {
        AfkCommand afkCommand = new AfkCommand(afkManager);
        afkCommand.registerCommands(this);

        CoreCommand coreCommand = new CoreCommand(teleportManager);
        coreCommand.registerCommands(this);

        FlyCommand flyCommand = new FlyCommand();
        flyCommand.registerCommands(this);

        GamemodeCommand gamemodeCommand = new GamemodeCommand();
        gamemodeCommand.registerCommands(this);

        InfoCommands infoCommands = new InfoCommands();
        infoCommands.registerCommands(this);

        SpeedCommand speedCommand = new SpeedCommand();
        speedCommand.registerCommands(this);

        MusicCommand musicCommand = new MusicCommand(musicManager, musicDialogMenu);
        musicCommand.registerCommands(this);

        SpawnCommand spawnCommand = new SpawnCommand(teleportManager);
        spawnCommand.registerCommands(this);

        PlaytimeCommand playtimeCommand = new PlaytimeCommand(playtimeManager);
        playtimeCommand.registerCommands(this);

        PrefixCommand prefixCommand = new PrefixCommand(prefixTypeDialog, prefixMainDialog, prefixColorDialog, prefixManager);
        prefixCommand.registerCommands(this);

        TeleportCommands teleportCommands = new TeleportCommands();
        teleportCommands.registerCommands(this);
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
