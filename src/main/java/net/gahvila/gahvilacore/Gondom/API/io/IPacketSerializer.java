package net.gahvila.gahvilacore.Gondom.API.io;

import io.netty.buffer.ByteBuf;

public interface IPacketSerializer {

    int readableBytes();

    int readVarInt();

    ByteBuf readBytes(int i);

    ByteBuf readBytes(byte[] buffer);

}
