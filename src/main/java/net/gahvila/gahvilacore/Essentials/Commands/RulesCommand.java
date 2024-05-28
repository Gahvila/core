package net.gahvila.gahvilacore.Essentials.Commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage(Component.text("Säännöt voidaan lyhentää helposti kolmeen sanaan, “älä oo tyhmä.”\n" +
                    "\n" +
                    "» 1. Asiaton käytös: Ikinä ei ole pakko aiheuttaa kenellekään huonoa fiilistä, vaikka saattaakin tuntua mahdottomalta väitteeltä.\n" +
                    "\n" +
                    "» 2. Mainostaminen: Älä mainosta muita Minecraft palvelimia taikka muita Discord palvelimia.\n" +
                    "\n" +
                    "» 3. Maalaisjärki: Maalaisjärjen käyttäminen on tärkeä taito, joka auttaa monia toimimaan oikein."));
        }
        return false;
    }
}
