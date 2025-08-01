package net.gahvila.gahvilacore.Essentials.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.leonhard.storage.Json;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.gahvila.gahvilacore.GahvilaCore;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static net.gahvila.gahvilacore.GahvilaCore.instance;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
public class TeleportCommands {
    //player which sent the tpa request, player which received the teleport request
    public static HashMap<Player, Player> tpa = new HashMap<>();
    public static HashMap<Player, Player> tpahere = new HashMap<>();

    public static HashMap<Player, Player> latestTpaName = new HashMap<>();

    public void registerCommands(GahvilaCore plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(createTpaCommand());
            commands.registrar().register(createTpaHereCommand());
            commands.registrar().register(createTpaCancelCommand());
            commands.registrar().register(createTpaToggleCommand());
            commands.registrar().register(createTpaYesCommand());
            commands.registrar().register(createTpaNoCommand());

        });
    }

    private LiteralCommandNode<CommandSourceStack> createTpaCommand() {
        return Commands.literal("tpa")
                .then(Commands.argument("target", ArgumentTypes.player())
                        .executes(ctx -> {
                            final CommandSender sender = ctx.getSource().getSender();
                            if (sender instanceof Player tpasender) {
                                final PlayerSelectorArgumentResolver playerSelector = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                                final Player tpareceiver = playerSelector.resolve(ctx.getSource()).getFirst();

                                if (tpasender.getName().equals(tpareceiver.getName())) {
                                    tpasender.sendMessage("Et voi teleportata itseesi.");
                                    return Command.SINGLE_SUCCESS;
                                }
                                if (getTpaToggle(tpareceiver)) {
                                    tpasender.sendMessage("Tuolle pelaajalle ei voi lähettää TPA-pyyntöjä.");
                                    return Command.SINGLE_SUCCESS;
                                }
                                if (tpa.containsKey(tpasender)) {
                                    tpasender.sendMessage(toMM("Sinulla on jo aktiivinen TPA-pyyntö. Lähettääksesi uuden sinun täytyy perua aikaisempi klikkaamalla tätä viestiä tai /tpacancel.")
                                            .hoverEvent(HoverEvent.showText(toMM("Klikkaa peruaksesi"))).clickEvent(ClickEvent.runCommand("/tpacancel")));
                                    return Command.SINGLE_SUCCESS;
                                }
                                if (Bukkit.getServer().getPluginManager().getPlugin("CarbonChat") != null) {
                                    CarbonPlayer carbonPlayer = CarbonChatProvider.carbonChat().userManager().user(tpareceiver.getUniqueId()).getNow(null);
                                    if (carbonPlayer.ignoring(tpasender.getUniqueId())) {
                                        tpasender.sendMessage("Et voi lähettää TPA-pyyntöä tuolle pelaajalle.");
                                        return Command.SINGLE_SUCCESS;
                                    }
                                }

                                tpa.put(tpasender, tpareceiver);
                                latestTpaName.put(tpareceiver, tpasender);
                                tpareceiver.playSound(tpareceiver.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2F, 1F);
                                tpasender.sendMessage(toMM("Lähetit TPA-pyynnön <#85FF00>" + tpareceiver.getName() + ":lle</#85FF00>."));

                                tpareceiver.sendMessage(toMM("\n| <#85FF00>" + tpasender.getName() + " </#85FF00>lähetti sinulle <#85FF00>TPA-pyynnön</#85FF00>."));
                                tpareceiver.sendMessage(toMM("<white>| Sinulla on <#85FF00>30 sekuntia</#85FF00> <white>aikaa hyväksyä."));
                                tpareceiver.sendMessage(toMM("| <green><b>Hyväksy</b>: /tpayes " + tpasender.getName())
                                        .hoverEvent(HoverEvent.showText(toMM("<green>Klikkaa hyväksyäksesi</green>"))).clickEvent(ClickEvent.runCommand("/tpayes " + tpasender.getName())));
                                tpareceiver.sendMessage(toMM("| <red><b>Kieltäydy</b>: /tpano " + tpasender.getName())
                                        .hoverEvent(HoverEvent.showText(toMM("<red>Klikkaa kieltäytyäksesi</red>"))).clickEvent(ClickEvent.runCommand("/tpano " + tpasender.getName())));

                                Bukkit.getServer().getScheduler().runTaskLater(instance, new Runnable() {
                                    @Override
                                    public void run() {
                                        if (tpa.get(tpasender) != null) {
                                            tpa.remove(tpasender);
                                            latestTpaName.remove(tpareceiver);
                                            tpasender.sendMessage("TPA-pyyntösi vanhentui.");
                                            tpareceiver.sendMessage(toMM("<#85FF00>" + tpasender.getName() + ":n </#85FF00>lähettämä TPA-pyyntö vanhentui."));
                                        }
                                    }
                                }, 20 * 30);
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }

    private LiteralCommandNode<CommandSourceStack> createTpaHereCommand() {
        return Commands.literal("tpahere")
                .then(Commands.argument("target", ArgumentTypes.player())
                        .executes(ctx -> {
                            final CommandSender sender = ctx.getSource().getSender();
                            if (sender instanceof Player tpasender) {
                                final PlayerSelectorArgumentResolver playerSelector = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                                final Player tpareceiver = playerSelector.resolve(ctx.getSource()).getFirst();

                                if (tpasender.getName().equals(tpareceiver.getName())){
                                    tpasender.sendMessage("Et voi teleportata itseesi.");
                                    return Command.SINGLE_SUCCESS;
                                }
                                if (getTpaToggle(tpareceiver)) {
                                    tpasender.sendMessage("Tuolle pelaajalle ei voi lähettää TPA-pyyntöjä.");
                                    return Command.SINGLE_SUCCESS;
                                }
                                if (tpahere.containsKey(tpasender) || tpa.containsKey(tpasender)) {
                                    tpasender.sendMessage(toMM("Sinulla on jo aktiivinen TPA-pyyntö. Lähettääksesi uuden sinun täytyy perua aikaisempi klikkaamalla tätä viestiä tai /tpacancel.")
                                            .hoverEvent(HoverEvent.showText(toMM("Klikkaa peruaksesi"))).clickEvent(ClickEvent.runCommand("/tpacancel")));
                                    return Command.SINGLE_SUCCESS;
                                }
                                if(Bukkit.getServer().getPluginManager().getPlugin("CarbonChat") != null) {
                                    CarbonPlayer carbonPlayer = CarbonChatProvider.carbonChat().userManager().user(tpareceiver.getUniqueId()).getNow(null);
                                    if (carbonPlayer.ignoring(tpasender.getUniqueId())) {
                                        tpasender.sendMessage("Et voi lähettää TPA-pyyntöä tuolle pelaajalle.");
                                        return Command.SINGLE_SUCCESS;
                                    }
                                }

                                tpahere.put(tpasender, tpareceiver);
                                latestTpaName.put(tpareceiver, tpasender);
                                tpareceiver.playSound(tpareceiver.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2F, 1F);
                                tpasender.sendMessage(toMM("Lähetit TPAHere-pyynnön <#85FF00>" + tpareceiver.getName() + ":lle</#85FF00>."));

                                tpareceiver.sendMessage(toMM("\n<white>| <#85FF00>" + tpasender.getName() + " </#85FF00>lähetti sinulle <#85FF00>TPAHere-pyynnön</#85FF00>."));
                                tpareceiver.sendMessage(toMM("<white>| Sinulla on <#85FF00>30 sekuntia</#85FF00> <white>aikaa hyväksyä."));
                                tpareceiver.sendMessage(toMM("<white>| <green><b>Hyväksy</b>: /tpayes " + tpasender.getName())
                                        .hoverEvent(HoverEvent.showText(toMM("<green>Klikkaa hyväksyäksesi</green>"))).clickEvent(ClickEvent.runCommand("/tpayes " + tpasender.getName())));
                                tpareceiver.sendMessage(toMM("<white>| <red><b>Kieltäydy</b>: /tpano " + tpasender.getName())
                                        .hoverEvent(HoverEvent.showText(toMM("<red>Klikkaa kieltäytyäksesi</red>"))).clickEvent(ClickEvent.runCommand("/tpano " + tpasender.getName())));

                                Bukkit.getServer().getScheduler().runTaskLater(instance, new Runnable() {
                                    @Override
                                    public void run() {
                                        if (tpahere.get(tpasender) != null) {
                                            tpahere.remove(tpasender);
                                            latestTpaName.remove(tpareceiver);
                                            tpasender.sendMessage("TPAHere-pyyntösi vanhentui.");
                                            tpareceiver.sendMessage(toMM("<#85FF00>" + tpasender.getName() + ":n </#85FF00>lähettämä TPAHere-pyyntö vanhentui."));
                                        }
                                    }
                                }, 20 * 30);
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }

    private LiteralCommandNode<CommandSourceStack> createTpaCancelCommand() {
        return Commands.literal("tpacancel")
            .executes(ctx -> {
                final CommandSender sender = ctx.getSource().getSender();
                if (sender instanceof Player tpasender) {
                    if (tpa.get(tpasender) != null) {
                        Player tpareceiver = tpa.get(tpasender);

                        tpa.remove(tpasender);
                        latestTpaName.remove(tpareceiver);

                        tpasender.sendMessage("Peruit TPA-pyyntösi.");
                        tpareceiver.sendMessage(toMM(tpasender.getName() + " perui TPA-pyyntönsä."));
                    } else if (tpahere.get(tpasender) != null) {
                        Player tpareceiver = tpahere.get(tpasender);

                        tpahere.remove(tpasender);
                        latestTpaName.remove(tpareceiver);

                        tpasender.sendMessage("Peruit TPAHere-pyyntösi.");
                        tpareceiver.sendMessage(toMM(tpasender.getName() + " perui TPAHere-pyyntönsä."));
                    }
                }

                return Command.SINGLE_SUCCESS;
            }
        ).build();
    }

    private LiteralCommandNode<CommandSourceStack> createTpaToggleCommand() {
        return Commands.literal("tpatoggle")
            .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    if (sender instanceof Player player) {
                        if (getTpaToggle(player)) {
                            changeTpaToggle(player);
                            player.sendMessage("Kytkit TPA-pyynnöt päälle.");
                        } else {
                            changeTpaToggle(player);
                            player.sendMessage("Kytkit TPA-pyynnöt pois päältä.");
                        }
                    }

                    return Command.SINGLE_SUCCESS;
                }
            ).build();
    }

    private LiteralCommandNode<CommandSourceStack> createTpaYesCommand() {
        return Commands.literal("tpayes")
                .executes(ctx -> {
                            final CommandSender sender = ctx.getSource().getSender();
                            if (sender instanceof Player tpareceiver) {
                                if (latestTpaName.containsKey(tpareceiver)) {
                                    if (tpa.containsKey(latestTpaName.get(tpareceiver))) {
                                        Player tpasender = latestTpaName.get(tpareceiver);
                                        acceptTpa(tpasender, tpareceiver, 0);
                                    } else if (tpahere.containsKey(latestTpaName.get(tpareceiver))) {
                                        Player tpasender = latestTpaName.get(tpareceiver);
                                        acceptTpa(tpasender, tpareceiver, 1);
                                    } else {
                                        tpareceiver.sendMessage("Sinulla ei ole TPA-pyyntöjä.");
                                    }
                                } else {
                                    tpareceiver.sendMessage("Sinulla ei ole TPA-pyyntöjä.");
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("target", ArgumentTypes.player())
                        .executes(ctx -> {
                            final CommandSender sender = ctx.getSource().getSender();
                            if (sender instanceof Player tpareceiver) {
                                final PlayerSelectorArgumentResolver playerSelector = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                                final Player tpasender = playerSelector.resolve(ctx.getSource()).getFirst();

                                if (tpa.containsKey(tpasender) && tpa.get(tpasender).equals(tpareceiver)){
                                    acceptTpa(tpasender, tpareceiver, 0);
                                } else if (tpahere.containsKey(tpasender) && tpahere.get(tpasender).equals(tpareceiver)) {
                                    acceptTpa(tpasender, tpareceiver, 1);
                                } else {
                                    tpareceiver.sendMessage(toMM("<white>Sinulla ei ole TPA-pyyntöjä pelaajalta <#85FF00>" + tpasender.getName() + "</#85FF00>."));
                                }
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }

    private LiteralCommandNode<CommandSourceStack> createTpaNoCommand() {
        return Commands.literal("tpano")
                .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    if (sender instanceof Player tpareceiver) {
                        if (latestTpaName.containsKey(tpareceiver)){
                            if (tpa.containsKey(latestTpaName.get(tpareceiver))) {
                                Player tpasender = latestTpaName.get(tpareceiver);
                                denyTpa(tpasender, tpareceiver, 0);
                            } else if (tpa.containsKey(latestTpaName.get(tpareceiver))) {
                                Player tpasender = latestTpaName.get(tpareceiver);
                                denyTpa(tpasender, tpareceiver, 1);
                            } else {
                                tpareceiver.sendMessage("Sinulla ei ole TPA-pyyntöjä.");
                            }
                        } else {
                            tpareceiver.sendMessage("Sinulla ei ole TPA-pyyntöjä.");
                        }
                    }
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("target", ArgumentTypes.player())
                        .executes(ctx -> {
                            final CommandSender sender = ctx.getSource().getSender();
                            if (sender instanceof Player tpareceiver) {
                                final PlayerSelectorArgumentResolver playerSelector = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
                                final Player tpasender = playerSelector.resolve(ctx.getSource()).getFirst();

                                if (tpa.containsKey(tpasender) && tpa.get(tpasender).equals(tpareceiver)){
                                    denyTpa(tpasender, tpareceiver, 0);
                                } else if (tpahere.containsKey(tpasender) && tpahere.get(tpasender).equals(tpareceiver)){
                                    denyTpa(tpasender, tpareceiver, 1);
                                } else {
                                    tpareceiver.sendMessage(toMM("<white>Sinulla ei ole TPA-pyyntöjä pelaajalta <#85FF00>" + tpasender.getName() + "</#85FF00>."));
                                }
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
    }

    public void acceptTpa(Player tpasender, Player tpareceiver, Integer type) {
        if (type == 0) {
            tpa.remove(tpasender);
        } else if (type == 1) {
            tpahere.remove(tpasender);
        }
        latestTpaName.remove(tpareceiver);
        if (!isLocationSafe(tpareceiver.getLocation())) {
            tpasender.sendMessage("Teleporttaus peruttiin epäturvallisen sijainnin takia.");
            tpareceiver.sendMessage("Teleporttaus peruttiin epäturvallisen sijainnin takia.");
            return;
        }

        if (type == 0) {
            tpasender.teleportAsync(tpareceiver.getLocation());
            tpareceiver.sendMessage(toMM("Hyväksyit <#85FF00>" + tpasender.getName() + ":n</#85FF00> TPA-pyynnön."));
            tpasender.sendMessage(toMM("<#85FF00>" + tpareceiver.getName() + "</#85FF00> hyväksyi TPA-pyyntösi."));

            tpasender.playSound(tpasender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2F, 1F);
            tpareceiver.playSound(tpareceiver.getLocation(), Sound.ENTITY_VILLAGER_YES, 2F, 1F);
        } else if (type == 1){
            tpareceiver.teleportAsync(tpasender.getLocation());
            tpareceiver.sendMessage(toMM("Hyväksyit <#85FF00>" + tpasender.getName() + ":n</#85FF00> TPAHere-pyynnön."));
            tpasender.sendMessage(toMM("<#85FF00>" + tpareceiver.getName() + "</#85FF00> hyväksyi TPAHere-pyyntösi."));

            tpareceiver.playSound(tpasender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2F, 1F);
            tpasender.playSound(tpareceiver.getLocation(), Sound.ENTITY_VILLAGER_YES, 2F, 1F);
        }
    }

    public void denyTpa(Player tpasender, Player tpareceiver, Integer type) {
        if (type == 0) {
            tpa.remove(tpasender);
        } else if (type == 1) {
            tpahere.remove(tpasender);
        }
        latestTpaName.remove(tpareceiver);
        if (type == 0) {
            tpareceiver.sendMessage(toMM("Kieltäydyit <#85FF00>" + tpasender.getName() + ":n</#85FF00> TPA-pyynnöstä."));
            tpasender.sendMessage(toMM("<#85FF00>" + tpareceiver.getName() + "</#85FF00> kieltäytyi TPA-pyynnöstäsi."));
            tpasender.playSound(tpasender.getLocation(), Sound.ENTITY_VILLAGER_NO, 2F, 1F);
        } else {
            tpareceiver.sendMessage(toMM("Kieltäydyit <#85FF00>" + tpasender.getName() + ":n</#85FF00> TPAHere-pyynnöstä."));
            tpasender.sendMessage(toMM("<#85FF00>" + tpareceiver.getName() + "</#85FF00> kieltäytyi TPAHere-pyynnöstäsi."));
            tpasender.playSound(tpasender.getLocation(), Sound.ENTITY_VILLAGER_NO, 2F, 1F);
        }
    }

    public void changeTpaToggle(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        UUID uuid = player.getUniqueId();

        if (getTpaToggle(player) == null || !getTpaToggle(player)) {
            playerData.set(uuid + ".tpaDisabled", true);
        } else {
            playerData.set(uuid + ".tpaDisabled", false);
        }
    }

    public Boolean getTpaToggle(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        UUID uuid = player.getUniqueId();
        Boolean toggle = playerData.getBoolean(uuid + ".tpaDisabled");
        return toggle;
    }

    public static HashSet<Material> bad_blocks = new HashSet<>();
    static {
        bad_blocks.add(Material.LAVA);
        bad_blocks.add(Material.FIRE);
        bad_blocks.add(Material.CACTUS);
        bad_blocks.add(Material.MAGMA_BLOCK);
    }
    public static HashSet<Material> ground_blocks = new HashSet<>();
    static {
        bad_blocks.add(Material.AIR);
        bad_blocks.add(Material.LAVA);
        bad_blocks.add(Material.FIRE);
        bad_blocks.add(Material.CACTUS);
        bad_blocks.add(Material.MAGMA_BLOCK);
    }


    public boolean isLocationSafe(Location location) {

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        //Get instances of the blocks around where the player would spawn
        Block block = location.getWorld().getBlockAt(x, y, z);
        Block below = location.getWorld().getBlockAt(x, y - 1, z);
        Block above = location.getWorld().getBlockAt(x, y + 1, z);
        //spawnrtp
        if (!ground_blocks.contains(below.getType())){
            return !(bad_blocks.contains(below.getType())) || (block.isSolid()) || (above.getType().isSolid());
        }else{
            return false;
        }
    }
}
