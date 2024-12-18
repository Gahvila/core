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
