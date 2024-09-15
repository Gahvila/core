package net.gahvila.gahvilacore;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.gahvila.gahvilacore.Essentials.Commands.TeleportCommands;
import net.gahvila.gahvilacore.Profiles.Economy.EconomyCommand;
import net.gahvila.gahvilacore.Profiles.Economy.EconomyManager;
import net.gahvila.gahvilacore.Essentials.AFK;
import net.gahvila.gahvilacore.Essentials.Commands.GamemodeCommand;
import net.gahvila.gahvilacore.Essentials.Commands.InfoCommands;
import net.gahvila.gahvilacore.Essentials.Commands.SpeedCommand;
import net.gahvila.gahvilacore.Profiles.Marriage.MarriageCommand;
import net.gahvila.gahvilacore.Profiles.Marriage.MarriageEvents;
import net.gahvila.gahvilacore.Profiles.Marriage.MarriageManager;
import net.gahvila.gahvilacore.Profiles.Marriage.MarriageMenu;
import net.gahvila.gahvilacore.Placeholder.Placeholders;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.PrefixManager;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixMainMenu;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixTypeMenu;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.PrefixCommand;
import net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu.PrefixColorMenu;
import net.gahvila.gahvilacore.RankFeatures.FullBypass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class GahvilaCore extends JavaPlugin {
    public static GahvilaCore instance;
    private PrefixManager prefixManager;
    private PrefixTypeMenu prefixTypeMenu;
    private PrefixMainMenu prefixMainMenu;
    private PrefixColorMenu prefixColorMenu;


    @Override
    public void onEnable() {
        instance = this;
        prefixManager = new PrefixManager();
        prefixTypeMenu = new PrefixTypeMenu(prefixManager);
        prefixMainMenu = new PrefixMainMenu();
        prefixColorMenu = new PrefixColorMenu(prefixManager);

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(false).silentLogs(true));

        //afk
        AFK afk = new AFK(instance);
        afk.afkScheduler();
        afk.registerCommands();
        Bukkit.getPluginManager().registerEvents(afk, this);

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

        //general commands
        InfoCommands infoCommands = new InfoCommands();
        infoCommands.registerCommands();

        GamemodeCommand gamemodeCommand = new GamemodeCommand();
        gamemodeCommand.registerCommands();

        SpeedCommand speedCommand = new SpeedCommand();
        speedCommand.registerCommands();

        TeleportCommands teleportCommands = new TeleportCommands();
        teleportCommands.registerCommands();

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


}
