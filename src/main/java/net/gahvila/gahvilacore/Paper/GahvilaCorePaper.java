package net.gahvila.gahvilacore.Paper;

import net.gahvila.gahvilacore.Paper.ChatFormat.SignFormat;
import net.gahvila.gahvilacore.Paper.Essentials.*;
import net.gahvila.gahvilacore.Paper.Placeholder.Placeholders;
import net.gahvila.gahvilacore.Paper.RankFeatures.VIP.FullBypass;
import net.gahvila.gahvilacore.Paper.RankFeatures.Pro.Prefix.Menu.Events.InventoryClick;
import net.gahvila.gahvilacore.Paper.RankFeatures.Pro.Prefix.Menu.PrefixmenuCMD;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;



public final class GahvilaCorePaper extends JavaPlugin {
    public static GahvilaCorePaper instance;

    @Override
    public void onEnable() {

        this.getConfig();
        this.saveDefaultConfig();


        instance = this;
        this.getCommand("prefixmenu").setExecutor(new PrefixmenuCMD());
        this.getCommand("discord").setExecutor(new DiscordCommand());
        this.getCommand("säännöt").setExecutor(new RulesCommand());
        this.getCommand("speed").setExecutor(new SpeedCommand());
        this.getCommand("flightspeed").setExecutor(new SpeedCommand());
        this.getCommand("walkspeed").setExecutor(new SpeedCommand());
        this.getCommand("timedshutdown").setExecutor(new TimedShutdownCommand());
        this.getCommand("afk").setExecutor(new AFK());
        this.getCommand("serverinfo").setExecutor(new ServerinfoCommand());

        Bukkit.getPluginManager().registerEvents(new SignFormat(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new FullBypass(), this);
        Bukkit.getPluginManager().registerEvents(new AFK(), this);

        this.getCommand("gamemode").setExecutor(new GameModeCommand());
        this.getCommand("fly").setExecutor(new FlyCommand());
        //placeholder
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();}


    }

    @Override
    public void onDisable() {


    }


}
