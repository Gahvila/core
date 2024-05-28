package net.gahvila.gahvilacore.Essentials.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Et ole pelaaja!");
            return true;
        }
        
        Player p = (Player) sender;
        if (!p.hasPermission("gahvilacore.speed")) {
            sender.sendMessage("Sinulla ei ole oikeuksia tuohon.");
            return true;
        }
        switch (cmd.getName()){
            case "walkspeed":
                if (args.length == 1) {
                    switch (args[0]) {
                        case "1":
                            p.setWalkSpeed(0.2F);
                            p.sendMessage("§eKävelynopeus §6§l→ §e1");
                            break;
                        case "2":
                            p.setWalkSpeed(0.3F);
                            p.sendMessage("§eKävelynopeus §6§l→ §e2");
                            break;
                        case "3":
                            p.setWalkSpeed(0.4F);
                            p.sendMessage("§eKävelynopeus §6§l→ §e3");
                            break;
                        case "4":
                            if (p.hasPermission("setspeed.4")) {
                                p.setWalkSpeed(0.4F);
                                p.sendMessage("§eKävelynopeus §6§l→ §e4");
                            } else {
                                p.sendMessage("§fVoit vain käyttää nopeuksia 1-3. VIP-arvolla voit käyttää 1-5.");
                            }
                            break;
                        case "5":
                            if (p.hasPermission("setspeed.5")) {
                                p.setWalkSpeed(0.5F);
                                p.sendMessage("§eKävelynopeus §6§l→ §e5");
                                return false;
                            } else {
                                p.sendMessage("§fVoit vain käyttää nopeuksia 1-3. VIP-arvolla voit käyttää 1-5.");
                            }
                        default:
                            p.sendMessage("§6§lNopeudet");
                            p.sendMessage(" §eNormaali pelaaja: §f1-3");
                            p.sendMessage(" §eVIP: §f1-5");
                    }
                    return false;
                } else {
                    p.sendMessage("§6§lNopeudet");
                    p.sendMessage(" §eNormaali pelaaja: §f1-3");
                    p.sendMessage(" §eVIP: §f1-5");
                }
                break;
            case "flightspeed":
                if (args.length == 1) {
                    switch (args[0]) {
                        case "1":
                            p.setFlySpeed(0.1F);
                            p.sendMessage("§eLentonopeus §6§l→ §e1");
                            break;
                        case "2":
                            p.setFlySpeed(0.2F);
                            p.sendMessage("§eLentonopeus §6§l→ §e2");
                            break;
                        case "3":
                            p.setFlySpeed(0.3F);
                            p.sendMessage("§eLentonopeus §6§l→ §e3");
                            break;
                        case "4":
                            if (p.hasPermission("setspeed.4")) {
                                p.setFlySpeed(0.4F);
                                p.sendMessage("§eLentonopeus §6§l→ §e4");
                            } else {
                                p.sendMessage("§fVoit vain käyttää nopeuksia 1-3. VIP-arvolla voit käyttää 1-5.");
                            }
                            break;
                        case "5":
                            if (p.hasPermission("setspeed.5")) {
                                p.setFlySpeed(0.5F);
                                p.sendMessage("§eLentonopeus §6§l→ §e5");
                                return false;
                            } else {
                                p.sendMessage("§fVoit vain käyttää nopeuksia 1-3. VIP-arvolla voit käyttää 1-5.");
                            }
                        default:
                            p.sendMessage("§6§lNopeudet");
                            p.sendMessage(" §eNormaali pelaaja: §f1-3");
                            p.sendMessage(" §eVIP: §f1-5");
                    }
                    return false;
                } else {
                    p.sendMessage("§6§lNopeudet");
                    p.sendMessage(" §eNormaali pelaaja: §f1-3");
                    p.sendMessage(" §eVIP: §f1-5");
                }
                break;
            case "speed":
                if (args.length == 1) {
                    switch (args[0]) {
                        case "1":
                            p.setWalkSpeed(0.2F);
                            p.setFlySpeed(0.1F);
                            p.sendMessage("§eNopeus §6§l→ §e1");
                            break;
                        case "2":
                            p.setWalkSpeed(0.3F);
                            p.setFlySpeed(0.2F);
                            p.sendMessage("§eNopeus §6§l→ §e2");
                            break;
                        case "3":
                            p.setWalkSpeed(0.4F);
                            p.setFlySpeed(0.3F);
                            p.sendMessage("§eNopeus §6§l→ §e3");
                            break;
                        case "4":
                            if (p.hasPermission("setspeed.4")) {
                                p.setWalkSpeed(0.4F);
                                p.setFlySpeed(0.4F);
                                p.sendMessage("§eNopeus §6§l→ §e4");
                            } else {
                                p.sendMessage("§fVoit vain käyttää nopeuksia 1-3. VIP-arvolla voit käyttää 1-5.");
                            }
                            break;
                        case "5":
                            if (p.hasPermission("setspeed.5")) {
                                p.setWalkSpeed(0.5F);
                                p.setFlySpeed(0.5F);
                                p.sendMessage("§eNopeus §6§l→ §e5");
                                return false;
                            } else {
                                p.sendMessage("§fVoit vain käyttää nopeuksia 1-3. VIP-arvolla voit käyttää 1-5.");
                            }
                        case "6":
                            if (p.hasPermission("setspeed.6")) {
                                p.setWalkSpeed(0.6F);
                                p.setFlySpeed(0.6F);
                                p.sendMessage("§eNopeus §6§l→ §e6");
                                return false;
                            } else {
                                p.sendMessage("§fVoit vain käyttää nopeuksia 1-5.");
                            }
                        case "7":
                            if (p.hasPermission("setspeed.7")) {
                                p.setWalkSpeed(0.7F);
                                p.setFlySpeed(0.7F);
                                p.sendMessage("§eNopeus §6§l→ §e7");
                                return false;
                            } else {
                                p.sendMessage("§fVoit vain käyttää nopeuksia 1-5.");
                            }
                        case "8":
                            if (p.hasPermission("setspeed.8")) {
                                p.setWalkSpeed(0.8F);
                                p.setFlySpeed(0.8F);
                                p.sendMessage("§eNopeus §6§l→ §e8");
                                return false;
                            } else {
                                p.sendMessage("§fVoit vain käyttää nopeuksia 1-5.");
                            }
                        case "9":
                            if (p.hasPermission("setspeed.9")) {
                                p.setWalkSpeed(0.9F);
                                p.setFlySpeed(0.9F);
                                p.sendMessage("§eNopeus §6§l→ §e9");
                                return false;
                            } else {
                                p.sendMessage("§fVoit vain käyttää nopeuksia 1-5.");
                            }
                        case "10":
                            if (p.hasPermission("setspeed.10")) {
                                p.setWalkSpeed(1.0F);
                                p.setFlySpeed(1.0F);
                                p.sendMessage("§eNopeus §6§l→ §e10");
                                return false;
                            } else {
                                p.sendMessage("§fVoit vain käyttää nopeuksia 1-5.");
                            }
                        default:
                            p.sendMessage("§6§lNopeudet");
                            p.sendMessage(" §eNormaali pelaaja: §f1-3");
                            p.sendMessage(" §eVIP: §f1-5");
                    }
                    return false;
                } else {
                    p.sendMessage("§6§lNopeudet");
                    p.sendMessage(" §eNormaali pelaaja: §f1-3");
                    p.sendMessage(" §eVIP: §f1-5");
                }
                break;
        }return true;
    }
}
