package net.gahvila.gahvilacore.Gondom.API.io;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.IPanillaPlayer;
import net.gahvila.gahvilacore.Gondom.API.exception.EntityNbtNotPermittedException;
import net.gahvila.gahvilacore.Gondom.API.exception.NbtNotPermittedException;
import net.gahvila.gahvilacore.Gondom.API.exception.PacketException;

import java.util.UUID;

public interface IPacketInspector {

    void checkPacketPlayInClickContainer(Object packetHandle) throws NbtNotPermittedException;

    void checkPacketPlayInSetCreativeSlot(Object packetHandle) throws NbtNotPermittedException;

    /**
     * Checks PacketPlayOutSetSlot
     * Should optionally return (depending on config) if the window id is not 0
     *
     * @param packetHandle
     * @throws NbtNotPermittedException
     */
    void checkPacketPlayOutSetSlot(Object packetHandle) throws NbtNotPermittedException;

    /**
     * Checks PacketPlayOutWindowItems
     * Should return if the window id is not 0 (the player's inventory)
     *
     * @param packetHandle
     * @throws NbtNotPermittedException
     */
    void checkPacketPlayOutWindowItems(Object packetHandle) throws NbtNotPermittedException;

    void checkPacketPlayOutSpawnEntity(Object packetHandle) throws EntityNbtNotPermittedException;

    void sendPacketPlayOutSetSlotAir(IPanillaPlayer player, int slot);

    void stripNbtFromItemEntity(UUID entityId);

    void stripNbtFromItemEntityLegacy(int entityId);

    void validateBaseComponentParse(String string) throws Exception;

    default void checkPlayIn(IPanilla panilla, IPanillaPlayer player, Object packetHandle) throws PacketException {
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

    default void checkPlayOut(IPanilla panilla, Object packetHandle) throws PacketException {
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

}
