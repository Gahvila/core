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

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class container_loot extends NbtCheck {

    public container_loot() {
        super("minecraft:container_loot", PStrictness.STRICT);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        tag = tag.getCompound(getName());

        String lootTable = tag.getString("loot_table");

        lootTable = lootTable.trim();

        if (lootTable.isEmpty()) {
            return NbtCheckResult.CRITICAL;
        }

        if (lootTable.contains(":")) {
            String[] keySplit = lootTable.split(":");

            if (keySplit.length < 2) {
                return NbtCheckResult.CRITICAL;
            }

            String namespace = keySplit[0];
            String key = keySplit[1];

            if (namespace.isEmpty() || key.isEmpty()) {
                return NbtCheckResult.CRITICAL;
            }
        }

        return NbtCheckResult.PASS;
    }

}
