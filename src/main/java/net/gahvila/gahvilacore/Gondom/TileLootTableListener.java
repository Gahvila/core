package net.gahvila.gahvilacore.Gondom;

import org.bukkit.Bukkit;
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