package net.gahvila.gahvilacore.Music;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import de.leonhard.storage.Json;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.gahvila.gahvilacore.Utils.WorldGuardRegionChecker;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class MusicManager {

    public MusicManager() {
    }

    public static ArrayList<Song> songs = new ArrayList<>();
    public static HashMap<String, Song> namedSong = new HashMap<>();
    public static HashMap<Player, SongPlayer> songPlayers = new HashMap<>();
    public static HashMap<Player, Boolean> speakerEnabled = new HashMap<>();
    public static HashMap<Player, Boolean> autoEnabled = new HashMap<>();
    public static HashMap<Player, Integer> playerVolume = new HashMap<>();



    public void loadSongs() {
        if (songs != null) songs.clear();
        if (namedSong != null) namedSong.clear();

        File folder = new File(instance.getDataFolder(), "songs");
        if (folder.listFiles() == null) return;
        for (File file : folder.listFiles()) {
            Song song = NBSDecoder.parse(file);
            songs.add(song);
            namedSong.put(song.getTitle(), song);
        }

        songs.sort((song1, song2) -> song1.getTitle().compareToIgnoreCase(song2.getTitle()));
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public Song getSong(String name){
        return namedSong.get(name);
    }

    public void saveSongPlayer(Player player, SongPlayer songPlayer){
        songPlayers.put(player, songPlayer);
    }

    public SongPlayer getSongPlayer(Player player){
        return songPlayers.get(player);
    }

    public void clearSongPlayer(Player player){
        if (!songPlayers.containsKey(player)) return;
        if (songPlayers.get(player) == null) songPlayers.remove(player);
        SongPlayer songPlayer = songPlayers.get(player);
        songPlayer.destroy();
        songPlayers.remove(player);
    }

    //SPEAKER MODE
    public boolean getSpeakerEnabled(Player player) {
        return speakerEnabled.get(player) != null && speakerEnabled.get(player);
    }

    public void setSpeakerEnabled(Player player, boolean option) {
        speakerEnabled.put(player, option);
    }

    //AUTO PLAY
    public boolean getAutoEnabled(Player player) {
        return autoEnabled.get(player);
    }

    public void setAutoEnabled(Player player, boolean option) {
        autoEnabled.put(player, option);
    }

    public void saveAutoState(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        playerData.set(uuid + "." + "radioState", getAutoEnabled(player));
    }
    public boolean getSavedAutoState(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();

        if (!playerData.contains(uuid + "." + "radioState")) {
            return true;
        }
        return playerData.getBoolean(uuid + "." + "radioState");
    }

    //VOLUME SELECTOR
    public int getVolume(Player player) {
        return playerVolume.getOrDefault(player, 5);
    }

    public void setVolume(Player player, int volume) {
        if (volume >= 10) {
            playerVolume.put(player, 10);
        } else playerVolume.put(player, Math.max(volume, 1));
    }

    public void increaseVolume(Player player) {
        setVolume(player, getVolume(player) + 1);
    }

    public void reduceVolume(Player player) {
        setVolume(player, getVolume(player) - 1);
    }

    public void saveVolume(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        playerData.set(uuid + "." + "volume", getVolume(player));
    }
    public int getSavedVolume(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();

        if (!playerData.contains(uuid + "." + "volume")) {
            return 5;
        }
        return playerData.getInt(uuid + "." + "volume");
    }

    //

    public void songPlayerSchedule(Player player, SongPlayer songPlayer) {
        double length = songPlayer.getSong().getLength();
        BossBar progressBar = BossBar.bossBar(toMM("<aqua>" + songPlayer.getSong().getOriginalAuthor() + " - " +
                songPlayer.getSong().getTitle() + "</aqua>"), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        player.showBossBar(progressBar);
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, task -> {
            double progress = (double) songPlayer.getTick() / length;
            if (progress >= 1.0 || progress < 0){
                progressBar.removeViewer(player);
                task.cancel();
                return;
            }
            progressBar.progress((float) progress);

            if (!songPlayer.isPlaying()){
                progressBar.name(toMM("<red>" + songPlayer.getSong().getOriginalAuthor() + " - " +
                        songPlayer.getSong().getTitle() + "</red>"));
                progressBar.color(BossBar.Color.RED);
            } else {
                progressBar.name(toMM("<aqua>" + songPlayer.getSong().getOriginalAuthor() + " - " +
                        songPlayer.getSong().getTitle() + "</aqua>"));
                progressBar.color(BossBar.Color.BLUE);
            }
        }, 0, 1);

        if (songPlayer instanceof EntitySongPlayer){
            boolean carbonEnabled = Bukkit.getServer().getPluginManager().getPlugin("CarbonChat") != null;
            boolean wgEnabled = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null;

            Bukkit.getScheduler().runTaskTimer(instance, task2 -> {
                double progress = (double) songPlayer.getTick() / length;
                if (progress >= 1.0 || progress < 0){
                    task2.cancel();
                    return;
                }
                if (carbonEnabled || wgEnabled) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (wgEnabled) {
                            if (!WorldGuardRegionChecker.isInRegion(onlinePlayer, "spawn")) {
                                songPlayer.addPlayer(onlinePlayer);
                                if (songPlayer.isPlaying()) {
                                    onlinePlayer.spawnParticle(Particle.NOTE, player.getLocation().add(0, 2, 0), 1);
                                }
                            } else {
                                songPlayer.removePlayer(onlinePlayer);
                            }
                        }

                        if (carbonEnabled) {
                            CarbonPlayer carbonPlayer = CarbonChatProvider.carbonChat().userManager().user(onlinePlayer.getUniqueId()).getNow(null);
                            if (carbonPlayer.ignoring(((EntitySongPlayer) songPlayer).getEntity().getUniqueId())) {
                                songPlayer.removePlayer(onlinePlayer);
                            }
                        }
                    }
                }
            }, 10L, 10);
        }
    }

    public byte volumeConverter(int volume) {
        return switch(volume) {
            case 1 -> (byte) 10;
            case 2 -> (byte) 20;
            case 3 -> (byte) 30;
            case 4 -> (byte) 40;
            case 5 -> (byte) 50;
            case 6 -> (byte) 60;
            case 7 -> (byte) 70;
            case 8 -> (byte) 80;
            case 9 -> (byte) 90;
            case 10 -> (byte) 100;
            default -> 50;
        };
    }

    public String songLength(Song song) {
        float lengthInSeconds = song.getLength() / song.getSpeed();

        int minutes = (int) (lengthInSeconds / 60);
        int seconds = (int) (lengthInSeconds % 60);

        return String.format("%d:%02d", minutes, seconds);
    }
}
