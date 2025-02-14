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
