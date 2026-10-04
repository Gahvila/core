package net.gahvila.gahvilacore.nbsminecraft.player;

import cz.koca2000.nbs4j.Layer;
import cz.koca2000.nbs4j.Note;
import cz.koca2000.nbs4j.Song;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.nbsminecraft.NBSAPI;
import net.gahvila.gahvilacore.nbsminecraft.events.SongEndEvent;
import net.gahvila.gahvilacore.nbsminecraft.events.SongNextEvent;
import net.gahvila.gahvilacore.nbsminecraft.platform.AbstractPlatform;
import net.gahvila.gahvilacore.nbsminecraft.player.emitter.GlobalSoundEmitter;
import net.gahvila.gahvilacore.nbsminecraft.player.emitter.SoundEmitter;
import net.gahvila.gahvilacore.nbsminecraft.song.Playlist;
import net.gahvila.gahvilacore.nbsminecraft.song.SongQueue;
import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import net.gahvila.gahvilacore.nbsminecraft.utils.Instruments;
import net.gahvila.gahvilacore.nbsminecraft.utils.PitchUtils;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundCategory;
import net.kyori.adventure.text.Component;
import net.gahvila.gahvilacore.nbsminecraft.events.SongNextEvent;
import net.gahvila.gahvilacore.nbsminecraft.events.SongEndEvent;
import org.bukkit.Bukkit;
import net.gahvila.gahvilacore.GahvilaCore;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class SongPlayer {
    private final AbstractPlatform platform;
    private final SoundEmitter soundEmitter;
    private final SongQueue queue;
    private final Map<UUID, AudioListener> listeners = new ConcurrentHashMap<>();
    private final SoundCategory soundCategory;
    private int volume;
    private final boolean transposeNotes;

    private volatile Song song = null;
    private volatile boolean playing = false;
    private volatile long songTick = 0;
    private volatile long songStartTime = -1;
    private volatile boolean crossfadeEnabled = false;
    private volatile Song nextSong = null;
    private volatile long nextSongTick = 0;
    private long nextSongStartTick = 0;
    private java.util.concurrent.ScheduledFuture<?> nextSongTask = null;
    private long crossfadeStartTime = -1;
    private long currentCrossfadeMs = 8000;

    private SongPlayer(AbstractPlatform platform, SoundEmitter soundEmitter, SongQueue queue, SoundCategory soundCategory, int volume, boolean transposeNotes) {
        this.platform = platform;
        this.soundEmitter = soundEmitter;
        this.queue = queue;
        this.soundCategory = soundCategory;
        this.volume = volume;
        this.transposeNotes = transposeNotes;
    }

    /**
     * Gets the song queue, adding songs directly to the queue will not ensure that
     * the player is playing
     * @return player's song queue
     */
    public SongQueue getQueue() {
        return queue;
    }

    /**
     * returns all audio listeners
     */
    public Map<UUID, AudioListener> getListeners() {
        return listeners;
    }

    /**
     * Add an audio listener
     * @param listener audio listener
     */
    public void addListener(AudioListener listener) {
        listeners.put(listener.uuid(), listener);
    }

    /**
     * Remove listener
     * @param uuid listener's uuid
     */
    public void removeListener(UUID uuid) {
        listeners.remove(uuid);
    }

    /**
     * Set the player's volume
     * @param volume volume
     */
    public void setCrossfade(boolean enabled) {
        this.crossfadeEnabled = enabled;
    }

    private boolean isDrumInstrument(Note note) {
        if (note.isCustomInstrument()) return false;
        int inst = note.getInstrument();
        return inst == 1 || inst == 2 || inst == 3 || inst == 4;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    /**
     * @return current song
     */
    public @Nullable Song getCurrentSong() {
        return nextSong != null ? nextSong : song;
    }

    /**
     * @return time in seconds since the start of the current song
     */
    public long getSongPlaytime() {
        return songStartTime != -1 ? Instant.now().getEpochSecond() - songStartTime : -1;
    }

    /**
     * @return total duration of current song in seconds
     */
    public long getSongDuration() {
        Song current = getCurrentSong();
        return current != null ? (long) current.getSongLengthInSeconds() : 0L;
    }

    /**
     * @return whether the player is marked as playing (A player can still be playing
     * even if no song is currently playing)
     */
    public boolean isPlaying() {
        return this.playing;
    }

    /**
     * Start/continue playing the current song
     */
    public void play() {
        if (!playing) {
            this.playing = true;
            NBSAPI.INSTANCE.getThreadPool().schedule(this::tickSong, 0, java.util.concurrent.TimeUnit.MICROSECONDS);
        }
    }

    private void ensurePlaying() {
        if (!playing) {
            this.playing = true;
            NBSAPI.INSTANCE.getThreadPool().schedule(this::tickSong, 0, java.util.concurrent.TimeUnit.MICROSECONDS);
        }
    }

    /**
     * Pause the current song
     */
    public void pause() {
        playing = false;
    }

    /**
     * Stop the player and clear the queue
     */
    public void stop() {
        playing = false;
        queue.clearQueue();
        song = null;
        songTick = 0;
        nextSong = null;
        nextSongTick = 0;
        crossfadeStartTime = -1;
        if (nextSongTask != null) {
            nextSongTask.cancel(false);
            nextSongTask = null;
        }
    }

    /**
     * Skip to the next queue song
     */
    public void skip() {
        playSong(queue.poll());
    }

    /**
     * Set the queue loop status
     * @param loop whether the queue should loop
     */
    public void loopQueue(boolean loop) {
        queue.loop(loop);
    }

    /**
     * Shuffle the queue
     */
    public void shuffleQueue() {
        queue.shuffle();
    }

    /**
     * Immediately plays a song, this will stop the current song
     * @param song song to play
     */
    public void playSong(Song song) {
        this.song = song;
        this.songTick = 0;
        this.songStartTime = java.time.Instant.now().getEpochSecond();
        this.nextSong = null;
        this.nextSongTick = 0;
        this.crossfadeStartTime = -1;
        if (this.nextSongTask != null) {
            this.nextSongTask.cancel(false);
            this.nextSongTask = null;
        }
        ensurePlaying();
    }

    /**
     * Queue a song to be played
     * @param song song to queue
     */
    public void queueSong(Song song) {
        queue.queueSong(song);
        ensurePlaying();
    }

    /**
     * Queue songs to be played
     * @param songs songs to queue
     */
    public void queueSongs(Collection<Song> songs) {
        queue.queueSongs(songs);
        ensurePlaying();
    }

    /**
     * Queue a song in the priority queue, songs in the priority queue will be played before
     * songs in the default queue and will not be effected by queue looping or shuffling.
     * @param song song to queue
     */
    public void queueSongPriority(Song song) {
        queue.queueSongPriority(song);
        ensurePlaying();
    }

    /**
     * Get the tick position of the song
     */
    public long getTick() {
        return nextSong != null ? nextSongTick : songTick;
    }

    /**
     * Set the tick position of the song
     */
    public void setTick(long tick) {
        this.songTick = tick;
    }

    /**
     * Get the SoundEmitter of the SongPlayer
     */
    public SoundEmitter getSoundEmitter() {
        return soundEmitter;
    }

    /**
     * Tick the current song
     */
    public void tickSong() {
        if (!isPlaying()) {
            return;
        }

        try {
            if (song == null && nextSong == null) {
                if (!queue.isEmpty()) {
                    playSong(queue.poll());
                } else {
                    playing = false;
                }
                return;
            }

            if (song != null) {
                float tempo = song.getTempo(songTick + 1);
                if (tempo <= 0) tempo = 10;
                long remainingMs = (long) ((song.getSongLength() - songTick) * (1000000.0f / tempo) / 1000);
                long totalSongMs = (long) (song.getSongLength() * (1000000.0f / tempo) / 1000);
                long allowedCrossfade = Math.max(1, Math.min(8000, totalSongMs / 4));
                
                if (crossfadeEnabled && nextSong == null && !queue.isEmpty() && remainingMs <= allowedCrossfade) {
                    nextSong = queue.poll();
                    if (nextSong != null) {
                        nextSongStartTick = getFirstNoteTick(nextSong);
                        nextSongTick = nextSongStartTick;
                        crossfadeStartTime = System.currentTimeMillis();
                        currentCrossfadeMs = allowedCrossfade;
                        SongNextEvent event = new SongNextEvent(this);
                        Bukkit.getScheduler().runTask(instance, () -> Bukkit.getPluginManager().callEvent(event));
                        tickNextSong();
                    }
                }

                if (!listeners.isEmpty() && this.volume > 0) {
                    float fadeOutVol = 1.0f;
                    if (crossfadeEnabled && nextSong != null && crossfadeStartTime != -1) {
                        long elapsed = System.currentTimeMillis() - crossfadeStartTime;
                        float ratioOut = Math.max(0.0f, 1.0f - (elapsed / (float) currentCrossfadeMs));
                        fadeOutVol = ratioOut;
                    }

                    for (cz.koca2000.nbs4j.Layer layer : song.getLayers()) {
                        try {
                            cz.koca2000.nbs4j.Note note = layer.getNote(songTick);
                            if (note == null) continue;
                            
                            if (crossfadeEnabled && nextSong != null && crossfadeStartTime != -1) {
                                if (fadeOutVol < 0.5f && isDrumInstrument(note)) continue;
                            }
                            
                            String sound = note.isCustomInstrument() ? song.getCustomInstrument(note.getInstrument()).getName() : Instruments.getSound(note.getInstrument());
                            int noteVol = note.getVolume() & 0xFF;
                            if (noteVol == 0) noteVol = 100;
                            
                            float volume = (layer.getVolume() * this.volume * noteVol) / 1_000_000F;
                            volume *= fadeOutVol;
                            
                            float pitch = PitchUtils.getPitchInOctave(note);
                            float panning = note.getPanning() & 0xFF;
                            
                            for (AudioListener listener : listeners.values()) {
                                soundEmitter.playSound(platform, listener, sound, soundCategory, volume, pitch, panning);
                            }
                        } catch (Exception e) {
                            // Ignore Sound Stopper or other invalid notes to prevent crash loop
                        }
                    }
                }

                songTick++;
                if (song.getSongLength() < songTick) {
                    if (nextSong != null) {
                        song = nextSong;
                        songTick = nextSongTick;
                        nextSong = null;
                        if (nextSongTask != null) {
                            nextSongTask.cancel(false);
                            nextSongTask = null;
                        }
                        float newTempo = song.getTempo(songTick + 1);
                        if (newTempo <= 0) newTempo = 10;
                        scheduleNextTick(newTempo);
                    } else {
                        onSongFinish();
                        if (isPlaying() && song != null) {
                            float newTempo = song.getTempo(songTick + 1);
                            if (newTempo <= 0) newTempo = 10;
                            scheduleNextTick(newTempo);
                        }
                    }
                } else {
                    scheduleNextTick(tempo);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            if (isPlaying() && song != null) {
                float tempo = song.getTempo(songTick + 1);
                if (tempo <= 0) tempo = 10;
                scheduleNextTick(tempo);
            }
        }
    }

    private void scheduleNextTick(float tempo) {
        if (tempo <= 0) tempo = 10;
        long period = (long) (1000000 / tempo);
        NBSAPI.INSTANCE.getThreadPool().schedule(this::tickSong, period, java.util.concurrent.TimeUnit.MICROSECONDS);
    }

    private void tickNextSong() {
        if (!isPlaying() || nextSong == null) {
            return;
        }

        try {
            if (!listeners.isEmpty() && this.volume > 0) {
                float fadeInVol = 1.0f;
                if (crossfadeStartTime != -1) {
                    long elapsed = System.currentTimeMillis() - crossfadeStartTime;
                    float ratioIn = Math.min(1.0f, elapsed / (float) currentCrossfadeMs);
                    fadeInVol = ratioIn;
                }
                
                for (cz.koca2000.nbs4j.Layer layer : nextSong.getLayers()) {
                    try {
                        cz.koca2000.nbs4j.Note note = layer.getNote(nextSongTick);
                        if (note == null) continue;
                        
                        if (crossfadeStartTime != -1) {
                            if (fadeInVol < 0.5f && isDrumInstrument(note)) continue;
                        }
                        
                        String sound = note.isCustomInstrument() ? nextSong.getCustomInstrument(note.getInstrument()).getName() : Instruments.getSound(note.getInstrument());
                        int noteVol = note.getVolume() & 0xFF;
                        if (noteVol == 0) noteVol = 100;
                        
                        float volume = (layer.getVolume() * this.volume * noteVol) / 1_000_000F;
                        volume *= fadeInVol;
                        
                        float pitch = PitchUtils.getPitchInOctave(note);
                        float panning = note.getPanning() & 0xFF;
                        
                        for (AudioListener listener : listeners.values()) {
                            soundEmitter.playSound(platform, listener, sound, soundCategory, volume, pitch, panning);
                        }
                    } catch (Exception e) {
                        // Ignore exceptions to prevent crashing the playback loop
                    }
                }
            }
            
            nextSongTick++;
            
            float tempo = nextSong.getTempo(nextSongTick + 1);
            if (tempo <= 0) tempo = 10;
            long period = (long) (1000000 / tempo);
            nextSongTask = NBSAPI.INSTANCE.getThreadPool().schedule(this::tickNextSong, period, java.util.concurrent.TimeUnit.MICROSECONDS);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    private long getFirstNoteTick(Song s) {
        long firstTick = -1;
        for (cz.koca2000.nbs4j.Layer layer : s.getLayers()) {
            for (long i = 0; i <= s.getSongLength(); i++) {
                if (layer.getNote(i) != null) {
                    if (firstTick == -1 || i < firstTick) firstTick = i;
                    break;
                }
            }
        }
        return firstTick == -1 ? 0 : firstTick;
    }

    private void onSongFinish() {
        if (!queue.isEmpty()) {
            SongNextEvent event = new SongNextEvent(this);
            Bukkit.getScheduler().runTask(instance, () -> Bukkit.getPluginManager().callEvent(event));
            playSong(queue.poll());
        } else {
            playing = false;
            SongEndEvent event = new SongEndEvent(this);
            Bukkit.getScheduler().runTask(instance, () -> Bukkit.getPluginManager().callEvent(event));
        }
    }

    public static class Builder {
        private final AbstractPlatform platform;
        private SoundEmitter soundEmitter = new GlobalSoundEmitter();
        private SongQueue queue = new SongQueue();
        private SoundCategory soundCategory = SoundCategory.RECORDS;
        private int volume = 100;
        private boolean isStrict = false;
        private boolean transposeNotes = true;

        public Builder(AbstractPlatform platform) {
            this.platform = platform;
        }

        public Builder soundEmitter(SoundEmitter soundEmitter) {
            this.soundEmitter = soundEmitter;
            return this;
        }

        public Builder setQueue(SongQueue queue) {
            this.queue = queue;
            return this;
        }

        public Builder queue(Song song) {
            this.queue.queueSong(song);
            return this;
        }

        public Builder queue(Playlist playlist) {
            this.queue.queuePlaylist(playlist);
            return this;
        }

        public Builder soundCategory(SoundCategory soundCategory) {
            this.soundCategory = soundCategory;
            return this;
        }

        public Builder volume(int volume) {
            this.volume = volume;
            return this;
        }

        public Builder isStrict(boolean strict) {
            this.isStrict = strict;
            return this;
        }

        public Builder transposeNotes(boolean transposeNotes) {
            this.transposeNotes = transposeNotes;
            return this;
        }

        public SongPlayer build() {
            return new SongPlayer(platform, soundEmitter, queue, soundCategory, volume, transposeNotes);
        }
    }
}



