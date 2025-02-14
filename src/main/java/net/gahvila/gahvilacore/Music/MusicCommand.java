package net.gahvila.gahvilacore.Music;

import cz.koca2000.nbs4j.Song;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.gahvila.gahvilacore.nbsminecraft.player.SongPlayer;
import org.bukkit.Sound;

import java.util.List;

public class MusicCommand {

    private final MusicMenu musicMenu;
    private final MusicManager musicManager;

    public MusicCommand(MusicManager musicManager, MusicMenu musicMenu) {
        this.musicManager = musicManager;
        this.musicMenu = musicMenu;
    }

    public void registerCommands() {
        new CommandAPICommand("music")
                .withSubcommand(new CommandAPICommand("play")
                        .withArguments(customSongArgument("title"))
                        .executesPlayer((player, args) -> {
                            Song song = musicManager.getSong(args.getRaw("title"));
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
                        }))
                .withSubcommand(new CommandAPICommand("pause")
                        .executesPlayer((player, args) -> {
                            if (musicManager.getSongPlayer(player) != null) {
                                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 1F);
                                if (musicManager.getSongPlayer(player).isPlaying()) {
                                    musicManager.getSongPlayer(player).pause();
                                } else {
                                    musicManager.getSongPlayer(player).play();
                                }
                                //musicManager.savePauseToCookie(player);
                                player.sendMessage("Vaihdettu.");
                            } else {
                                player.sendMessage("Ei vaihdettu.");
                            }
                        }))
                .withSubcommand(new CommandAPICommand("stop")
                        .executesPlayer((player, args) -> {
                            player.sendMessage("Pysäytetty.");
                            player.playSound(player.getLocation(), Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1F, 1F);
                            musicManager.clearSongPlayer(player);
                        }))
                .withSubcommand(new CommandAPICommand("volume")
                        .withArguments(new IntegerArgument("volume", 1, 10))
                        .executesPlayer((player, args) -> {
                            int volumeInt = (int) args.get("volume");
                            musicManager.setVolume(player, (byte) volumeInt);
                            player.sendRichMessage("Äänenvoimakkuus asetettu: <yellow>" + volumeInt + "</yellow>.");
                        }))
                .withSubcommand(new CommandAPICommand("reload")
                        .withPermission("music.reload")
                        .executes((sender, args) -> {
                            sender.sendMessage("Ladataan musiikit uudelleen...");
                            musicManager.loadSongs(executionTime -> {
                                sender.sendMessage("Ladattu musiikit uudelleen " + executionTime + " millisekuntissa.");
                            });
                        }))
                .executesPlayer((p, args) -> {
                    p.playSound(p.getLocation(), Sound.ENTITY_LLAMA_SWAG, 0.6F, 1F);
                    musicMenu.showGUI(p, musicManager.getPage(p));
                })
                .register();
    }

    public Argument<List<String>> customSongArgument(String nodeName) {
        return new CustomArgument<>(new GreedyStringArgument(nodeName), info -> {

            List<String> songNames = musicManager.getSongs().stream()
                    .map(song -> song.getMetadata().getTitle())
                    .toList();

            if (songNames.isEmpty()) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Kappaleita ei ole."));
            } else {
                return songNames;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            List<String> songNames = musicManager.getSongs().stream()
                    .map(song -> song.getMetadata().getTitle())
                    .toList();

            return songNames.toArray(new String[0]);
        }));
    }
}
