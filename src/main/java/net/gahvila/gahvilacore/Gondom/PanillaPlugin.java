package net.gahvila.gahvilacore.Gondom;

import net.gahvila.gahvilacore.Gondom.API.*;
import net.gahvila.gahvilacore.Gondom.API.config.PConfig;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.config.PTranslations;
import net.gahvila.gahvilacore.Gondom.API.io.IPacketInspector;
import net.gahvila.gahvilacore.Gondom.API.io.IPacketSerializer;
import net.gahvila.gahvilacore.Gondom.API.io.IPlayerInjector;
import net.gahvila.gahvilacore.Gondom.NMS.InventoryCleaner;
import net.gahvila.gahvilacore.Gondom.NMS.io.PacketInspector;
import net.gahvila.gahvilacore.Gondom.NMS.io.PlayerInjector;
import net.gahvila.gahvilacore.Gondom.NMS.io.dplx.PacketSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class PanillaPlugin extends JavaPlugin implements IPanilla {

    private static final String SERVER_IMP = Bukkit.getServer().getClass().getSimpleName();
    private static Class<? extends IPacketSerializer> packetSerializerClass;

    private PConfig pConfig;
    private PTranslations pTranslations;
    private IPanillaLogger panillaLogger;
    private IProtocolConstants protocolConstants;
    private IPlayerInjector playerInjector = new PlayerInjector();
    private IPacketInspector packetInspector;
    private IInventoryCleaner containerCleaner;
    private IEnchantments enchantments;

    @Override
    public PConfig getPConfig() {
        return pConfig;
    }

    @Override
    public PTranslations getPTranslations() {
        return pTranslations;
    }

    @Override
    public IPanillaLogger getPanillaLogger() {
        return panillaLogger;
    }

    @Override
    public IProtocolConstants getProtocolConstants() {
        return protocolConstants;
    }

    @Override
    public IPacketInspector getPacketInspector() {
        return packetInspector;
    }

    @Override
    public IPlayerInjector getPlayerInjector() {
        return playerInjector;
    }

    @Override
    public IInventoryCleaner getInventoryCleaner() {
        return containerCleaner;
    }

    @Override
    public IEnchantments getEnchantments() {
        return enchantments;
    }

    @Override
    public IPacketSerializer createPacketSerializer(Object byteBuf) {
        try {
            return (IPacketSerializer) packetSerializerClass.getConstructors()[0].newInstance(byteBuf);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void exec(Runnable runnable) {
        getServer().getScheduler().runTask(this, runnable);
    }

    private synchronized void loadConfig() {
        saveDefaultConfig();

        pConfig = new BukkitPConfig();

        pConfig.language = getConfig().getString("language", pConfig.language);
        pConfig.consoleLogging = getConfig().getBoolean("logging.console", pConfig.consoleLogging);
        pConfig.chatLogging = getConfig().getBoolean("logging.chat", pConfig.chatLogging);
        pConfig.strictness = PStrictness.valueOf(getConfig().getString("strictness", pConfig.strictness.name()).toUpperCase());
        pConfig.preventMinecraftEducationSkulls = getConfig().getBoolean("prevent-minecraft-education-skulls", pConfig.preventMinecraftEducationSkulls);
        pConfig.preventFaweBrushNbt = getConfig().getBoolean("prevent-fawe-brush-nbt", pConfig.preventFaweBrushNbt);
        pConfig.ignoreNonPlayerInventories = getConfig().getBoolean("ignore-non-player-inventories", pConfig.ignoreNonPlayerInventories);
        pConfig.noBlockEntityTag = getConfig().getBoolean("no-block-entity-tag", pConfig.noBlockEntityTag);
        pConfig.nbtWhitelist = getConfig().getStringList("nbt-whitelist");
        pConfig.disabledWorlds = getConfig().getStringList("disabled-worlds");
        pConfig.maxNonMinecraftNbtKeys = getConfig().getInt("max-non-minecraft-nbt-keys", pConfig.maxNonMinecraftNbtKeys);
        pConfig.overrideMinecraftMaxEnchantmentLevels = getConfig().getBoolean("max-enchantment-levels.override-minecraft-max-enchantment-levels", pConfig.overrideMinecraftMaxEnchantmentLevels);

        Map<String, Integer> enchantmentOverrides = new HashMap<>();

        for (String enchantmentOverride : getConfig().getConfigurationSection("max-enchantment-levels.overrides").getKeys(false)) {
            int level = getConfig().getInt("max-enchantment-levels.overrides." + enchantmentOverride);
            enchantmentOverrides.put(enchantmentOverride, level);
        }

        pConfig.minecraftMaxEnchantmentLevelOverrides = enchantmentOverrides;
    }

    private synchronized void loadTranslations(String languageKey) {
        try {
            pTranslations = PTranslations.get(languageKey);
        } catch (IOException e) {
            getPanillaLogger().warning("Could not load language translations for " + languageKey, false);
        }
    }

    @Override
    public void onEnable() {
        loadConfig();
        loadTranslations(pConfig.language);

        panillaLogger = new BukkitPanillaLogger(this, getLogger());
        enchantments = new BukkitEnchantments(pConfig);

        packetSerializerClass = PacketSerializer.class;
        protocolConstants = new IProtocolConstants() {
            @Override
            public int maxBookPages() {
                return 100;
            }
        };
        playerInjector = new PlayerInjector();
        packetInspector = new PacketInspector(this);
        containerCleaner = new InventoryCleaner(this);

        /* Register listeners */
        getServer().getPluginManager().registerEvents(new JoinQuitListener(this, this), this);
        getServer().getPluginManager().registerEvents(new TileLootTableListener(), this);

        /* Register command */
        getCommand("panilla").setExecutor(new PanillaCommand(this));

        /* Inject already online players in case of reload */
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                playerInjector.register(this, new BukkitPanillaPlayer(player));
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    @Override
    public void onDisable() {
        /* Uninject any online players */
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                playerInjector.unregister(new BukkitPanillaPlayer(player));
            } catch (IOException e) {
                // Ignore
            }
        }
    }

}
