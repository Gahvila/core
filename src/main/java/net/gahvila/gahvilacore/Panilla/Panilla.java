package net.gahvila.gahvilacore.Panilla;

import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Panilla.API.*;
import net.gahvila.gahvilacore.Panilla.API.config.PConfig;
import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.config.PTranslations;
import net.gahvila.gahvilacore.Panilla.NMS.InventoryCleaner;
import net.gahvila.gahvilacore.Panilla.NMS.io.PacketInspector;
import net.gahvila.gahvilacore.Panilla.NMS.io.PlayerInjector;
import net.gahvila.gahvilacore.Panilla.NMS.io.dplx.PacketSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class Panilla {

    private static final String SERVER_IMP = Bukkit.getServer().getClass().getSimpleName();
    private static Class<? extends PacketSerializer> packetSerializerClass;

    private PConfig pConfig;
    private PTranslations pTranslations;
    private PanillaLogger panillaLogger;
    private DefaultProtocolConstants protocolConstants;
    private PlayerInjector playerInjector = new PlayerInjector();
    private PacketInspector packetInspector;
    private InventoryCleaner containerCleaner;
    private PanillaEnchantments enchantments;
    private PanillaCommand panillaCommand;

    public PConfig getPConfig() {
        return pConfig;
    }

    public PTranslations getPTranslations() {
        return pTranslations;
    }

    public PanillaLogger getPanillaLogger() {
        return panillaLogger;
    }

    public DefaultProtocolConstants getProtocolConstants() {
        return protocolConstants;
    }

    public PacketInspector getPacketInspector() {
        return packetInspector;
    }

    public PlayerInjector getPlayerInjector() {
        return playerInjector;
    }

    public InventoryCleaner getInventoryCleaner() {
        return containerCleaner;
    }

    public PanillaEnchantments getEnchantments() {
        return enchantments;
    }

    public PacketSerializer createPacketSerializer(Object byteBuf) {
        try {
            return (PacketSerializer) packetSerializerClass.getConstructors()[0].newInstance(byteBuf);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void exec(Runnable runnable) {
        instance.getServer().getScheduler().runTask(instance, runnable);
    }

    private synchronized void loadConfig() {
        // Define the path to the panilla/panilla.yml in your own plugin folder
        File configFile = new File(instance.getDataFolder(), "panilla.yml");

        // Check if the file exists, if not, copy the default file from resources
        if (!configFile.exists()) {
            instance.saveResource("panilla.yml", false); // Save the default config if it doesn't exist
        }

        // Load the config file
        @NotNull YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        pConfig = new PConfig() {};

        pConfig.language = config.getString("language", pConfig.language);
        pConfig.consoleLogging = config.getBoolean("logging.console", pConfig.consoleLogging);
        pConfig.chatLogging = config.getBoolean("logging.chat", pConfig.chatLogging);
        pConfig.strictness = PStrictness.valueOf(config.getString("strictness", pConfig.strictness.name()).toUpperCase());
        pConfig.preventMinecraftEducationSkulls = config.getBoolean("prevent-minecraft-education-skulls", pConfig.preventMinecraftEducationSkulls);
        pConfig.preventFaweBrushNbt = config.getBoolean("prevent-fawe-brush-nbt", pConfig.preventFaweBrushNbt);
        pConfig.ignoreNonPlayerInventories = config.getBoolean("ignore-non-player-inventories", pConfig.ignoreNonPlayerInventories);
        pConfig.noBlockEntityTag = config.getBoolean("no-block-entity-tag", pConfig.noBlockEntityTag);
        pConfig.nbtWhitelist = config.getStringList("nbt-whitelist");
        pConfig.disabledWorlds = config.getStringList("disabled-worlds");
        pConfig.maxNonMinecraftNbtKeys = config.getInt("max-non-minecraft-nbt-keys", pConfig.maxNonMinecraftNbtKeys);
        pConfig.overrideMinecraftMaxEnchantmentLevels = config.getBoolean("max-enchantment-levels.override-minecraft-max-enchantment-levels", pConfig.overrideMinecraftMaxEnchantmentLevels);

        Map<String, Integer> enchantmentOverrides = new HashMap<>();

        // Make sure the section exists before looping through it
        if (config.contains("max-enchantment-levels.overrides")) {
            for (String enchantmentOverride : config.getConfigurationSection("max-enchantment-levels.overrides").getKeys(false)) {
                int level = config.getInt("max-enchantment-levels.overrides." + enchantmentOverride);
                enchantmentOverrides.put(enchantmentOverride, level);
            }
        }

        pConfig.minecraftMaxEnchantmentLevelOverrides = enchantmentOverrides;
    }

    public void loadPanilla(GahvilaCore gahvilaCore) {
        loadConfig();

        panillaLogger = new PanillaLogger(this, gahvilaCore.getLogger());
        enchantments = new PanillaEnchantments(pConfig);

        packetSerializerClass = PacketSerializer.class;
        protocolConstants = new DefaultProtocolConstants() {};
        playerInjector = new PlayerInjector();
        packetInspector = new PacketInspector(this);
        containerCleaner = new InventoryCleaner(this);

        /* Register listeners */
        instance.getServer().getPluginManager().registerEvents(new JoinQuitListener(this), instance);
        instance.getServer().getPluginManager().registerEvents(new TileLootTableListener(), instance);

        /* Register command */
        panillaCommand = new PanillaCommand();
        panillaCommand.registerCommands();

        /* Inject already online players in case of reload */
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                playerInjector.register(this, new PanillaPlayer(player));
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    public void disablePanilla() {
        /* Uninject any online players */
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                playerInjector.unregister(new PanillaPlayer(player));
            } catch (IOException e) {
                // Ignore
            }
        }
    }

}
