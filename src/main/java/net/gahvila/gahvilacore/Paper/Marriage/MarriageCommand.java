package net.gahvila.gahvilacore.Paper.Marriage;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.gahvila.gahvilacore.Paper.GahvilaCorePaper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class MarriageCommand {

    private final MarriageMenu marriageMenu;
    private final MarriageManager marriageManager;

    public static HashMap<Player, Player> marry = new HashMap<>();

    public MarriageCommand(MarriageMenu marriageMenu, MarriageManager marriageManager) {
        this.marriageMenu = marriageMenu;
        this.marriageManager = marriageManager;

    }
    public void registerCommands() {
        new CommandAPICommand("marry")
                .withPermission("gahvilacore.marry")
                .withSubcommand(new CommandAPICommand("divorce")
                        .executesPlayer((player, args) -> {
                            if (marriageManager.isPlayerMarried(player)){
                                UUID uuid = UUID.fromString(marriageManager.getMarriageInfo(player, "uuid"));
                                String name = marriageManager.getMarriageInfo(player, "currentname");
                                player.sendMessage("Erosit " + name + ":sta.");
                                if (Bukkit.getOfflinePlayer(uuid).isOnline()){
                                    Player player2 = Bukkit.getPlayer(uuid);
                                    player2.sendMessage("Sydämesi, joka on sykkinyt yhdessä "+ player.getName() + ":n kanssa, on nyt kohdannut murhetta. Hän on päättänyt erota sinusta.");
                                }
                                marriageManager.breakMarriage(player.getUniqueId(), uuid);
                            }

                        }))
                .withSubcommand(new CommandAPICommand("invite")
                        .withArguments(new PlayerArgument("nimi"))
                        .executesPlayer((sender, args) -> {
                            if (marriageManager.isPlayerMarried(sender)) {
                                sender.sendMessage("Olet jo parisuhteessa.");
                                return;
                            }

                            Player receiver = (Player) args.get("nimi");

                            if (marriageManager.isPlayerMarried(receiver)) {
                                sender.sendMessage(receiver.getName() + " on jo parisuhteessa.");
                                return;
                            }

                            if (sender.getName().equals(receiver.getName())){
                                sender.sendMessage("Et voi mennä naimisiin kanssasi.");
                                return;
                            }

                            if (marry.containsKey(sender)) {
                                sender.sendMessage("Sinulla on jo aktiivinen kutsu.");
                                return;
                            }

                            marry.put(sender, receiver);
                            sender.sendMessage("Lähetit kutsun.");
                            receiver.sendMessage(toMiniMessage("\n| <yellow>" + sender.getName() + " </yellow>haluaa naimisiin kanssasi."));
                            receiver.sendMessage(toMiniMessage("<white>| Sinulla on <yellow>30 sekuntia<yellow> <white>aikaa hyväksyä."));
                            receiver.sendMessage(toMiniMessage("| <green><b>Hyväksy</b>: /marry acceptinvite " + sender.getName())
                                    .hoverEvent(HoverEvent.showText(toMiniMessage("Klikkaa hyväksyäksesi."))).clickEvent(ClickEvent.runCommand("/marry acceptinvite " + sender.getName())));
                            receiver.sendMessage(toMiniMessage("| <red><b>Kieltäydy</b>: /marry denyinvite " + sender.getName())
                                    .hoverEvent(HoverEvent.showText(toMiniMessage("Klikkaa kieltäytyäksesi"))).clickEvent(ClickEvent.runCommand("/marry denyinvite " + sender.getName())));

                            Bukkit.getServer().getScheduler().runTaskLater(GahvilaCorePaper.instance, new Runnable() {
                                @Override
                                public void run() {
                                    if (marry.get(sender) != null) {
                                        marry.remove(sender);
                                        sender.sendMessage("Sinun kutsu parisuhteeseen vanhentui.");
                                        receiver.sendMessage(toMiniMessage("<yellow>" + receiver.getName() + ":n </yellow>lähettämä kutsu vanhentui."));
                                    }
                                }
                            }, 20 * 30);
                        }))
                .withSubcommand(new CommandAPICommand("denyinvite")
                        .withArguments(new PlayerArgument("nimi"))
                        .executesPlayer((receiver, args) -> {
                            if (marriageManager.isPlayerMarried(receiver)){
                                receiver.sendMessage("Olet jo naimisissa.");
                                return;
                            }
                            Player sender = (Player) args.get("nimi");

                            if (marry.containsKey(sender) && marry.get(sender) == receiver) {
                                marry.remove(sender);
                                if (sender.isOnline()){
                                    sender.sendMessage(toMiniMessage(receiver.getName() + " kieltäytyi parisuhdekutsusta."));
                                }
                                receiver.sendMessage(toMiniMessage("Kieltäydyit parisuhdekutsusta."));
                            }

                        }))
                .withSubcommand(new CommandAPICommand("acceptinvite")
                        .withArguments(new PlayerArgument("nimi"))
                        .executesPlayer((receiver, args) -> {
                            Player sender = (Player) args.get("nimi");

                            if (marry.containsKey(sender) && marry.get(sender) == receiver) {
                                marry.remove(sender);
                                marriageManager.formMarriage(sender.getUniqueId(), receiver.getUniqueId());
                                Bukkit.broadcast(toMiniMessage("On suuri ilo ilmoittaa, että <yellow>" + sender.getName() + "</yellow> on mennyt naimisiin <yellow>" + receiver.getName() + ":n</yellow> kanssa."));
                            }

                        }))
                .withSubcommand(new CommandAPICommand("teleport")
                        .executesPlayer((player, args) -> {
                            if (marriageManager.isPlayerMarried(player)){
                                UUID uuid = UUID.fromString(marriageManager.getMarriageInfo(player, "uuid"));
                                if (Bukkit.getOfflinePlayer(uuid).isOnline()){
                                    Player player2 = Bukkit.getPlayer(uuid);
                                    player.sendMessage("Teleporttasit kumppaniisi.");
                                    player2.sendMessage("Kumppanisi teleporttasi sinuun.");
                                    player.teleport(player2);
                                } else {
                                    player.sendMessage("Kumppanisi ei ole paikalla.");

                                }
                            }

                        }))
                .executesPlayer((p, args) -> {
                    if (marriageManager.isPlayerMarried(p)){
                        marriageMenu.showGUI(p);
                        return;
                    }
                    p.sendMessage("Et ole parisuhteessa.");
                })

                .register();

    }

    public @NotNull Component toMiniMessage(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }
}