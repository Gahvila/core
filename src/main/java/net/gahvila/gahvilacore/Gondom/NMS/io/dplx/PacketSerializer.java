package net.gahvila.gahvilacore.Gondom.NMS.io.dplx;

import io.netty.buffer.ByteBuf;
import net.gahvila.gahvilacore.Gondom.API.io.IPacketSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class PacketSerializer implements IPacketSerializer {

    private final FriendlyByteBuf handle;

    public PacketSerializer(ByteBuf byteBuf) {
        this.handle = new FriendlyByteBuf(byteBuf);
    }

    @Override
    public int readableBytes() {
        return handle.readableBytes();
    }

    @Override
    public int readVarInt() {
        return handle.readVarInt();
    }

    @Override
    public ByteBuf readBytes(int i) {
        return handle.readBytes(i);
    }

    @Override
    public ByteBuf readBytes(byte[] buffer) {
        return handle.readBytes(buffer);
    }

}
