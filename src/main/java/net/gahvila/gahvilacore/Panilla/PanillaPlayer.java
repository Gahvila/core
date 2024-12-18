package net.gahvila.gahvilacore.Panilla;

import net.gahvila.gahvilacore.Panilla.API.config.PConfig;
import net.gahvila.gahvilacore.Panilla.API.exception.PacketException;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;
import org.bukkit.entity.Player;

public class PanillaPlayer {

    private final Player handle;

    public PanillaPlayer(Player handle) {
        this.handle = handle;
    }

    public Player getHandle() {
        return handle;
    }

    public String getName() {
        return handle.getName();
    }

    public String getCurrentWorldName() {
        return handle.getWorld().getName();
    }

    public boolean hasPermission(String node) {
        return handle.hasPermission(node);
    }

    public boolean canBypassChecks(Panilla panilla, PacketException e) {
        if (e.getFailedNbt().result == NbtCheck.NbtCheckResult.CRITICAL) {
            return false;   // to prevent crash exploits
        }

        boolean inDisabledWorld = panilla.getPConfig().disabledWorlds.contains(getCurrentWorldName());

        return inDisabledWorld || hasPermission(PConfig.PERMISSION_BYPASS);
    }

}
