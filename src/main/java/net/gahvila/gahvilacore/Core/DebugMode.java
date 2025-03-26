package net.gahvila.gahvilacore.Core;

import net.gahvila.gahvilacore.GahvilaCore;

public class DebugMode {

    private static Boolean isDebugging = false;

    public static boolean isDebugging() {
        return isDebugging != null && isDebugging;
    }

    public static void setDebugging(boolean debug) {
        isDebugging = debug;
    }

    public static void logDebugMessage(String message) {
        if (isDebugging()) {
            GahvilaCore.instance.getLogger().info("[DEBUG] " + message);
        }
    }
}