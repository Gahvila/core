package net.gahvila.gahvilacore.Panilla;

import net.gahvila.gahvilacore.Panilla.API.config.PConfig;
import org.bukkit.enchantments.Enchantment;

public class PanillaEnchantments {
    private PConfig pConfig;

    public PanillaEnchantments(PConfig pConfig) {
        this.pConfig = pConfig;
    }

    public int getMaxLevel(Enchantment enchantment) {
        if (enchantment == null) {
            return Integer.MAX_VALUE; // unknown enchantment
        } else if (pConfig.overrideMinecraftMaxEnchantmentLevels) {
            String enchantmentName = String.valueOf(enchantment.getKey()).split(":")[1];
            if (pConfig.minecraftMaxEnchantmentLevelOverrides.containsKey(enchantmentName)) {
                return pConfig.minecraftMaxEnchantmentLevelOverrides.get(enchantmentName);
            }
        }
        return enchantment.getMaxLevel();
    }

    public int getStartLevel(Enchantment enchantment) {
        if (enchantment == null) {
            return Integer.MAX_VALUE; // unknown enchantment
        } else {
            return enchantment.getStartLevel();
        }
    }

    public boolean conflicting(Enchantment enchantment, Enchantment _enchantment) {
        if (enchantment == null || _enchantment == null) {
            return false; // unknown enchantment
        } else {
            return enchantment.conflictsWith(_enchantment);
        }
    }
}
