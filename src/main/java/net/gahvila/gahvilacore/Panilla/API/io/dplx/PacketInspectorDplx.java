package net.gahvila.gahvilacore.Panilla.API.io.dplx;

import net.gahvila.gahvilacore.Panilla.API.exception.FailedNbt;
import net.gahvila.gahvilacore.Panilla.API.exception.PacketException;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.gahvila.gahvilacore.Panilla.PanillaLogger;
import net.gahvila.gahvilacore.Panilla.PanillaPlayer;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class PacketInspectorDplx extends ChannelDuplexHandler {

    private final PanillaPlugin panilla;
    private final PanillaPlayer player;

    public PacketInspectorDplx(PanillaPlugin panilla, PanillaPlayer player) {
        this.panilla = panilla;
        this.player = player;
    }

    // player -> server
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            panilla.getPacketInspector().checkServerbound(panilla, player, msg);
        } catch (PacketException e) {
            if (handlePacketException(player, e)) {
                return;
            }
        }

        super.channelRead(ctx, msg);
    }

    // server -> player
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            panilla.getPacketInspector().checkClientbound(panilla, msg);
        } catch (PacketException e) {
            if (handlePacketException(player, e)) {
                return;
            }
        }

        try {
            super.write(ctx, msg, promise);
        } catch (IllegalArgumentException e) {
            // java.lang.IllegalArgumentException: Packet too big (is 10606067, should be less than 8388608): net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket@20e05249
            panilla.getPanillaLogger().info("Dropped packet to " + player.getName() + " :: " + e.getMessage(), false);
        }
    }

    private boolean handlePacketException(PanillaPlayer player, PacketException e) {
        if (!player.canBypassChecks(panilla, e)) {
            panilla.getInventoryCleaner().clean(player);

            PanillaLogger panillaLogger = panilla.getPanillaLogger();

            String nmsClass = e.getNmsClass();
            String username = player.getName();
            String tag;

            if (FailedNbt.failsThreshold(e.getFailedNbt())) {
                tag = "key size threshold";
            } else {
                tag = e.getFailedNbt().key;
            }

            final String message;

            if (e.isFrom()) {
                message = "Packet of type " + nmsClass + " from player " + username + " was dropped because NBT tag " + tag + " failed checks";
            } else {
                message = "Packet of type " + nmsClass + " to player " + username + " was dropped because NBT tag " + tag + " failed checks";
            }

            panillaLogger.log(message, true);

            return true;
        }

        return false;
    }

}
