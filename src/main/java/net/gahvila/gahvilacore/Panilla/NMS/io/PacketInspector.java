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
package net.gahvila.gahvilacore.Panilla.NMS.io;

import de.tr7zw.changeme.nbtapi.NBT;
import net.gahvila.gahvilacore.Panilla.API.exception.*;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtChecks;
import net.gahvila.gahvilacore.Panilla.PanillaPlayer;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.HashedStack;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PacketInspector {

    private final Panilla panilla;

    public PacketInspector(Panilla panilla) {
        this.panilla = panilla;
    }

    public void checkServerbound(Panilla panilla, PanillaPlayer player, Object packetHandle) throws PacketException {
        try {
            checkServerboundContainerClickPacket(packetHandle);
        } catch (NbtNotPermittedException e) {
            if (!player.canBypassChecks(panilla, e)) {
                sendClientboundContainerSetSlotPacketAir(player, e.getItemSlot());
            }
            throw e;
        }

        try {
            checkServerboundSetCreativeModeSlotPacket(packetHandle);
        } catch (NbtNotPermittedException e) {
            if (!player.canBypassChecks(panilla, e)) {
                sendClientboundContainerSetSlotPacketAir(player, e.getItemSlot());
            }
            throw e;
        }
    }

    public void checkClientbound(Panilla panilla, Object packetHandle) throws PacketException {
        checkClientboundContainerSetSlotPacket(packetHandle);
        checkClientboundContainerSetContentPacket(packetHandle);

        try {
            checkClientboundAddEntityPacket(packetHandle);
        } catch (EntityNbtNotPermittedException e) {
            if (!panilla.getPConfig().disabledWorlds.contains(e.getWorld())) {
                stripNbtFromItemEntity(e.getEntityId());
                panilla.getPanillaLogger().log(panilla.getPTranslations().getTranslation("itemEntityStripped", e.getFailedNbt().key), true);
                throw e;
            }
        }
    }

    public void checkServerboundContainerClickPacket(Object packetHandle) throws NbtNotPermittedException {
        if (!(packetHandle instanceof ServerboundContainerClickPacket packet)) return;
        int windowId = packet.containerId();
        if (windowId != 0 && panilla.getPConfig().ignoreNonPlayerInventories) return;

        int slot = packet.slotNum();
        HashedStack hashedStack = packet.carriedItem();
        if (hashedStack instanceof HashedStack.ActualItem actualItem) {
            ItemStack item = new ItemStack(actualItem.item());
            if (item.isEmpty() || item.getComponents().isEmpty()) return;
            NbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(item.getBukkitStack()).getCompound("components"));
            String itemClass = item.getClass().getName();

            NbtChecks.checkServerbound(slot, tag, itemClass, packet.getClass().getSimpleName(), panilla);
        }
    }

    public void checkServerboundSetCreativeModeSlotPacket(Object packetHandle) throws NbtNotPermittedException {
        if (!(packetHandle instanceof ServerboundSetCreativeModeSlotPacket packet)) return;

        int slot = packet.slotNum();
        ItemStack item = packet.itemStack();
        if (item == null || item.isEmpty() || item.getComponents().isEmpty()) return;

        NbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(item.getBukkitStack()).getCompound("components"));
        String itemClass = item.getClass().getName();

        NbtChecks.checkServerbound(slot, tag, itemClass, packet.getClass().getSimpleName(), panilla);
    }

    public void checkClientboundContainerSetSlotPacket(Object packetHandle) throws NbtNotPermittedException {
        if (!(packetHandle instanceof ClientboundContainerSetSlotPacket packet)) return;

        int windowId = packet.getContainerId();

        // check if window is not player inventory and we are ignoring non-player inventories
        if (windowId != 0 && panilla.getPConfig().ignoreNonPlayerInventories) {
            return;
        }

        int slot = packet.getSlot();

        ItemStack item = packet.getItem();

        if (item == null || item.isEmpty() || item.getComponents().isEmpty()) {
            return;
        }

        NbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(item.getBukkitStack()).getCompound("components"));
        String itemClass = item.getClass().getSimpleName();

        NbtChecks.checkClientbound(slot, tag, itemClass, packet.getClass().getSimpleName(), panilla);
    }

    public void checkClientboundContainerSetContentPacket(Object packetHandle) throws NbtNotPermittedException {
        if (!(packetHandle instanceof ClientboundContainerSetContentPacket packet)) return;

        int windowId = packet.containerId();

        // check if window is not player inventory
        if (windowId != 0) {
            return;
        }

        List<ItemStack> itemStacks = packet.items();

        for (int slotIndex = 0; slotIndex < itemStacks.size(); slotIndex++) {
            ItemStack itemStack = itemStacks.get(slotIndex);

            if (!itemStack.isEmpty() || itemStack.getComponents().isEmpty()) {
                continue;
            }

            NbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(itemStack.asBukkitCopy()).getCompound("components"));
            String itemClass = itemStack.getClass().getSimpleName();

            NbtChecks.checkClientbound(slotIndex, tag, itemClass, packet.getClass().getSimpleName(), panilla);
        }
    }

    public void checkClientboundAddEntityPacket(Object packetHandle) throws EntityNbtNotPermittedException {
        if ((!(packetHandle instanceof ClientboundAddEntityPacket packet))) return;

        UUID entityId = packet.getUUID();
        Entity entity = null;

        for (ServerLevel worldServer : MinecraftServer.getServer().getAllLevels()) {
            entity = worldServer.getEntities().get(entityId); //TODO: CHANGE BACK TO MOONRISE!!!
            if (entity != null) break;
        }

        if (!(entity instanceof ItemEntity)) return;

        ItemEntity item = (ItemEntity) entity;
        ItemStack itemStack = item.getItem();

        if (itemStack == null) {
            return;
        }

        if (itemStack.isEmpty() || itemStack.getComponents().isEmpty()) {
            return;
        }

        NbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(itemStack.getBukkitStack()).getCompound("components"));
        String itemName = itemStack.getItem().getDescriptionId();
        String worldName = "";

        try {
            Field worldField = Entity.class.getDeclaredField("level");
            worldField.setAccessible(true);
            Level world = (Level) worldField.get(entity);
            worldName = world.getWorld().getName();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        FailedNbtList failedNbtList = NbtChecks.checkAll(tag, itemName, panilla);

        if (failedNbtList.containsCritical()) {
            throw new EntityNbtNotPermittedException(packet.getClass().getSimpleName(), false, failedNbtList.getCritical(), entityId, worldName);
        }

        FailedNbt failedNbt = failedNbtList.findFirstNonCritical();

        if (failedNbt != null) {
            throw new EntityNbtNotPermittedException(packet.getClass().getSimpleName(), false, failedNbt, entityId, worldName);
        }
    }

    public void sendClientboundContainerSetSlotPacketAir(PanillaPlayer player, int slot) {
        CraftPlayer craftPlayer = (CraftPlayer) player.getHandle();
        ServerPlayer entityPlayer = craftPlayer.getHandle();
        ClientboundContainerSetSlotPacket packet = new ClientboundContainerSetSlotPacket(entityPlayer.containerMenu.containerId, entityPlayer.containerMenu.incrementStateId(), slot, new ItemStack(Blocks.AIR));
        entityPlayer.connection.send(packet);
    }

    public void stripNbtFromItemEntity(UUID entityId) {
        Entity entity = null;

        for (ServerLevel worldServer : MinecraftServer.getServer().getAllLevels()) {
            entity = worldServer.getEntities().get(entityId); //TODO: CHANGE BACK TO MOONRISE!!!
            if (entity != null) break;
        }

        if (entity instanceof ItemEntity item) {
            ItemStack itemStack = item.getItem();
            if (itemStack == null || itemStack.isEmpty() || itemStack.getComponents().isEmpty()) return;
            Iterator<TypedDataComponent<?>> iter = itemStack.getComponents().iterator();
            while (iter.hasNext()) iter.remove();
        }
    }

    public void validateBaseComponentParse(String string) throws Exception {
        CraftChatMessage.fromJSON(string);
    }

}
