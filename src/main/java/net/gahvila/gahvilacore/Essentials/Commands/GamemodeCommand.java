package net.gahvila.gahvilacore.Essentials.Commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static net.gahvila.gahvilacore.API.Utils.MiniMessageUtils.toMM;


public class GamemodeCommand {

    public void registerCommands() {
        HashMap<String, GameMode> gamemodes = new HashMap<>();
        gamemodes.put("adventure", GameMode.ADVENTURE);
        gamemodes.put("creative", GameMode.CREATIVE);
        gamemodes.put("spectator", GameMode.SPECTATOR);
        gamemodes.put("survival", GameMode.SURVIVAL);

        CommandAPI.unregister("gamemode");
        for (Map.Entry<String, GameMode> entry : gamemodes.entrySet()) {
            new CommandAPICommand("gamemode")
                    .withAliases("gm")
                    .withPermission("gahvilacore.command.gamemode")
                    .withArguments(new LiteralArgument(entry.getKey()))
                    .withOptionalArguments(new PlayerArgument("pelaaja"))
                    .executesPlayer((p, args) -> {
                        String gamemode = entry.getKey();
                        Player player;
                        if (args.get("pelaaja") != null && p.hasPermission("gahvilacore.command.gamemode.others")) {
                            player = (Player) args.get("pelaaja");
                        } else {
                            player = p;
                        }

                        if (gamemode == null) {
                            p.sendRichMessage("Sinun täytyy syöttää kelvollinen pelitila.");
                            return;
                        }

                        player.setGameMode(entry.getValue());

                        if (p == player) {
                            p.sendRichMessage("Asetit pelitilan <yellow>" + gamemode + "</yellow> itsellesi.");
                        } else {
                            p.sendRichMessage("Asetit pelitilan <yellow>" + gamemode + "</yellow> pelaajalle <yellow>" + player.getName() + "</yellow>.");
                        }
                    })
                    .register();
        }
    }
}
