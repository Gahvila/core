package net.gahvila.gahvilacore.Music.MusicBlocks;

import cz.koca2000.nbs4j.Song;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Music.MusicManager;
import net.gahvila.gahvilacore.nbsminecraft.platform.bukkit.player.BukkitSongPlayer;
import net.gahvila.gahvilacore.nbsminecraft.player.SongPlayer;
import net.gahvila.gahvilacore.nbsminecraft.player.emitter.StaticSoundEmitter;
import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MusicBlock {

    private final GahvilaCore plugin;
    private final MusicManager musicManager;
    private final String name;
    private final Location location;
    private final List<String> songTitles;
    private final int maxRange;
    private final int maxVolume;

    private SongPlayer masterPlayer;
    private final Map<UUID, SongPlayer> playerSongPlayers = new ConcurrentHashMap<>();
    private Song lastMasterSong = null;

    private BukkitTask particleTask;
    private BukkitTask updateTask;
    private SoundLocation soundLocation;

    public MusicBlock(GahvilaCore plugin, MusicManager musicManager, String name, Location location, List<String> songTitles, int range, int volume) {
        this.plugin = plugin;
        this.musicManager = musicManager;
        this.name = name;
        this.location = location;
        this.songTitles = songTitles;
        this.maxRange = range;
        this.maxVolume = volume;
    }

    public void start() {
        if (songTitles.isEmpty()) {
            Bukkit.getLogger().warning("Music block '" + name + "' has no songs and will not be started.");
            return;
        }

        this.soundLocation = new SoundLocation(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ()
        );

        this.masterPlayer = new BukkitSongPlayer.Builder()
                .soundEmitter(new StaticSoundEmitter(this.soundLocation))
                .transposeNotes(false)
                .volume(0)
                .build();

        int validSongCount = 0;
        for (String title : songTitles) {
            Song song = musicManager.getSong(title);
            if (song != null) {
                masterPlayer.queueSong(song);
                validSongCount++;
            }
        }

        if (validSongCount == 0) {
            Bukkit.getLogger().warning("Music block '" + name + "' found no valid songs. Player not starting.");
            masterPlayer.stop();
            masterPlayer = null;
            return;
        }

        masterPlayer.loopQueue(true);
        masterPlayer.shuffleQueue();
        masterPlayer.play();
        this.lastMasterSong = masterPlayer.getCurrentSong();

        startParticleTask();
        startUpdateTask();
    }

    public void stop() {
        if (particleTask != null) {
            particleTask.cancel();
            particleTask = null;
        }
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }

        playerSongPlayers.values().forEach(SongPlayer::stop);
        playerSongPlayers.clear();

        if (masterPlayer != null) {
            masterPlayer.stop();
            masterPlayer = null;
        }
    }

    private void startParticleTask() {
        Location particleLoc = location.clone().add(0.5, 1.2, 0.5);

        this.particleTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!playerSongPlayers.isEmpty()) {
                location.getWorld().spawnParticle(Particle.NOTE, particleLoc, 1, 0, 0, 0, 0);
            }
        }, 0L, 20L);
    }

    private void startUpdateTask() {
        final double maxRangeSquared = (double) this.maxRange * this.maxRange;
        final World blockWorld = location.getWorld();

        if (blockWorld == null) {
            Bukkit.getLogger().warning("Music block '" + name + "' is in an invalid world.");
            return;
        }

        this.updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (masterPlayer == null) {
                return;
            }

            Song currentMasterSong = masterPlayer.getCurrentSong();
            long currentMasterTick = masterPlayer.getTick();

            if (currentMasterSong != null && !currentMasterSong.equals(lastMasterSong)) {
                for (SongPlayer playerSP : playerSongPlayers.values()) {
                    playerSP.playSong(currentMasterSong);
                    playerSP.setTick(currentMasterTick);
                }
                lastMasterSong = currentMasterSong;
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getWorld().equals(blockWorld)) {
                    if (playerSongPlayers.containsKey(player.getUniqueId())) {
                        removePlayer(player);
                    }
                    continue;
                }

                double distanceSq = player.getLocation().distanceSquared(location);
                boolean inRange = distanceSq <= maxRangeSquared;
                boolean isListening = playerSongPlayers.containsKey(player.getUniqueId());

                if (inRange) {
                    SongPlayer sp = playerSongPlayers.get(player.getUniqueId());
                    if (sp == null) {
                        sp = addPlayer(player);
                    }

                    if (sp == null) continue;

                    int newVolume = getNewVolume(distanceSq);
                    sp.setVolume(newVolume);

                } else if (isListening) {
                    removePlayer(player);
                }
            }
        }, 0L, 5L);
    }

    private int getNewVolume(double distanceSq) {
        double distance = Math.sqrt(distanceSq);
        double closeness = Math.max(0.0, 1.0 - (distance / this.maxRange));
        double curvedCloseness = closeness * closeness;

        int newVolume = (int) (this.maxVolume * curvedCloseness);
        return newVolume;
    }


    public SongPlayer addPlayer(Player player) {
        if (playerSongPlayers.containsKey(player.getUniqueId()) || masterPlayer == null) {
            return null;
        }

        SongPlayer newPlayerSP = new BukkitSongPlayer.Builder()
                .soundEmitter(new StaticSoundEmitter(this.soundLocation))
                .transposeNotes(false)
                .volume(0)
                .build();

        newPlayerSP.addListener(new AudioListener(player.getEntityId(), player.getUniqueId()));

        for (String title : songTitles) {
            Song song = musicManager.getSong(title);
            if (song != null) {
                newPlayerSP.queueSong(song);
            }
        }
        newPlayerSP.loopQueue(true);

        Song masterSong = masterPlayer.getCurrentSong();
        if (masterSong != null) {
            newPlayerSP.playSong(masterSong);
            newPlayerSP.setTick(masterPlayer.getTick());
        }
        newPlayerSP.play();

        playerSongPlayers.put(player.getUniqueId(), newPlayerSP);
        return newPlayerSP;
    }

    public void removePlayer(Player player) {
        SongPlayer sp = playerSongPlayers.remove(player.getUniqueId());
        if (sp != null) {
            sp.stop();
        }
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public List<String> getSongTitles() {
        return songTitles;
    }

    public int getRange() {
        return maxRange;
    }

    public int getVolume() {
        return maxVolume;
    }
}