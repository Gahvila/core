package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

public class enchantments extends NbtCheck {

    public enchantments() {
        super("minecraft:enchantments", PStrictness.AVERAGE, "minecraft:stored_enchantments");
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        String using = null;

        if (tag.hasKeyOfType(getName(), NbtDataType.COMPOUND)) {
            using = getName();
        } else {
            for (String alias : getAliases()) {
                if (tag.hasKeyOfType(alias, NbtDataType.COMPOUND)) {
                    using = alias;
                    break;
                }
            }
        }

        if (using == null) throw new RuntimeException("Unknown enchantment tag");

        NbtTagCompound enchantments = tag.getCompound(using);

        if (enchantments.hasKeyOfType("levels", NbtDataType.COMPOUND)) {
            NbtTagCompound levels = enchantments.getCompound("levels");
            for (String key : levels.getKeys()) {
                int lvl = levels.getInt(key);
                Enchantment compat = getByNamedKey(key);
                if (lvl > panilla.getEnchantments().getMaxLevel(compat)) {
                    return NbtCheckResult.FAIL;
                }

                if (lvl < panilla.getEnchantments().getStartLevel(compat)) {
                    return NbtCheckResult.FAIL;
                }

                for (String other : levels.getKeys()) {
                    Enchantment compatOther = getByNamedKey(other);
                    if (compatOther != compat) {
                        if (panilla.getEnchantments().conflicting(compatOther, compat)) {
                            return NbtCheckResult.FAIL;
                        }
                    }
                }
            }
        }

        return NbtCheckResult.PASS;
    }

    public static Enchantment getByNamedKey(String namedKey) {
        try {
            NamespacedKey key = NamespacedKey.fromString(namedKey);
            if (key == null) {
                throw new IllegalArgumentException("Invalid enchantment key format: " + namedKey);
            }

            return RegistryAccess.registryAccess()
                    .getRegistry(RegistryKey.ENCHANTMENT)
                    .get(key);
        } catch (IllegalArgumentException | NullPointerException e) {
            Bukkit.getLogger().warning("Invalid enchantment key: " + namedKey);
            return null;
        }
    }
}
