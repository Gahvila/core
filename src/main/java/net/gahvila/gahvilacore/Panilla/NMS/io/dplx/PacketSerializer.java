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
