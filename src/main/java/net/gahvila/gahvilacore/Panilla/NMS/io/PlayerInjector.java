package net.gahvila.gahvilacore.Panilla.NMS.io;

import de.tr7zw.changeme.nbtapi.NBT;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.gahvila.gahvilacore.Panilla.API.io.dplx.PacketInspectorDplx;
import net.gahvila.gahvilacore.Panilla.PanillaPlayer;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.io.IOException;
import java.util.NoSuchElementException;

public class PlayerInjector {

    static {
        NBT.preloadApi();
    }

    String HANDLER_PANILLA_INSPECTOR = "panilla_inspector";

    public Channel getPlayerChannel(PanillaPlayer player) throws IllegalArgumentException {
        CraftPlayer craftPlayer = (CraftPlayer) player.getHandle();
        ServerPlayer entityPlayer = craftPlayer.getHandle();
        return entityPlayer.connection.connection.channel;
    }

    public int getCompressionLevel() {
        return 256;
    }

    public ByteToMessageDecoder getDecompressor() {
        return null;
    }

    public ByteToMessageDecoder getDecoder() {
        throw new RuntimeException("Not implemented");
    }

    public String getDecompressorHandlerName() {
        return "decompress";
    }

    public String getPacketHandlerName() {
        return "packet_handler";
    }

    public void register(PanillaPlugin panilla, PanillaPlayer player) throws IOException {
        Channel pChannel = getPlayerChannel(player);

        if (pChannel == null || !pChannel.isRegistered()) {
            return;
        }

        /* Inject packet inspector */
        ChannelHandler minecraftHandler = pChannel.pipeline().get(getPacketHandlerName());

        if (minecraftHandler != null && !(minecraftHandler instanceof PacketInspectorDplx)) {
            PacketInspectorDplx packetInspector = new PacketInspectorDplx(panilla, player);
            pChannel.pipeline().addBefore(getPacketHandlerName(), HANDLER_PANILLA_INSPECTOR, packetInspector);
        }
    }

    public void unregister(final PanillaPlayer player) throws IOException {
        Channel pChannel = getPlayerChannel(player);

        if (pChannel == null || !pChannel.isRegistered()) {
            return;
        }

        /* Remove packet inspector */
        ChannelHandler panillaHandler = pChannel.pipeline().get(HANDLER_PANILLA_INSPECTOR);

        if (panillaHandler instanceof PacketInspectorDplx) {
            try {
                pChannel.pipeline().remove(panillaHandler);
            } catch (NoSuchElementException e) {
                // We can safely ignore this. Even with the instanceof check, depending on circumstance, this can still happen
            }
        }
    }

}
