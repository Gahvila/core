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
package net.gahvila.gahvilacore.Panilla.API;

import net.gahvila.gahvilacore.Panilla.API.Annotations.ProtocolLimit;
import net.minecraft.network.CompressionDecoder;

public interface ProtocolConstants {

    default int maxUsernameLength() {
        return 16;
    }

    default int maxBookTitleLength() {
        return 16;
    }

    default int maxAnvilRenameChars() {
        return 35;
    }

    default int maxFireworksFlight() {
        return 3;
    }

    default int minFireworksFlight() {
        return 1;
    }

    default int maxFireworksExplosions() {
        return 8;
    }

    default int maxSlimeSize() {
        return 3; // large slime
    }

    default int maxBookPages() {
        return 100;
    }

    @ProtocolLimit(description = "Maximum length for entity custom name.")
    default int maxEntityTagCustomNameLength() {
        return 64;
    }

    @ProtocolLimit(description = "Maximum length for item name.")
    default int maxItemNameLength() {
        return 128;
    }

    @ProtocolLimit(description = "Maximum number of lore lines for items.")
    default int maxLoreLines() {
        return 48;
    }

    @ProtocolLimit(description = "Maximum length for a lore line, based on item name length.")
    default int maxLoreLineLength() {
        return maxItemNameLength() * 16;
    }

    @ProtocolLimit(description = "Maximum length in bytes for block entity tag.")
    default int maxBlockEntityTagLengthBytes() {
        return (Short.MAX_VALUE * 2 + 1) * 4;
    }

}
