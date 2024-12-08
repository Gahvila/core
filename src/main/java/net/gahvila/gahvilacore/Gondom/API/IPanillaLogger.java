package net.gahvila.gahvilacore.Gondom.API;

public interface IPanillaLogger {

    void log(String message, boolean colorize);

    void info(String message, boolean colorize);

    void warning(String message, boolean colorize);

}
