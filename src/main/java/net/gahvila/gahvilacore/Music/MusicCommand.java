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
import net.gahvila.gahvilacore.Music.MusicBlocks.MusicBlock;
import net.gahvila.gahvilacore.Music.MusicBlocks.MusicBlockManager;
import net.gahvila.gahvilacore.nbsminecraft.player.SongPlayer;
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
import java.util.stream.Collectors;

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
                                                .then(Commands.argument("songs", StringArgumentType.greedyString())
                                                        .suggests(this::suggestSongList)
                                                        .executes(this::executeCreateBlock)
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
        String songsInput = context.getArgument("songs", String.class);

        Location location = player.getLocation().getBlock().getLocation();

        List<String> songTitles = Arrays.asList(songsInput.split("\\s*,\\s*"));
        List<String> validTitles = new ArrayList<>();
        List<String> invalidTitles = new ArrayList<>();

        for (String title : songTitles) {
            if (musicManager.getSong(title) != null) {
                validTitles.add(title);
            } else {
                invalidTitles.add(title);
            }
        }

        if (validTitles.isEmpty()) {
            player.sendRichMessage("<red>Error: No valid songs found. Block not created.");
            if (!invalidTitles.isEmpty()) {
                player.sendRichMessage("<gray>Invalid titles: " + String.join(", ", invalidTitles));
            }
            return 0;
        }

        if (musicBlockManager.createMusicBlock(name, location, validTitles, range, volume)) {
            player.sendRichMessage("<green>Music block '" + name + "' created at your location (Range: " + range + ", Volume: " + volume + "%).");
            if (!invalidTitles.isEmpty()) {
                player.sendRichMessage("<yellow>Warning: The following songs were not found and were skipped: " + String.join(", ", invalidTitles));
            }
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
            sender.sendRichMessage("<green>Set volume for music block '" + name + "' to " + newVolume + "%.");
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
            sender.sendRichMessage("<green>Set range for music block '" + name + "' to " + newRange + " blocks.");
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
        List<String> invalidTitles = new ArrayList<>();
        List<String> duplicateTitles = new ArrayList<>();

        List<String> currentSongs = block.getSongTitles();

        for (String title : songTitles) {
            if (currentSongs.contains(title)) {
                duplicateTitles.add(title);
            } else if (musicManager.getSong(title) != null) {
                validTitles.add(title);
            } else {
                invalidTitles.add(title);
            }
        }

        if (validTitles.isEmpty()) {
            sender.sendRichMessage("<red>Error: No new, valid songs found to add.");
            if (!invalidTitles.isEmpty()) {
                sender.sendRichMessage("<gray>Invalid titles: " + String.join(", ", invalidTitles));
            }
            if (!duplicateTitles.isEmpty()) {
                sender.sendRichMessage("<gray>Duplicate titles: " + String.join(", ", duplicateTitles));
            }
            return 0;
        }

        if (musicBlockManager.addBlockSongs(name, validTitles)) {
            sender.sendRichMessage("<green>Added " + validTitles.size() + " songs to '" + name + "': " + String.join(", ", validTitles));
            if (!invalidTitles.isEmpty()) {
                sender.sendRichMessage("<yellow>Warning (Not Found): " + String.join(", ", invalidTitles));
            }
            if (!duplicateTitles.isEmpty()) {
                sender.sendRichMessage("<yellow>Warning (Duplicates): " + String.join(", ", duplicateTitles));
            }
        } else {
            sender.sendRichMessage("<red>Error: Could not add songs to '" + name + "'.");
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
        List<String> removedTitles = new ArrayList<>();
        List<String> notFoundTitles = new ArrayList<>();
        List<String> currentSongs = block.getSongTitles();

        for (String title : songsToRemove) {
            if (currentSongs.contains(title)) {
                removedTitles.add(title);
            } else {
                notFoundTitles.add(title);
            }
        }

        if (removedTitles.isEmpty()) {
            sender.sendRichMessage("<red>Error: None of the specified songs were found on block '" + name + "'.");
            if (!notFoundTitles.isEmpty()) {
                sender.sendRichMessage("<gray>Titles not on block: " + String.join(", ", notFoundTitles));
            }
            return 0;
        }

        if (musicBlockManager.removeBlockSongs(name, removedTitles)) {
            sender.sendRichMessage("<green>Removed " + removedTitles.size() + " songs from '" + name + "': " + String.join(", ", removedTitles));
            if (!notFoundTitles.isEmpty()) {
                sender.sendRichMessage("<yellow>Warning (Not Found): " + String.join(", ", notFoundTitles));
            }
        } else {
            sender.sendRichMessage("<red>Error: Could not remove songs from '" + name + "'.");
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
            if (musicManager.getRadioEnabled(player)) {
                musicManager.setRadioEnabled(player, false);
                player.sendRichMessage("Radiotila kytketty pois päältä.");
            }
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
            if (musicManager.getRadioEnabled(player)) {
                if (musicManager.getRadioPlayer().getListeners().containsKey(player.getUniqueId())) {
                    musicManager.removeRadioListener(player);
                } else {
                    musicManager.addRadioListener(player);
                }
                player.sendMessage("Vaihdettu.");
            } else {
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
        }
        return 0;
    }

    private int executeStop(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender instanceof Player player) {
            player.sendMessage("Pysäytetty.");
            player.playSound(player.getLocation(), Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1F, 1F);
            musicManager.clearSongPlayer(player);
            if (musicManager.getRadioEnabled(player)) {
                musicManager.removeRadioListener(player);
            }
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
                sender.sendMessage("Ladattu musiikit uudelleen " + executionTime + " millisekuntissa. Myös musiikkilohkot ladattiin uudelleen.");
            });
            return Command.SINGLE_SUCCESS;
        } else {
            sender.sendMessage("Ei oikeuksia.");
        }
        return 0;
    }

    private int executeRadioSkip(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender.hasPermission("music.radioskip")) {
            musicManager.getRadioPlayer().skip();
            Set<UUID> playerUUIDs = musicManager.getRadioPlayer().getListeners().keySet();
            for (UUID uuid : playerUUIDs) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendRichMessage("DJ ohitti kappaleen. Nyt soi: <yellow>" + musicManager.getRadioPlayer().getCurrentSong().getMetadata().getTitle());
                }
            }
            return Command.SINGLE_SUCCESS;
        } else {
            sender.sendMessage("Ei oikeuksia.");
        }
        return 0;
    }
}