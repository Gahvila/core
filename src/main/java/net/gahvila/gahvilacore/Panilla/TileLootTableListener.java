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

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.loot.Lootable;

public class TileLootTableListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        fixLootTable(event.getBlock());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        fixLootTable(event.getClickedBlock());
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        ItemStack itemStack = event.getItem();

        if (itemStack.getItemMeta() instanceof BlockStateMeta) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) itemStack.getItemMeta();

            if (blockStateMeta.getBlockState() instanceof ShulkerBox) {
                ShulkerBox shulker = (ShulkerBox) blockStateMeta.getBlockState();

                if (shulker.isPlaced()) {
                    try {
                        if (shulker.getLootTable() != null) {
                            shulker.getLootTable().getKey();
                        }
                    } catch (Exception e) {
                        shulker.setLootTable(null);
                        blockStateMeta.setBlockState(shulker);
                        itemStack.setItemMeta(blockStateMeta);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private static void fixLootTable(Block block) {
        if (block == null) {
            return;
        }

        BlockState blockState = block.getState();

        if (blockState instanceof Lootable) {
            Lootable lootable = (Lootable) blockState;

            try {
                if (lootable.getLootTable() != null) {
                    lootable.getLootTable().getKey();
                }
            } catch (Exception e) {
                lootable.setLootTable(null);
                blockState.update(true);
            }
        }
    }

}
