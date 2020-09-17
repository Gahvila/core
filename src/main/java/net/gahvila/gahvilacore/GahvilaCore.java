package net.gahvila.gahvilacore;

import net.gahvila.gahvilacore.ChatFormat.ChatFormat;
import net.gahvila.gahvilacore.ChatFormat.ChatFormatToggleCommand;
import net.gahvila.gahvilacore.ChatFormat.SignFormat;
import net.gahvila.gahvilacore.Essentials.FlyCommand;
import net.gahvila.gahvilacore.Essentials.GameModeCommand;
import net.gahvila.gahvilacore.ProGui.GuiCommand;
import net.gahvila.gahvilacore.ProGui.InventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class GahvilaCore extends JavaPlugin {

    public static GahvilaCore instance;

    @Override
    public void onEnable() {

        this.getConfig();
        this.saveDefaultConfig();


        instance = this;
        this.getCommand("gui").setExecutor(new GuiCommand());
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);

        this.getCommand("chatformattoggle").setExecutor(new ChatFormatToggleCommand());
        Bukkit.getPluginManager().registerEvents(new ChatFormat(), this);
        Bukkit.getPluginManager().registerEvents(new SignFormat(), this);

        this.getCommand("gamemode").setExecutor(new GameModeCommand());
        this.getCommand("fly").setExecutor(new FlyCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
