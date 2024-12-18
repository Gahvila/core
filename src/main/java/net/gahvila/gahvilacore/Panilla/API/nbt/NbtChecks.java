package net.gahvila.gahvilacore.Panilla.API.nbt;

import net.gahvila.gahvilacore.Panilla.API.exception.FailedNbt;
import net.gahvila.gahvilacore.Panilla.API.exception.FailedNbtList;
import net.gahvila.gahvilacore.Panilla.API.exception.NbtNotPermittedException;
import net.gahvila.gahvilacore.Panilla.API.nbt.checks.*;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagList;
import net.gahvila.gahvilacore.Panilla.Panilla;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NbtChecks {

    private static final Map<String, NbtCheck> checks = new HashMap<>();

    static {
        // vanilla
        register(new attribute_modifiers());
        register(new block_entity_data());
        register(new block_state());
        register(new bucket_entity_data());
        register(new can_break());
        register(new can_place_on());
        register(new charged_projectiles());
        register(new container());
        register(new container_loot());
        register(new custom_data());
        register(new custom_model_data());
        register(new custom_name());
        register(new enchantments());
        register(new entity_data());
        register(new fireworks());
        register(new hide_tooltip());
        register(new lock());
        register(new lore());
        register(new potion_contents());
        register(new profile());
        register(new unbreakable());
        register(new writable_book_content());
        register(new written_book_content());
    }

    private static void register(NbtCheck check) {
        checks.put(check.getName(), check);
        for (String alias : check.getAliases()) checks.put(alias, check);
    }

    public static Map<String, NbtCheck> getChecks() {
        return checks;
    }

    public static void checkServerbound(int slot, NbtTagCompound tag, String nmsItemClassName, String nmsPacketClassName,
                                        Panilla panilla) throws NbtNotPermittedException {
        List<FailedNbt> failedNbtList = checkAll(tag, nmsItemClassName, panilla);

        FailedNbt lastNonCritical = null;

        for (FailedNbt failedNbt : failedNbtList) {
            if (failedNbt.result == NbtCheck.NbtCheckResult.CRITICAL) {
                throw new NbtNotPermittedException(nmsPacketClassName, false, failedNbt, slot);
            } else if (FailedNbt.fails(failedNbt)) {
                lastNonCritical = failedNbt;
            }
        }

        if (lastNonCritical != null) {
            throw new NbtNotPermittedException(nmsPacketClassName, true, lastNonCritical, slot);
        }
    }

    public static void checkClientbound(int slot, NbtTagCompound tag, String nmsItemClassName, String nmsPacketClassName,
                                        Panilla panilla) throws NbtNotPermittedException {
        FailedNbtList failedNbtList = checkAll(tag, nmsItemClassName, panilla);

        if (failedNbtList.containsCritical()) {
            throw new NbtNotPermittedException(nmsPacketClassName, false, failedNbtList.getCritical(), slot);
        }

        FailedNbt failedNbt = failedNbtList.findFirstNonCritical();

        if (failedNbt != null) {
            throw new NbtNotPermittedException(nmsPacketClassName, false, failedNbt, slot);
        }
    }

    private static boolean tagMeetsKeyThreshold(NbtTagCompound tag, Panilla panilla) {
        int maxNonMinecraftKeys = panilla.getPConfig().maxNonMinecraftNbtKeys;

        if (tag.getNonMinecraftKeys().size() > maxNonMinecraftKeys) {
            return false;
        }

        for (String key : tag.getKeys()) {
            if (tag.hasKeyOfType(key, NbtDataType.COMPOUND)) {
                NbtTagCompound subTag = tag.getCompound(key);

                if (!tagMeetsKeyThreshold(subTag, panilla)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static FailedNbtList checkAll(NbtTagCompound tag, String nmsItemClassName, Panilla panilla) {
        FailedNbtList failedNbtList = new FailedNbtList();
        if (!tagMeetsKeyThreshold(tag, panilla)) {
            failedNbtList.add(FailedNbt.FAIL_KEY_THRESHOLD);
        }

        for (String key : tag.getKeys()) {
            if (panilla.getPConfig().nbtWhitelist.contains(key)) {
                continue;
            }

            if (tag.hasKeyOfType(key, NbtDataType.LIST)) {
                NbtTagList list = tag.getList(key);

                if (list.size() > 128) {
                    failedNbtList.add(new FailedNbt((key), NbtCheck.NbtCheckResult.CRITICAL));
                }
            }

            NbtCheck check = checks.get(key);

            if (check == null) {
                // a non-minecraft NBT tag
                continue;
            }

            if (check.getTolerance().ordinal() > panilla.getPConfig().strictness.ordinal()) {
                continue;
            }

            NbtCheck.NbtCheckResult result = check.check(tag, nmsItemClassName, panilla);

            if (result != NbtCheck.NbtCheckResult.PASS) {
                failedNbtList.add(new FailedNbt(key, result));
            }
        }

        return failedNbtList;
    }

}
