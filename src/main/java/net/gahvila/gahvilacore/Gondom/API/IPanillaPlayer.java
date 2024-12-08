package net.gahvila.gahvilacore.Gondom.API;

import net.gahvila.gahvilacore.Gondom.API.exception.PacketException;

public interface IPanillaPlayer {

    Object getHandle();

    String getName();

    String getCurrentWorldName();

    boolean hasPermission(String node);

    boolean canBypassChecks(IPanilla panilla, PacketException e);

}
