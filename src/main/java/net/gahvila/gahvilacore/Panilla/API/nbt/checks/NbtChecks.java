package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.exception.FailedNbt;
import net.gahvila.gahvilacore.Panilla.API.exception.FailedNbtList;
import net.gahvila.gahvilacore.Panilla.API.exception.NbtNotPermittedException;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagList;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NbtChecks {

    private static final Map<String, NbtCheck> checks = new HashMap<>();

    static {
        // vanilla
        register(new NbtCheck_Enchantments());
        register(new NbtCheck_BlockEntityData());
        register(new NbtCheck_Container());
        register(new NbtCheck_ContainerLoot());
        register(new NbtCheck_CustomName());
        register(new NbtCheck_Fireworks());
        register(new NbtCheck_Lock());
        register(new NbtCheck_Lore());
        register(new NbtCheck_PaperRange());
        register(new NbtCheck_WritableBookContent());
        register(new NbtCheck_WrittenBookContent());
        register(new NbtCheck_EntityData());
        register(new NbtCheck_Unbreakable());
        register(new NbtCheck_CanDestroy());
        register(new NbtCheck_CanPlaceOn());
        register(new NbtCheck_BlockEntityTag());
        register(new NbtCheck_BlockStateTag());
        register(new NbtCheck_RepairCost());
        register(new NbtCheck_AttributeModifiers());
        register(new NbtCheck_CustomPotionEffects());
        register(new NbtCheck_Potion());
        register(new NbtCheck_CustomPotionColor());
        register(new NbtCheck_display());
        register(new NbtCheck_HideFlags());
        register(new NbtCheck_resolved());
        register(new NbtCheck_generation());
        register(new NbtCheck_author());
        register(new NbtCheck_title());
        register(new NbtCheck_pages());
        register(new NbtCheck_SkullOwner());
        register(new NbtCheck_SkullProfile());
        register(new NbtCheck_Explosion());
        register(new NbtCheck_Fireworks());
        register(new NbtCheck_EntityTag());
        register(new NbtCheck_BucketVariantTag());
        register(new NbtCheck_map());
        register(new NbtCheck_map_scale_direction());
        register(new NbtCheck_Decorations());
        register(new NbtCheck_Effects());
        register(new NbtCheck_CustomModelData());
        register(new NbtCheck_HasVisualFire()); // 1.17
        register(new NbtCheck_ChargedProjectiles());
        register(new NbtCheck_Items());
        // non-vanilla
        register(new NbtCheck_weBrushJson());
    }

    private static void register(NbtCheck check) {
        checks.put(check.getName(), check);
        for (String alias : check.getAliases()) checks.put(alias, check);
    }

    public static Map<String, NbtCheck> getChecks() {
        return checks;
    }

    public static void checkPacketPlayIn(int slot, NbtTagCompound tag, String nmsItemClassName, String nmsPacketClassName,
                                         PanillaPlugin panilla) throws NbtNotPermittedException {
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

    public static void checkPacketPlayOut(int slot, NbtTagCompound tag, String nmsItemClassName, String nmsPacketClassName,
                                          PanillaPlugin panilla) throws NbtNotPermittedException {
        FailedNbtList failedNbtList = checkAll(tag, nmsItemClassName, panilla);

        if (failedNbtList.containsCritical()) {
            throw new NbtNotPermittedException(nmsPacketClassName, false, failedNbtList.getCritical(), slot);
        }

        FailedNbt failedNbt = failedNbtList.findFirstNonCritical();

        if (failedNbt != null) {
            throw new NbtNotPermittedException(nmsPacketClassName, false, failedNbt, slot);
        }
    }

    private static boolean tagMeetsKeyThreshold(NbtTagCompound tag, PanillaPlugin panilla) {
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

    public static FailedNbtList checkAll(NbtTagCompound tag, String nmsItemClassName, PanillaPlugin panilla) {
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
