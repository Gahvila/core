package net.gahvila.gahvilacore.Gondom.API;

import net.gahvila.gahvilacore.Gondom.API.config.PConfig;
import net.gahvila.gahvilacore.Gondom.API.config.PTranslations;
import net.gahvila.gahvilacore.Gondom.API.io.IPacketInspector;
import net.gahvila.gahvilacore.Gondom.API.io.IPacketSerializer;
import net.gahvila.gahvilacore.Gondom.API.io.IPlayerInjector;

public interface IPanilla {

    PConfig getPConfig();

    PTranslations getPTranslations();

    IPanillaLogger getPanillaLogger();

    IProtocolConstants getProtocolConstants();

    IPlayerInjector getPlayerInjector();

    IPacketInspector getPacketInspector();

    IInventoryCleaner getInventoryCleaner();

    IEnchantments getEnchantments();

    IPacketSerializer createPacketSerializer(Object byteBuf);

    void exec(Runnable runnable);

}
