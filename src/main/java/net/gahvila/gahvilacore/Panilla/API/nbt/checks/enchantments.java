/*
 * MIT License
 *
 * Copyright (c) 2019 Ruinscraft, LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
