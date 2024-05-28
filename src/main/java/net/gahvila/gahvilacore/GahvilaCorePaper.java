package net.gahvila.gahvilacore;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.gahvila.gahvilacore.ChatFormat.SignFormat;
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



public final class GahvilaCorePaper extends JavaPlugin {
    public static GahvilaCorePaper instance;

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

        //general commands
        InfoCommands infoCommands = new InfoCommands();
        infoCommands.registerCommands();

        this.getCommand("prefixmenu").setExecutor(new PrefixmenuCMD());
        this.getCommand("säännöt").setExecutor(new RulesCommand());
        this.getCommand("speed").setExecutor(new SpeedCommand());
        this.getCommand("flightspeed").setExecutor(new SpeedCommand());
        this.getCommand("walkspeed").setExecutor(new SpeedCommand());
        this.getCommand("timedshutdown").setExecutor(new TimedShutdownCommand());
        this.getCommand("serverinfo").setExecutor(new ServerinfoCommand());

        Bukkit.getPluginManager().registerEvents(new SignFormat(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new FullBypass(), this);
        Bukkit.getPluginManager().registerEvents(new MarriageEvents(marriageManager), this);


        this.getCommand("gamemode").setExecutor(new GameModeCommand());
        //placeholder
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this, marriageManager).register();
        }
    }

    @Override
    public void onDisable() {


    }


}
