package net.gahvila.gahvilacore.Music;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import cz.koca2000.nbs4j.Song;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.gahvila.gahvilacore.Core.DebugMode;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.nbsminecraft.player.SongPlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MusicCommand {

    private final MusicDialogMenu musicDialogMenu;
    private final MusicManager musicManager;

    public MusicCommand(MusicManager musicManager, MusicDialogMenu musicDialogMenu) {
        this.musicManager = musicManager;
        this.musicDialogMenu = musicDialogMenu;
    }

    public void registerCommands(GahvilaCore plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(create());
        });
    }

    private LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("music")
                .executes(this::execute)
                .then(Commands.literal("play")
                        .then(Commands.argument("title", StringArgumentType.greedyString())
                                .suggests((ctx, builder) -> {
                                    musicManager.getSongs().stream()
                                            .map(song -> song.getMetadata().getTitle())
                                            .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                            .forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(this::executePlay)
                        )
                )
                .then(Commands.literal("pause").executes(this::executePause))
                .then(Commands.literal("stop").executes(this::executeStop))
                .then(Commands.literal("volume")
                        .then(Commands.argument("volume", IntegerArgumentType.integer(1, 10))
                                .suggests((ctx, builder) -> {
                                    for (int i = 1; i <= 10; i++) {
                                        String suggestion = String.valueOf(i);
                                        if (suggestion.startsWith(builder.getRemainingLowerCase())) {
                                            builder.suggest(i);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(this::executeVolume)
                        )
                )
                .then(Commands.literal("settings").executes(this::executeSettings))
                .then(Commands.literal("reload")
                        .requires(source -> source.getSender().hasPermission("music.reload"))
                        .executes(this::executeReload))
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getSender() instanceof Player player) {
            player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SWAG, 0.6F, 1F);
            musicDialogMenu.show(player, musicManager.getPage(player));
        }
        return 1;
    }

    private int executePlay(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getSender() instanceof Player player) {
            Song song = musicManager.getSong(context.getArgument("title", String.class));
            if (song != null) {
                musicManager.clearSongPlayer(player);
                if (!musicManager.getSpeakerEnabled(player)) {
                    musicManager.createSongPlayer(player, song, 0, true);
                } else if (musicManager.getSpeakerEnabled(player)) {
                    musicManager.createSongPlayer(player, song, 0, true);
                }
                player.sendRichMessage("<white>Laitoit kappaleen <yellow>" + song.getMetadata().getTitle() + "</yellow> <white>soimaan.");
            } else {
                player.sendRichMessage("Kelvoton nimi.");
            }
        }
        return 1;
    }

    private int executePause(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender instanceof Player player) {
            if (musicManager.getSongPlayer(player) != null) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 1F);
                if (musicManager.getSongPlayer(player).isPlaying()) {
                    musicManager.pauseSong(player, musicManager.getSongPlayer(player));
                } else {
                    musicManager.playSong(player, musicManager.getSongPlayer(player));
                }
                musicManager.savePauseToCookie(player);
                player.sendMessage("Vaihdettu.");
            } else {
                player.sendMessage("Ei vaihdettu.");
            }
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private int executeStop(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender instanceof Player player) {
            player.sendMessage("Pysäytetty.");
            player.playSound(player.getLocation(), Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1F, 1F);
            musicManager.clearSongPlayer(player);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private int executeVolume(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        final int volume = context.getArgument("volume", Integer.class);

        if (sender instanceof Player player) {
            if (volume >= 1 && volume <= 10) {
                musicManager.setVolume(player, (byte) volume);
                player.sendRichMessage("Äänenvoimakkuus asetettu: <yellow>" + volume + "</yellow>.");
            } else {
                sender.sendMessage("Kelvoton äänenvoimakkuus'.");
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private int executeSettings(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender instanceof Player player) {
            player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SWAG, 0.6F, 1F);
            musicDialogMenu.showSettings(player);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private int executeReload(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender.hasPermission("music.reload")) {
            sender.sendMessage("Ladataan musiikit uudelleen...");
            musicManager.loadSongs(executionTime -> {
                sender.sendMessage("Ladattu musiikit uudelleen " + executionTime + " millisekuntissa.");
            });
            return Command.SINGLE_SUCCESS;
        } else {
            sender.sendMessage("Ei oikeuksia.");
        }
        return 0;
    }
}
