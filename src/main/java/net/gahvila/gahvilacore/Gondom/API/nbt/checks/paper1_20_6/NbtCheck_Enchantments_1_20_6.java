package net.gahvila.gahvilacore.Gondom.API.nbt.checks.paper1_20_6;

import net.gahvila.gahvilacore.Gondom.API.EnchantmentCompat;
import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Gondom.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Gondom.API.nbt.checks.NbtCheck;

public class NbtCheck_Enchantments_1_20_6 extends NbtCheck {

    public NbtCheck_Enchantments_1_20_6() {
        super("minecraft:enchantments", PStrictness.AVERAGE, "minecraft:stored_enchantments");
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
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

        INbtTagCompound enchantments = tag.getCompound(using);

        if (enchantments.hasKeyOfType("levels", NbtDataType.COMPOUND)) {
            INbtTagCompound levels = enchantments.getCompound("levels");
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
