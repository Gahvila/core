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
