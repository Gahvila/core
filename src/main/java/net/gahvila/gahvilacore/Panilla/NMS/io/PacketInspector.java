package net.gahvila.gahvilacore.Panilla.NMS.io;

import de.tr7zw.changeme.nbtapi.NBT;
import net.gahvila.gahvilacore.Panilla.API.exception.*;
import net.gahvila.gahvilacore.Panilla.API.nbt.checks.NbtChecks;
import net.gahvila.gahvilacore.Panilla.BukkitPanillaPlayer;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;
import net.minecraft.core.component.TypedDataComponent;
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

    private final PanillaPlugin panilla;

    public PacketInspector(PanillaPlugin panilla) {
        this.panilla = panilla;
    }

    public void checkPlayIn(PanillaPlugin panilla, BukkitPanillaPlayer player, Object packetHandle) throws PacketException {
        try {
            checkPacketPlayInClickContainer(packetHandle);
        } catch (NbtNotPermittedException e) {
            if (!player.canBypassChecks(panilla, e)) {
                sendPacketPlayOutSetSlotAir(player, e.getItemSlot());
            }
            throw e;
        }

        try {
            checkPacketPlayInSetCreativeSlot(packetHandle);
        } catch (NbtNotPermittedException e) {
            if (!player.canBypassChecks(panilla, e)) {
                sendPacketPlayOutSetSlotAir(player, e.getItemSlot());
            }
            throw e;
        }
    }

    public void checkPlayOut(PanillaPlugin panilla, Object packetHandle) throws PacketException {
        checkPacketPlayOutSetSlot(packetHandle);
        checkPacketPlayOutWindowItems(packetHandle);

        try {
            checkPacketPlayOutSpawnEntity(packetHandle);
        } catch (EntityNbtNotPermittedException e) {
            if (!panilla.getPConfig().disabledWorlds.contains(e.getWorld())) {
                stripNbtFromItemEntity(e.getEntityId());
                panilla.getPanillaLogger().log(panilla.getPTranslations().getTranslation("itemEntityStripped", e.getFailedNbt().key), true);
                throw e;
            }
        }
    }

    public void checkPacketPlayInClickContainer(Object packetHandle) throws NbtNotPermittedException {
        if (!(packetHandle instanceof ServerboundContainerClickPacket packet)) return;
        int windowId = packet.getContainerId();
        if (windowId != 0 && panilla.getPConfig().ignoreNonPlayerInventories) return;

        int slot = packet.getSlotNum();
        ItemStack item = packet.getCarriedItem();
        if (item == null || item.isEmpty() || item.getComponents().isEmpty()) return;

        NbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(item.getBukkitStack()).getCompound("components"));
        String itemClass = item.getClass().getName();
        String packetClass = "PacketPlayInWindowClick";

        NbtChecks.checkPacketPlayIn(slot, tag, itemClass, packetClass, panilla);
    }

    public void checkPacketPlayInSetCreativeSlot(Object packetHandle) throws NbtNotPermittedException {
        if (!(packetHandle instanceof ServerboundSetCreativeModeSlotPacket packet)) return;

        int slot = packet.slotNum();
        ItemStack item = packet.itemStack();
        if (item == null || item.isEmpty() || item.getComponents().isEmpty()) return;

        NbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(item.getBukkitStack()).getCompound("components"));
        String itemClass = item.getClass().getName();
        String packetClass = "PacketPlayInSetCreativeSlot";

        NbtChecks.checkPacketPlayIn(slot, tag, itemClass, packetClass, panilla);
    }

    public void checkPacketPlayOutSetSlot(Object packetHandle) throws NbtNotPermittedException {
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
        String packetClass = packet.getClass().getSimpleName();

        NbtChecks.checkPacketPlayOut(slot, tag, itemClass, packetClass, panilla);
    }

    public void checkPacketPlayOutWindowItems(Object packetHandle) throws NbtNotPermittedException {
        if (!(packetHandle instanceof ClientboundContainerSetContentPacket packet)) return;

        int windowId = packet.getContainerId();

        // check if window is not player inventory
        if (windowId != 0) {
            return;
        }

        List<ItemStack> itemStacks = packet.getItems();

        for (ItemStack itemStack : itemStacks) {
            if (!itemStack.isEmpty() || itemStack.getComponents().isEmpty()) {
                continue;
            }

            NbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(itemStack.asBukkitCopy()).getCompound("components"));
            String itemClass = itemStack.getClass().getSimpleName();
            String packetClass = packet.getClass().getSimpleName();

            NbtChecks.checkPacketPlayOut(0, tag, itemClass, packetClass, panilla); // TODO: set slot?
        }
    }

    public void checkPacketPlayOutSpawnEntity(Object packetHandle) throws EntityNbtNotPermittedException {
        if ((!(packetHandle instanceof ClientboundAddEntityPacket packet))) return;

        UUID entityId = packet.getUUID();
        Entity entity = null;

        for (ServerLevel worldServer : MinecraftServer.getServer().getAllLevels()) {
            entity = worldServer.moonrise$getEntityLookup().get(entityId);
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

    public void sendPacketPlayOutSetSlotAir(BukkitPanillaPlayer player, int slot) {
        CraftPlayer craftPlayer = (CraftPlayer) player.getHandle();
        ServerPlayer entityPlayer = craftPlayer.getHandle();
        ClientboundContainerSetSlotPacket packet = new ClientboundContainerSetSlotPacket(entityPlayer.containerMenu.containerId, entityPlayer.containerMenu.incrementStateId(), slot, new ItemStack(Blocks.AIR));
        entityPlayer.connection.send(packet);
    }

    public void stripNbtFromItemEntity(UUID entityId) {
        Entity entity = null;

        for (ServerLevel worldServer : MinecraftServer.getServer().getAllLevels()) {
            entity = worldServer.moonrise$getEntityLookup().get(entityId);
            if (entity != null) break;
        }

        if (entity instanceof ItemEntity item) {
            ItemStack itemStack = item.getItem();
            if (itemStack == null || itemStack.isEmpty() || itemStack.getComponents().isEmpty()) return;
            Iterator<TypedDataComponent<?>> iter = itemStack.getComponents().iterator();
            while (iter.hasNext()) iter.remove();
        }
    }

    public void stripNbtFromItemEntityLegacy(int entityId) {
        throw new RuntimeException("cannot use #stripNbtFromItemEntityLegacy on 1.20.6");
    }

    public void validateBaseComponentParse(String string) throws Exception {
        CraftChatMessage.fromJSON(string);
    }

}
