package net.gahvila.gahvilacore;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.gahvila.gahvilacore.Config.ConfigManager;
import net.gahvila.gahvilacore.Essentials.AFK;
import net.gahvila.gahvilacore.Essentials.Commands.*;
import net.gahvila.gahvilacore.Music.MusicCommand;
import net.gahvila.gahvilacore.Music.MusicEvents;
import net.gahvila.gahvilacore.Music.MusicManager;
import net.gahvila.gahvilacore.Music.MusicMenu;
import net.gahvila.gahvilacore.Placeholder.Placeholders;
import net.gahvila.gahvilacore.Profiles.Economy.EconomyCommand;
import net.gahvila.gahvilacore.Profiles.Economy.EconomyManager;
import net.gahvila.gahvilacore.Profiles.Marriage.MarriageCommand;
import net.gahvila.gahvilacore.Profiles.Marriage.MarriageEvents;
import net.gahvila.gahvilacore.Profiles.Marriage.MarriageManager;
import net.gahvila.gahvilacore.Profiles.Marriage.MarriageMenu;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeCommand;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeListener;
import net.gahvila.gahvilacore.Profiles.Playtime.PlaytimeManager;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.PrefixManager;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixColorMenu;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixMainMenu;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixTypeMenu;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.PrefixCommand;
import net.gahvila.gahvilacore.RankFeatures.FullBypass;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class GahvilaCore extends JavaPlugin {
    public static GahvilaCore instance;
    private PrefixManager prefixManager;
    private PrefixTypeMenu prefixTypeMenu;
    private PrefixMainMenu prefixMainMenu;
    private PrefixColorMenu prefixColorMenu;
    private PlaytimeManager playtimeManager;
    private PluginManager pluginManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        instance = this;
        pluginManager = Bukkit.getPluginManager();
        prefixManager = new PrefixManager();
        prefixTypeMenu = new PrefixTypeMenu(prefixManager);
        prefixMainMenu = new PrefixMainMenu();
        prefixColorMenu = new PrefixColorMenu(prefixManager);
        playtimeManager = new PlaytimeManager();

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(false).silentLogs(true));

        //afk
        AFK afk = new AFK(instance);
        afk.afkScheduler();
        afk.registerCommands();
        Bukkit.getPluginManager().registerEvents(afk, this);

        //music
        MusicManager musicManager = new MusicManager();
        MusicMenu musicMenu = new MusicMenu(musicManager);
        musicManager.loadSongs(executionTime -> {
            Bukkit.getLogger().info("Ladattu musiikit " + executionTime + " millisekuntissa.");
        });
        MusicCommand musicCommand = new MusicCommand(musicManager, musicMenu);
        musicCommand.registerCommands();
        registerListeners(new MusicEvents(musicManager));

        //marriage
        MarriageManager marriageManager = new MarriageManager();
        MarriageMenu marriageMenu = new MarriageMenu(marriageManager);
        MarriageCommand marriageCommand = new MarriageCommand(marriageMenu, marriageManager);
        marriageCommand.registerCommands();

        //prefixmenu
        PrefixCommand prefixCommand = new PrefixCommand(prefixTypeMenu, prefixMainMenu, prefixColorMenu, prefixManager);
        prefixCommand.registerCommands();

        //economy
        EconomyManager economyManager = new EconomyManager();
        EconomyCommand economyCommand = new EconomyCommand(economyManager);
        economyCommand.registerCommands();

        //playtime
        PlaytimeManager playtimeManager = new PlaytimeManager();
        PlaytimeCommand playtimeCommand = new PlaytimeCommand(playtimeManager);
        playtimeCommand.registerCommands();
        registerListeners(new PlaytimeListener(playtimeManager));
        playtimeManager.startScheduledSaveTask();

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

        //events
        Bukkit.getPluginManager().registerEvents(new FullBypass(), this);
        Bukkit.getPluginManager().registerEvents(new MarriageEvents(marriageManager), this);

        //placeholder
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this, marriageManager, prefixManager).register();
        }
    }

    @Override
    public void onDisable() {


    }

    private void registerListeners(Listener...listeners){
        for(Listener listener : listeners){
            pluginManager.registerEvents(listener, this);
        }
    }

    public PlaytimeManager getPlaytimeManager() {
        return playtimeManager;
    }
}
