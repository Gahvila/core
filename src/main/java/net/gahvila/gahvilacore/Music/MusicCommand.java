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
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Music.MusicBlocks.MusicBlock;
import net.gahvila.gahvilacore.Music.MusicBlocks.MusicBlockManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MusicCommand {

    private final MusicDialogMenu musicDialogMenu;
    private final MusicManager musicManager;
    private final MusicBlockManager musicBlockManager;

    public MusicCommand(MusicManager musicManager, MusicDialogMenu musicDialogMenu, MusicBlockManager musicBlockManager) {
        this.musicManager = musicManager;
        this.musicDialogMenu = musicDialogMenu;
        this.musicBlockManager = musicBlockManager;
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
                        .requires(source -> source.getSender().hasPermission("music.admin.reload"))
                        .executes(this::executeReload))
                .then(Commands.literal("radioskip")
                        .requires(source -> source.getSender().hasPermission("music.admin.radioskip"))
                        .executes(this::executeRadioSkip))

                .then(Commands.literal("createblock")
                        .requires(source -> source.getSender().hasPermission("music.admin.musicblocks"))
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("range", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("volume", IntegerArgumentType.integer(0, 100))
                                                .then(Commands.argument("times", StringArgumentType.string())
                                                        .suggests((ctx, builder) -> {
                                                            builder.suggest("-1");
                                                            builder.suggest("0");
                                                            builder.suggest("0,6000,12000,18000");
                                                            return builder.buildFuture();
                                                        })
                                                        .then(Commands.argument("songs", StringArgumentType.greedyString())
                                                                .suggests(this::suggestSongList)
                                                                .executes(this::executeCreateBlock)
                                                        )
                                                )
                                        )
                                )
                        )
                )

                .then(Commands.literal("removeblock")
                        .requires(source -> source.getSender().hasPermission("music.admin.musicblocks"))
                        .then(Commands.argument("name", StringArgumentType.string())
                                .suggests(this::suggestBlockSongs)
                                .executes(this::executeRemoveBlock)
                        )
                )

                .then(Commands.literal("editblock")
                        .requires(source -> source.getSender().hasPermission("music.admin.musicblocks"))
                        .then(Commands.argument("name", StringArgumentType.string())
                                .suggests(this::suggestActiveBlocks)
                                .then(Commands.literal("volume")
                                        .then(Commands.argument("volume", IntegerArgumentType.integer(0, 100))
                                                .executes(this::executeEditVolume)
                                        )
                                )
                                .then(Commands.literal("range")
                                        .then(Commands.argument("range", IntegerArgumentType.integer(1))
                                                .executes(this::executeEditRange)
                                        )
                                )
                                .then(Commands.literal("times")
                                        .then(Commands.argument("times", StringArgumentType.greedyString())
                                                .suggests((ctx, builder) -> {
                                                    builder.suggest("-1");
                                                    builder.suggest("0");
                                                    builder.suggest("0,6000,12000,18000");
                                                    return builder.buildFuture();
                                                })
                                                .executes(this::executeEditTimes)
                                        )
                                )
                                .then(Commands.literal("addsongs")
                                        .then(Commands.argument("songs", StringArgumentType.greedyString())
                                                .suggests(this::suggestSongList)
                                                .executes(this::executeEditAddSongs)
                                        )
                                )
                                .then(Commands.literal("removesongs")
                                        .then(Commands.argument("songs", StringArgumentType.greedyString())
                                                .suggests(this::suggestBlockSongs)
                                                .executes(this::executeEditRemoveSongs)
                                        )
                                )
                        )
                )
                .build();
    }

    private CompletableFuture<Suggestions> suggestSongList(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        String input = builder.getRemainingLowerCase();
        String[] currentSongs = input.split("\\s*,\\s*", -1);
        String lastSongFragment = currentSongs.length > 0 ? currentSongs[currentSongs.length - 1] : "";
        String prefix = input.substring(0, input.length() - lastSongFragment.length());

        musicManager.getSongs().stream()
                .map(song -> song.getMetadata().getTitle())
                .filter(title -> title.toLowerCase().startsWith(lastSongFragment))
                .forEach(title -> builder.suggest(prefix + title));

        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestActiveBlocks(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        musicBlockManager.getActiveBlockNames().stream()
                .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestBlockSongs(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        String blockName;
        try {
            blockName = ctx.getArgument("name", String.class);
        } catch (IllegalArgumentException e) {
            return builder.buildFuture();
        }

        MusicBlock block = musicBlockManager.getMusicBlock(blockName);
        if (block == null) {
            return builder.buildFuture();
        }

        String input = builder.getRemainingLowerCase();
        String[] currentSongs = input.split("\\s*,\\s*", -1);
        String lastSongFragment = currentSongs.length > 0 ? currentSongs[currentSongs.length - 1] : "";
        String prefix = input.substring(0, input.length() - lastSongFragment.length());

        block.getSongTitles().stream()
                .filter(title -> title.toLowerCase().startsWith(lastSongFragment))
                .forEach(title -> builder.suggest(prefix + title));

        return builder.buildFuture();
    }

    private int executeCreateBlock(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            context.getSource().getSender().sendMessage("This command can only be run by a player.");
            return 0;
        }

        String name = context.getArgument("name", String.class);
        int range = context.getArgument("range", Integer.class);
        int volume = context.getArgument("volume", Integer.class);
        String timesInput = context.getArgument("times", String.class);
        String songsInput = context.getArgument("songs", String.class);

        List<Integer> triggerTicks = new ArrayList<>();
        try {
            String[] timesParts = timesInput.split(",");
            for (String t : timesParts) {
                triggerTicks.add(Integer.parseInt(t.trim()));
            }
        } catch (NumberFormatException e) {
            player.sendRichMessage("<red>Error: Invalid time format. Use numbers separated by commas (e.g., '0,6000').");
            return 0;
        }

        Location location = player.getLocation().getBlock().getLocation();

        List<String> songTitles = Arrays.asList(songsInput.split("\\s*,\\s*"));
        List<String> validTitles = new ArrayList<>();

        for (String title : songTitles) {
            if (musicManager.getSong(title) != null) validTitles.add(title);
        }

        if (validTitles.isEmpty()) {
            player.sendRichMessage("<red>Error: No valid songs found.");
            return 0;
        }

        if (musicBlockManager.createMusicBlock(name, location, validTitles, range, volume, triggerTicks)) {
            player.sendRichMessage("<green>Music block '" + name + "' created.");
        } else {
            player.sendRichMessage("<red>Error: A music block with the name '" + name + "' already exists.");
        }

        return Command.SINGLE_SUCCESS;
    }

    private int executeRemoveBlock(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        String name = context.getArgument("name", String.class);
        if (musicBlockManager.removeMusicBlock(name)) {
            sender.sendRichMessage("<green>Music block '" + name + "' has been removed.");
        } else {
            sender.sendRichMessage("<red>Error: No music block found with the name '" + name + "'.");
        }
        return Command.SINGLE_SUCCESS;
    }

    private int executeEditVolume(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        String name = context.getArgument("name", String.class);
        int newVolume = context.getArgument("volume", Integer.class);

        if (musicBlockManager.setBlockVolume(name, newVolume)) {
            sender.sendRichMessage("<green>Set volume for '" + name + "' to " + newVolume + "%.");
        } else {
            sender.sendRichMessage("<red>Error: No music block found with the name '" + name + "'.");
        }
        return Command.SINGLE_SUCCESS;
    }

    private int executeEditRange(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        String name = context.getArgument("name", String.class);
        int newRange = context.getArgument("range", Integer.class);

        if (musicBlockManager.setBlockRange(name, newRange)) {
            sender.sendRichMessage("<green>Set range for '" + name + "' to " + newRange + " blocks.");
        } else {
            sender.sendRichMessage("<red>Error: No music block found with the name '" + name + "'.");
        }
        return Command.SINGLE_SUCCESS;
    }

    private int executeEditTimes(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        String name = context.getArgument("name", String.class);
        String timesInput = context.getArgument("times", String.class);

        List<Integer> triggerTicks = new ArrayList<>();
        try {
            String[] timesParts = timesInput.split(",");
            for (String t : timesParts) {
                triggerTicks.add(Integer.parseInt(t.trim()));
            }
        } catch (NumberFormatException e) {
            sender.sendRichMessage("<red>Error: Invalid time format. Use numbers separated by commas (e.g., '0,6000').");
            return 0;
        }

        if (musicBlockManager.setBlockTimes(name, triggerTicks)) {
            sender.sendRichMessage("<green>Set trigger times for '" + name + "' to: " + triggerTicks.toString());
        } else {
            sender.sendRichMessage("<red>Error: No music block found with the name '" + name + "'.");
        }
        return Command.SINGLE_SUCCESS;
    }

    private int executeEditAddSongs(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        String name = context.getArgument("name", String.class);
        String songsInput = context.getArgument("songs", String.class);

        MusicBlock block = musicBlockManager.getMusicBlock(name);
        if (block == null) {
            sender.sendRichMessage("<red>Error: No music block found with the name '" + name + "'.");
            return 0;
        }

        List<String> songTitles = Arrays.asList(songsInput.split("\\s*,\\s*"));
        List<String> validTitles = new ArrayList<>();

        for (String title : songTitles) {
            if (musicManager.getSong(title) != null && !block.getSongTitles().contains(title)) {
                validTitles.add(title);
            }
        }

        if (musicBlockManager.addBlockSongs(name, validTitles)) {
            sender.sendRichMessage("<green>Added " + validTitles.size() + " songs to '" + name + "'.");
        } else {
            sender.sendRichMessage("<red>Error: Could not add songs.");
        }
        return Command.SINGLE_SUCCESS;
    }

    private int executeEditRemoveSongs(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        String name = context.getArgument("name", String.class);
        String songsInput = context.getArgument("songs", String.class);

        MusicBlock block = musicBlockManager.getMusicBlock(name);
        if (block == null) {
            sender.sendRichMessage("<red>Error: No music block found with the name '" + name + "'.");
            return 0;
        }

        List<String> songsToRemove = Arrays.asList(songsInput.split("\\s*,\\s*"));
        List<String> validRemovals = new ArrayList<>();

        for (String title : songsToRemove) {
            if (block.getSongTitles().contains(title)) validRemovals.add(title);
        }

        if (musicBlockManager.removeBlockSongs(name, validRemovals)) {
            sender.sendRichMessage("<green>Removed " + validRemovals.size() + " songs from '" + name + "'.");
        } else {
            sender.sendRichMessage("<red>Error: Could not remove songs.");
        }
        return Command.SINGLE_SUCCESS;
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
            if (musicManager.getRadioEnabled(player)) musicManager.setRadioEnabled(player, false);
            Song song = musicManager.getSong(context.getArgument("title", String.class));
            if (song != null) {
                musicManager.clearSongPlayer(player);
                musicManager.createSongPlayer(player, song, 0, true);
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
            if (!musicManager.getRadioEnabled(player)) {
                if (musicManager.getSongPlayer(player) != null) {
                    if (musicManager.getSongPlayer(player).isPlaying()) musicManager.pauseSong(player, musicManager.getSongPlayer(player));
                    else musicManager.playSong(player, musicManager.getSongPlayer(player));
                    musicManager.savePauseToCookie(player);
                    player.sendMessage("Vaihdettu.");
                }
            }
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private int executeStop(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender instanceof Player player) {
            player.sendMessage("Pysäytetty.");
            musicManager.clearSongPlayer(player);
            if (musicManager.getRadioEnabled(player)) musicManager.removeRadioListener(player);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private int executeVolume(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        final int volume = context.getArgument("volume", Integer.class);
        if (sender instanceof Player player) {
            musicManager.setVolume(player, (byte) volume);
            player.sendRichMessage("Äänenvoimakkuus asetettu: <yellow>" + volume + "</yellow>.");
        }
        return Command.SINGLE_SUCCESS;
    }

    private int executeSettings(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender instanceof Player player) {
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
        }
        return 0;
    }

    private int executeRadioSkip(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender.hasPermission("music.radioskip")) {
            musicManager.getRadioPlayer().skip();
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }
}