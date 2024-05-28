package net.gahvila.gahvilacore.Essentials.Commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import io.papermc.paper.ServerBuildInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class InfoCommands {

    public void registerCommands() {
        new CommandAPICommand("discord")
                .withAliases("dc")
                .executesPlayer((p, args) -> {
                    p.sendMessage(toMM("<white>Gahvilan Discordin osoite: <yellow><click:open_url:'https://dc.gahvila.net'>dc.gahvila.net</click></yellow>"));
                })
                .register();
        new CommandAPICommand("serverinfo")
                .executesPlayer((p, args) -> {
                    final ServerBuildInfo build = ServerBuildInfo.buildInfo();
                    List<String> plugins = new ArrayList<>();
                    for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
                        plugins.add(plugin.getName());
                    }

                    p.sendMessage(toMM("Tämä palvelin käyttää Minecraft-versiota <yellow>" + build.minecraftVersionName()
                            + " " + build.brandName() + "</yellow> <white>koontiversiota</white><yellow> " + build.buildNumber() + "</yellow><br><br>" +
                            "Palvelimella on <yellow>" + plugins.size() + "</yellow> lisäosaa: <yellow>" + String.join("<white>,<yellow> ", plugins)));
                })
                .register();
        CommandAPI.unregister("perf");
        new CommandAPICommand("perf")
                .executesPlayer((p, args) -> {
                    p.sendMessage(toMM("TPS: <yellow>" + Arrays.toString(Bukkit.getTPS()) + "</yellow><br>" +
                            "MSPT: <gold>" + Bukkit.getAverageTickTime() + "</gold>"));
                })
                .register();
        new CommandAPICommand("rules")
                .withAliases("säännöt")
                .executesPlayer((p, args) -> {
                    p.sendMessage(Component.text("Säännöt voidaan lyhentää helposti kolmeen sanaan, “älä oo tyhmä.”\n" +
                            "\n" +
                            "» 1. Asiaton käytös: Ikinä ei ole pakko aiheuttaa kenellekään huonoa fiilistä, vaikka saattaakin tuntua mahdottomalta väitteeltä.\n" +
                            "\n" +
                            "» 2. Mainostaminen: Älä mainosta muita Minecraft palvelimia taikka muita Discord palvelimia.\n" +
                            "\n" +
                            "» 3. Maalaisjärki: Maalaisjärjen käyttäminen on tärkeä taito, joka auttaa monia toimimaan oikein."));
                })
                .register();
    }
}
