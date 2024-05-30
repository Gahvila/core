package net.gahvila.gahvilacore;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.gahvila.gahvilacore.ChatFormat.SignFormat;
import net.gahvila.gahvilacore.Economy.EconomyCommand;
import net.gahvila.gahvilacore.Economy.EconomyManager;
import net.gahvila.gahvilacore.Essentials.*;
import net.gahvila.gahvilacore.Essentials.Commands.*;
import net.gahvila.gahvilacore.Marriage.MarriageCommand;
import net.gahvila.gahvilacore.Marriage.MarriageEvents;
import net.gahvila.gahvilacore.Marriage.MarriageManager;
import net.gahvila.gahvilacore.Marriage.MarriageMenu;
import net.gahvila.gahvilacore.Placeholder.Placeholders;
import net.gahvila.gahvilacore.RankFeatures.VIP.FullBypass;
import net.gahvila.gahvilacore.RankFeatures.Pro.Prefix.Menu.Events.InventoryClick;
import net.gahvila.gahvilacore.RankFeatures.Pro.Prefix.Menu.PrefixmenuCMD;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class GahvilaCore extends JavaPlugin {
    public static GahvilaCore instance;

    @Override
    public void onEnable() {
        instance = this;
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
        PrefixmenuCMD prefixmenuCMD = new PrefixmenuCMD();
        prefixmenuCMD.registerCommands();

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

        //events
        Bukkit.getPluginManager().registerEvents(new SignFormat(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new FullBypass(), this);
        Bukkit.getPluginManager().registerEvents(new MarriageEvents(marriageManager), this);

        //placeholder
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this, marriageManager).register();
        }
    }

    @Override
    public void onDisable() {


    }


}
