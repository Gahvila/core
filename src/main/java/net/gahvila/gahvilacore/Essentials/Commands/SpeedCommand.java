package net.gahvila.gahvilacore.Essentials.Commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class SpeedCommand {

    public void registerCommands() {
        new CommandAPICommand("speed")
                .withArguments(new IntegerArgument("nopeus", 1, 5))
                .withOptionalArguments(new PlayerArgument("pelaaja"))
                .executesPlayer((p, args) -> {
                    int speed = (int) args.get("nopeus");
                    Player player;
                    if (args.get("pelaaja") != null && p.hasPermission("gahvilacore.gamemode.others")) {
                        player = (Player) args.get("pelaaja");
                    } else {
                        player = p;
                    }
                    switch (speed) {
                        case 1:
                            p.setWalkSpeed(0.2F);
                            p.setFlySpeed(0.1F);
                            break;
                        case 2:
                            p.setWalkSpeed(0.3F);
                            p.setFlySpeed(0.2F);
                            break;
                        case 3:
                            p.setWalkSpeed(0.4F);
                            p.setFlySpeed(0.3F);
                            break;
                        case 4:
                            p.setWalkSpeed(0.5F);
                            p.setFlySpeed(0.4F);
                            break;
                        case 5:
                            p.setWalkSpeed(0.6F);
                            p.setFlySpeed(0.5F);
                            break;
                    }

                    if (p == player) {
                        p.sendMessage(toMM("Asetit nopeuden <yellow>" + speed + "</yellow> itsellesi."));
                    } else {
                        p.sendMessage(toMM("Asetit nopeuden <yellow>" + speed + "</yellow> pelaajalle <yellow>" + player.getName() + "</yellow>."));
                    }
                })
                .register();
    }
}
