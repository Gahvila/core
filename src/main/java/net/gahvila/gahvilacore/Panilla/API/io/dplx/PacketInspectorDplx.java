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
package net.gahvila.gahvilacore.Panilla.API.io.dplx;

import net.gahvila.gahvilacore.Panilla.API.exception.FailedNbt;
import net.gahvila.gahvilacore.Panilla.API.exception.PacketException;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.gahvila.gahvilacore.Panilla.PanillaLogger;
import net.gahvila.gahvilacore.Panilla.PanillaPlayer;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class PacketInspectorDplx extends ChannelDuplexHandler {

    private final Panilla panilla;
    private final PanillaPlayer player;

    public PacketInspectorDplx(Panilla panilla, PanillaPlayer player) {
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
