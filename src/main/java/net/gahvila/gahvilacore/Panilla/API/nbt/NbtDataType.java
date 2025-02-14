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
package net.gahvila.gahvilacore.Panilla.API.nbt;

public enum NbtDataType {

    END(0x0), BYTE(0x1), SHORT(0x2), INT(0x3), LONG(0x4), FLOAT(0x5), DOUBLE(0x6), BYTE_ARRAY(0x7),
    STRING(0x8), LIST(0x9), COMPOUND(0xA), INT_ARRAY(0xB);

    public final int id;

    NbtDataType(int id) {
        this.id = id;
    }

    public static NbtDataType fromId(int id) {
        for (NbtDataType type : values()) {
            if (type.id == id) {
                return type;
            }
        }

        return null;
    }

}
