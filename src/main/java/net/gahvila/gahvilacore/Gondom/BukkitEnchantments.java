package net.gahvila.gahvilacore.Gondom;

import net.gahvila.gahvilacore.Gondom.API.EnchantmentCompat;
import net.gahvila.gahvilacore.Gondom.API.IEnchantments;
import net.gahvila.gahvilacore.Gondom.API.config.PConfig;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BukkitEnchantments implements IEnchantments {
    private PConfig pConfig;

    public BukkitEnchantments(PConfig pConfig) {
        this.pConfig = pConfig;
    }

    @Override
    public int getMaxLevel(EnchantmentCompat enchCompat) {
        Enchantment bukkitEnchantment = getBukkitEnchantment(enchCompat);
        if (bukkitEnchantment == null) {
            return Integer.MAX_VALUE; // unknown enchantment
        } else if (pConfig.overrideMinecraftMaxEnchantmentLevels) {
            String enchantmentName = enchCompat.namedKey.split(":")[1];
            if (pConfig.minecraftMaxEnchantmentLevelOverrides.containsKey(enchantmentName)) {
                return pConfig.minecraftMaxEnchantmentLevelOverrides.get(enchantmentName);
            }
        }
        return bukkitEnchantment.getMaxLevel();
    }

    @Override
    public int getStartLevel(EnchantmentCompat enchCompat) {
        Enchantment bukkitEnchantment = getBukkitEnchantment(enchCompat);
        if (bukkitEnchantment == null) {
            return Integer.MAX_VALUE; // unknown enchantment
        } else {
            return bukkitEnchantment.getStartLevel();
        }
    }

    @Override
    public boolean conflicting(EnchantmentCompat enchCompat, EnchantmentCompat _enchCompat) {
        Enchantment bukkitEnchantment = getBukkitEnchantment(enchCompat);
        Enchantment _bukkitEnchantment = getBukkitEnchantment(_enchCompat);
        if (bukkitEnchantment == null || _bukkitEnchantment == null) {
            return false; // unknown enchantment
        } else {
            return bukkitEnchantment.conflictsWith(_bukkitEnchantment);
        }
    }

    private Enchantment getBukkitEnchantment(EnchantmentCompat enchCompat) {
        Enchantment bukkitEnchantment = null;
        try {
            Method getByKey = Enchantment.class.getDeclaredMethod("getByKey", NamespacedKey.class);
            bukkitEnchantment = (Enchantment) getByKey.invoke(null,
                    new NamespacedKey(enchCompat.namedKey.split(":")[0],
                            enchCompat.namedKey.split(":")[1]));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return bukkitEnchantment;
    }

}
