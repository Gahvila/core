package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.EnchantmentCompat;
import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_Enchantments extends NbtCheck {

    public NbtCheck_Enchantments() {
        super("minecraft:enchantments", PStrictness.AVERAGE, "minecraft:stored_enchantments");
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, PanillaPlugin panilla) {
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
                EnchantmentCompat compat = EnchantmentCompat.getByNamedKey(key);
                if (lvl > panilla.getEnchantments().getMaxLevel(compat)) {
                    return NbtCheckResult.FAIL;
                }

                if (lvl < panilla.getEnchantments().getStartLevel(compat)) {
                    return NbtCheckResult.FAIL;
                }

                for (String other : levels.getKeys()) {
                    EnchantmentCompat compatOther = EnchantmentCompat.getByNamedKey(other);
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

}
