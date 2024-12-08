package net.gahvila.gahvilacore.Gondom.NMS;

import de.tr7zw.changeme.nbtapi.NBT;
import net.gahvila.gahvilacore.Gondom.API.IInventoryCleaner;
import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.IPanillaPlayer;
import net.gahvila.gahvilacore.Gondom.API.exception.FailedNbt;
import net.gahvila.gahvilacore.Gondom.API.exception.FailedNbtList;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Gondom.API.nbt.checks.NbtChecks;
import net.gahvila.gahvilacore.Gondom.NMS.nbt.NbtTagCompound;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.Iterator;

public class InventoryCleaner implements IInventoryCleaner {

    private final IPanilla panilla;

    public InventoryCleaner(IPanilla panilla) {
        this.panilla = panilla;
    }

    @Override
    public void clean(IPanillaPlayer player) {
        CraftPlayer craftPlayer = (CraftPlayer) player.getHandle();
        Inventory container = craftPlayer.getHandle().getInventory();

        for (int slot = 0; slot < container.getContents().size(); slot++) {
            ItemStack itemStack = container.getContents().get(slot);

            if (itemStack == null || itemStack.isEmpty() || itemStack.getComponents().isEmpty()) {
                continue;
            }

            INbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(itemStack.getBukkitStack()).getCompound("components"));
            String itemName = itemStack.getItem().getDescriptionId();

            FailedNbtList failedNbtList = NbtChecks.checkAll(tag, itemName, panilla);

            for (FailedNbt failedNbt : failedNbtList) {
                if (FailedNbt.failsThreshold(failedNbt)) {
                    Iterator<TypedDataComponent<?>> iter = itemStack.getComponents().iterator();
                    while (iter.hasNext()) iter.remove();

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
