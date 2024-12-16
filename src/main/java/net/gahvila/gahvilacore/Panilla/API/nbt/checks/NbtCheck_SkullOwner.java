package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;

import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagList;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class NbtCheck_SkullOwner extends NbtCheck {

    public NbtCheck_SkullOwner() {
        super("minecraft:profile", PStrictness.LENIENT);
    }

    public static final Pattern URL_MATCHER = Pattern.compile("url");

    public static UUID minecraftSerializableUuid(final int[] ints) {
        return new UUID((long) ints[0] << 32 | ((long) ints[1] & 0xFFFFFFFFL), (long) ints[2] << 32 | ((long) ints[3] & 0xFFFFFFFFL));
    }

    @Override
    public NbtCheck.NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        NbtTagCompound skullOwner = tag.getCompound("minecraft:profile");

        if (skullOwner.hasKey("name")) {
            String name = skullOwner.getString("name");

            if (name.length() > 64) {
                return NbtCheckResult.CRITICAL;
            }
        }

        // id or Id UUID string
        if (skullOwner.hasKeyOfType("id", NbtDataType.STRING)) {
            String uuidString = skullOwner.getString("id");

            try {
                // Ensure valid UUID
                UUID.fromString(uuidString);
            } catch (Exception e) {
                return NbtCheckResult.CRITICAL;
            }
        }
        if (skullOwner.hasKeyOfType("Id", NbtDataType.STRING)) {
            String uuidString = skullOwner.getString("Id");

            try {
                // Ensure valid UUID
                UUID.fromString(uuidString);
            } catch (Exception e) {
                return NbtCheckResult.CRITICAL;
            }
        }

        // id or Id int array
        if (skullOwner.hasKeyOfType("Id", NbtDataType.INT_ARRAY)) {
            int[] ints = skullOwner.getIntArray("Id");

            try {
                UUID check = minecraftSerializableUuid(ints);
            } catch (Exception e) {
                return NbtCheckResult.CRITICAL;
            }
        }
        if (skullOwner.hasKeyOfType("id", NbtDataType.INT_ARRAY)) {
            int[] ints = skullOwner.getIntArray("id");

            try {
                UUID check = minecraftSerializableUuid(ints);
            } catch (Exception e) {
                return NbtCheckResult.CRITICAL;
            }
        }

        if (panilla.getPConfig().preventMinecraftEducationSkulls) {
            if (skullOwner.hasKey("properties")) {
                NbtTagList properties = skullOwner.getList("properties");
                for (int i = 0; i < properties.size(); i++) {
                    NbtTagCompound entry = properties.getCompound(i);
                    if (!"textures".equals(entry.getString("name"))) continue;

                    String b64 = entry.getString("value");
                    String decoded;

                    try {
                        decoded = new String(Base64.getDecoder().decode(b64));
                    } catch (IllegalArgumentException e) {
                        panilla.getPanillaLogger().warning("Invalid head texture", false);
                        return NbtCheckResult.CRITICAL;
                    }

                    // all lowercase, no parentheses or spaces
                    decoded = decoded.trim()
                            .replace(" ", "")
                            .replace("\"", "")
                            .toLowerCase();

                    Matcher matcher = URL_MATCHER.matcher(decoded);

                    // example: {textures:{SKIN:{url:https://education.minecraft.net/wp-content/uploads/deezcord.png}}}
                    // may contain multiple url tags
                    while (matcher.find()) {
                        String url = decoded.substring(matcher.end() + 1);

                        if (url.startsWith("http://textures.minecraft.net") ||
                                url.startsWith("https://textures.minecraft.net")) {
                            continue;
                        } else {
                            return NbtCheckResult.FAIL;
                        }
                    }
                }
            }
        }

        return NbtCheckResult.PASS;
    }

}