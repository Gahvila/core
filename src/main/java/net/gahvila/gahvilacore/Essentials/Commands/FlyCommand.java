package net.gahvila.gahvilacore.Essentials.Commands;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class FlyCommand {
    public void registerCommands() {
        new CommandAPICommand("fly")
                .withPermission("gahvilacore.command.fly")
                .executesPlayer((p, args) -> {
                    if (p.getAllowFlight()){
                        p.sendRichMessage("Lentotila: <red>pois päältä");
                        p.setAllowFlight(false);
                        p.setFlying(false);
                    } else {
                        p.sendRichMessage("Lentotila: <green>päällä");
                        p.setAllowFlight(true);

                        Vector velocity = p.getVelocity();
                        velocity.setY(0.5);
                        p.setVelocity(velocity);

                        Bukkit.getScheduler().runTaskLater(instance, () -> p.setFlying(true), 0);
                    }
                })
                .register();
    }
}
