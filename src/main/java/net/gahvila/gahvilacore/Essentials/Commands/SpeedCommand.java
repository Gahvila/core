package net.gahvila.gahvilacore.Essentials.Commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;

public class SpeedCommand {

    public void registerCommands() {
        new CommandAPICommand("speed")
                .withPermission("gahvilacore.command.speed")
                .withArguments(new IntegerArgument("nopeus", 1, 5))
                .withOptionalArguments(new PlayerArgument("pelaaja"))
                .executesPlayer((p, args) -> {
                    int speed = (int) args.get("nopeus");
                    Player player;
                    if (args.get("pelaaja") != null && p.hasPermission("gahvilacore.command.speed.others")) {
                        player = (Player) args.get("pelaaja");
                    } else {
                        player = p;
                    }
                    assert player != null;
                    switch (speed) {
                        case 1:
                            player.setWalkSpeed(0.2F);
                            player.setFlySpeed(0.1F);
                            break;
                        case 2:
                            player.setWalkSpeed(0.3F);
                            player.setFlySpeed(0.2F);
                            break;
                        case 3:
                            player.setWalkSpeed(0.4F);
                            player.setFlySpeed(0.3F);
                            break;
                        case 4:
                            player.setWalkSpeed(0.5F);
                            player.setFlySpeed(0.4F);
                            break;
                        case 5:
                            player.setWalkSpeed(0.6F);
                            player.setFlySpeed(0.5F);
                            break;
                    }

                    if (p == player) {
                        p.sendRichMessage("Asetit nopeuden <yellow>" + speed + "</yellow> itsellesi.");
                    } else {
                        p.sendRichMessage(("Asetit nopeuden <yellow>" + speed + "</yellow> pelaajalle <yellow>" + player.getName() + "</yellow>."));
                    }
                })
                .register();
    }
}
