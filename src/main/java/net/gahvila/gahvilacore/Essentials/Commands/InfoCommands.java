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

import static net.gahvila.gahvilacore.API.Utils.MiniMessageUtils.toMM;

public class InfoCommands {

    public void registerCommands() {
        new CommandAPICommand("discord")
                .withAliases("dc")
                .executesPlayer((p, args) -> {
                    p.sendRichMessage("<white>Gahvilan Discordin osoite: <yellow><click:open_url:'https://dc.gahvila.net'>dc.gahvila.net</click></yellow>");
                })
                .register();
        new CommandAPICommand("serverinfo")
                .executesPlayer((p, args) -> {
                    final ServerBuildInfo build = ServerBuildInfo.buildInfo();
                    List<String> plugins = new ArrayList<>();
                    for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
                        plugins.add(plugin.getName());
                    }
                    int buildNumber = build.buildNumber().isPresent() ? build.buildNumber().getAsInt() : 0;
                    p.sendRichMessage("Tämä palvelin käyttää <yellow>" + build.brandName()
                            + " " + build.minecraftVersionName() + "</yellow> <white>koontiversiota</white><yellow> " + buildNumber + "</yellow>.<br><br>" +
                            "Palvelimella on <yellow>" + plugins.size() + "</yellow> lisäosaa: <yellow>" + String.join("<white>,<yellow> ", plugins));
                })
                .register();
        CommandAPI.unregister("perf");
        new CommandAPICommand("perf")
                .executesPlayer((p, args) -> {
                    p.sendRichMessage("TPS [1m, 5m, 15m]: <yellow>" + Arrays.toString(Bukkit.getTPS()) + "</yellow><br>" +
                            "MSPT: <gold>" + Bukkit.getAverageTickTime() + "</gold>");
                })
                .register();
        new CommandAPICommand("rules")
                .withAliases("säännöt")
                .executesPlayer((p, args) -> {
                    p.sendRichMessage("<white>Säännöt löytyvät osoitteesta: <yellow><click:open_url:'https://gahvila.net/rules'>gahvila.net/rules</click></yellow>");
                })
                .register();
    }
}
