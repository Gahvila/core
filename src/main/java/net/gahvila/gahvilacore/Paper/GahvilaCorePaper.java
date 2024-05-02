package net.gahvila.gahvilacore.Paper;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.gahvila.gahvilacore.Paper.ChatFormat.SignFormat;
import net.gahvila.gahvilacore.Paper.Essentials.*;
import net.gahvila.gahvilacore.Paper.Marriage.MarriageCommand;
import net.gahvila.gahvilacore.Paper.Marriage.MarriageEvents;
import net.gahvila.gahvilacore.Paper.Marriage.MarriageManager;
import net.gahvila.gahvilacore.Paper.Marriage.MarriageMenu;
import net.gahvila.gahvilacore.Paper.Placeholder.Placeholders;
import net.gahvila.gahvilacore.Paper.RankFeatures.VIP.FullBypass;
import net.gahvila.gahvilacore.Paper.RankFeatures.Pro.Prefix.Menu.Events.InventoryClick;
import net.gahvila.gahvilacore.Paper.RankFeatures.Pro.Prefix.Menu.PrefixmenuCMD;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;



public final class GahvilaCorePaper extends JavaPlugin {
    public static GahvilaCorePaper instance;
    private MarriageManager marriageManager;
    private MarriageMenu marriageMenu;

    @Override
    public void onEnable() {

        this.getConfig();

        instance = this;

        marriageManager = new MarriageManager();
        marriageMenu = new MarriageMenu(marriageManager);


        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(false).silentLogs(true));

        MarriageCommand marriageCommand = new MarriageCommand(marriageMenu, marriageManager);
        marriageCommand.registerCommands();

        AFK afk = new AFK(instance);
        afk.afkScheduler();

        this.getCommand("prefixmenu").setExecutor(new PrefixmenuCMD());
        this.getCommand("discord").setExecutor(new DiscordCommand());
        this.getCommand("säännöt").setExecutor(new RulesCommand());
        this.getCommand("speed").setExecutor(new SpeedCommand());
        this.getCommand("flightspeed").setExecutor(new SpeedCommand());
        this.getCommand("walkspeed").setExecutor(new SpeedCommand());
        this.getCommand("timedshutdown").setExecutor(new TimedShutdownCommand());
        this.getCommand("afk").setExecutor(new AFK(instance));
        this.getCommand("serverinfo").setExecutor(new ServerinfoCommand());

        Bukkit.getPluginManager().registerEvents(new SignFormat(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new FullBypass(), this);
        Bukkit.getPluginManager().registerEvents(new AFK(instance), this);
        Bukkit.getPluginManager().registerEvents(new MarriageEvents(marriageManager), this);


        this.getCommand("gamemode").setExecutor(new GameModeCommand());
        //placeholder
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this, marriageManager).register();}


    }

    @Override
    public void onDisable() {


    }


}
