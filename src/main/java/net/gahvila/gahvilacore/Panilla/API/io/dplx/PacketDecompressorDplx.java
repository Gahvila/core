package net.gahvila.gahvilacore.Panilla.API.io.dplx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.gahvila.gahvilacore.Panilla.PanillaPlayer;
import net.gahvila.gahvilacore.Panilla.NMS.io.dplx.PacketSerializer;
import net.gahvila.gahvilacore.Panilla.Panilla;

import java.util.List;
import java.util.zip.Inflater;

public class PacketDecompressorDplx extends ByteToMessageDecoder {

    private final Panilla panilla;
    private final PanillaPlayer player;
    private final int minBytes;
    private final int maxBytes;
    private final Inflater inflater;

    public PacketDecompressorDplx(Panilla panilla, PanillaPlayer player) {
        this.panilla = panilla;
        this.player = player;
        this.minBytes = panilla.getProtocolConstants().minPacketSize();
        this.maxBytes = panilla.getProtocolConstants().maxPacketSize();
        this.inflater = new Inflater();
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        final int readableBytes = byteBuf.readableBytes();

        if (readableBytes <= 0) {
            return;
        }

        PacketSerializer packetSerializer = panilla.createPacketSerializer(byteBuf);

        int packetLength = packetSerializer.readVarInt();

        // uncompressed packet
        if (packetLength == 0) {
            list.add(packetSerializer.readBytes(packetSerializer.readableBytes()));
        }

        // compressed packet
        else if (packetLength > 0) {
            if (packetLength > maxBytes) {
                panilla.getPanillaLogger().log("Prevented player %s from being kicked due to exception oversized packet", true);
                return;
            }

            if (packetLength < minBytes) {
                panilla.getPanillaLogger().log("Prevented player %s from being kicked due to exception undersized packet", true);
                return;
            }

            byte[] buffer = new byte[packetSerializer.readableBytes()];

            packetSerializer.readBytes(buffer);

            inflater.setInput(buffer);

            byte[] data = new byte[packetLength];

            inflater.inflate(data);

            list.add(Unpooled.wrappedBuffer(data));

            inflater.reset();
        }

        // a negative length packet? ignore it.
        else {
            panilla.getPanillaLogger().log("Prevented player " + player.getName() + " from being kicked due to exception invalid packet length", true);
        }
    }

}
