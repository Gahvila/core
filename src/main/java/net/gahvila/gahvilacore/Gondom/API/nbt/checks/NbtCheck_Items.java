package net.gahvila.gahvilacore.Gondom.API.nbt.checks;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.exception.FailedNbt;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagList;

// Similar to BlockEntityTag
// Used mainly for Bundles (1.17)
public class NbtCheck_Items extends NbtCheck {

    public NbtCheck_Items() {
        super("Items", PStrictness.LENIENT);
    }

    @Override
    public NbtCheck.NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        INbtTagList itemsTagList = tag.getList(getName());

        // Bundles should only have 64 items
        if (itemsTagList.size() > 64) {
            return NbtCheckResult.CRITICAL;
        }

        // Check for air or bundles
        // Can cause a server crash if bundles have air items
        // Do not allow nested bundles
        /*
            net.minecraft.ReportedException: Ticking player
            at net.minecraft.server.level.ServerPlayer.doTick(ServerPlayer.java:754) ~[?:?]
            at net.minecraft.server.network.ServerGamePacketListenerImpl.tick(ServerGamePacketListenerImpl.java:314) ~[?:?]
            at net.minecraft.network.Connection.tick(Connection.java:567) ~[?:?]
            at net.minecraft.server.network.ServerConnectionListener.tick(ServerConnectionListener.java:231) ~[?:?]
            at net.minecraft.server.MinecraftServer.tickChildren(MinecraftServer.java:1623) ~[paper-1.18.2.jar:git-Paper-388]
            at net.minecraft.server.dedicated.DedicatedServer.tickChildren(DedicatedServer.java:483) ~[paper-1.18.2.jar:git-Paper-388]
            at net.minecraft.server.MinecraftServer.tickServer(MinecraftServer.java:1456) ~[paper-1.18.2.jar:git-Paper-388]
            at net.minecraft.server.MinecraftServer.runServer(MinecraftServer.java:1226) ~[paper-1.18.2.jar:git-Paper-388]
            at net.minecraft.server.MinecraftServer.lambda$spin$0(MinecraftServer.java:316) ~[paper-1.18.2.jar:git-Paper-388]
            at java.lang.Thread.run(Thread.java:833) ~[?:?]
            Caused by: java.lang.IllegalArgumentException: item is null or air
         */
        for (int i = 0; i < itemsTagList.size(); i++) {
            if (itemsTagList.getCompound(i).hasKey("id")) {
                if (itemsTagList.getCompound(i).getString("id").equals("minecraft:air")) {
                    return NbtCheckResult.CRITICAL;
                } else if (itemsTagList.getCompound(i).getString("id").equals("minecraft:bundle")) {
                    return NbtCheckResult.CRITICAL;
                }
            }
        }

        FailedNbt failedNbt = NbtCheck_BlockEntityTag.checkItems(getName(), itemsTagList, itemName, panilla);

        if (FailedNbt.fails(failedNbt)) {
            return failedNbt.result;
        } else {
            return NbtCheck.NbtCheckResult.PASS;
        }
    }

}
