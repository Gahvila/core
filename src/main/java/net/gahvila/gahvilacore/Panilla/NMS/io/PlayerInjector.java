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
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.gahvila.gahvilacore.Panilla.API.io.dplx.PacketInspectorDplx;
import net.gahvila.gahvilacore.Panilla.PanillaPlayer;
import net.gahvila.gahvilacore.Panilla.Panilla;
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

    public void register(Panilla panilla, PanillaPlayer player) throws IOException {
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
