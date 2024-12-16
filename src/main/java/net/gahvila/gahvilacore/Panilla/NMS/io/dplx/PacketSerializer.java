package net.gahvila.gahvilacore.Panilla.NMS.io.dplx;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

public class PacketSerializer {

    private final FriendlyByteBuf handle;

    public PacketSerializer(ByteBuf byteBuf) {
        this.handle = new FriendlyByteBuf(byteBuf);
    }

    public int readableBytes() {
        return handle.readableBytes();
    }

    public int readVarInt() {
        return handle.readVarInt();
    }

    public ByteBuf readBytes(int i) {
        return handle.readBytes(i);
    }

    public ByteBuf readBytes(byte[] buffer) {
        return handle.readBytes(buffer);
    }

}
