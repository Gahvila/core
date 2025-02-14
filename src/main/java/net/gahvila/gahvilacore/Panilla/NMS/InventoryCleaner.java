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
package net.gahvila.gahvilacore.Panilla.NMS;

import de.tr7zw.changeme.nbtapi.NBT;
import net.gahvila.gahvilacore.Panilla.API.exception.FailedNbt;
import net.gahvila.gahvilacore.Panilla.API.exception.FailedNbtList;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtChecks;
import net.gahvila.gahvilacore.Panilla.PanillaPlayer;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.Iterator;

public class InventoryCleaner {

    private final Panilla panilla;

    public InventoryCleaner(Panilla panilla) {
        this.panilla = panilla;
    }

    public void clean(PanillaPlayer player) {
        CraftPlayer craftPlayer = (CraftPlayer) player.getHandle();
        Inventory container = craftPlayer.getHandle().getInventory();

        for (int slot = 0; slot < container.getContents().size(); slot++) {
            ItemStack itemStack = container.getContents().get(slot);

            if (itemStack == null || itemStack.isEmpty() || itemStack.getComponents().isEmpty()) {
                continue;
            }

            NbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(itemStack.getBukkitStack()).getCompound("components"));
            String itemName = itemStack.getItem().getDescriptionId();

            FailedNbtList failedNbtList = NbtChecks.checkAll(tag, itemName, panilla);

            for (FailedNbt failedNbt : failedNbtList) {
                if (FailedNbt.failsThreshold(failedNbt)) {
                    Iterator<TypedDataComponent<?>> iter = itemStack.getComponents().iterator();
                    while (iter.hasNext()) {
                        iter.next(); // Move to the next element
                        iter.remove(); // Remove the current element
                    }
                    break;
                } else if (FailedNbt.fails(failedNbt)) {
                    NBT.modifyComponents(itemStack.getBukkitStack(), s -> {
                        s.removeKey(failedNbt.key);
                    });
                    break;
                }
            }
        }
    }
}
