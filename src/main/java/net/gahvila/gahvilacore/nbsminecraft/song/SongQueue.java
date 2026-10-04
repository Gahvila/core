package net.gahvila.gahvilacore.nbsminecraft.song;

import cz.koca2000.nbs4j.Song;

import java.util.*;

public class SongQueue {
    private final Deque<Song> priorityQueue = new ArrayDeque<>();
    private final Deque<Song> queue = new ArrayDeque<>();
    private boolean loop;

    public SongQueue() {}

    public SongQueue(Song song) {
        queueSong(song);
    }

    public SongQueue(Playlist playlist) {
        queuePlaylist(playlist);
    }

    /**
     * @return an unmodifiable view of the current queue
     */
    public synchronized Collection<Song> getQueue() {
        return Collections.unmodifiableCollection(new ArrayList<>(queue));
    }

    /**
     * Queue a song to be played
     * @param song song to queue
     */
    public synchronized void queueSong(Song song) {
        if (song != null) {
            queue.add(song);
        }
    }

    /**
     * Queue songs to be played
     * @param songs songs to queue
     */
    public synchronized void queueSongs(Song... songs) {
        for (Song song : songs) {
            queueSong(song);
        }
    }

    /**
     * Queue songs to be played
     * @param songs songs to queue
     */
    public synchronized void queueSongs(Collection<Song> songs) {
        if (songs != null) {
            for (Song song : songs) {
                queueSong(song);
            }
        }
    }

    /**
     * Queue a song in the priority queue, songs in the priority queue will be played before
     * songs in the default queue and will not be effected by queue looping or shuffling.
     * @param song song to queue
     */
    public synchronized void queueSongPriority(Song song) {
        if (song != null) {
            priorityQueue.add(song);
        }
    }

    /**
     * Queue a playlist of songs
     * @param playlist playlist to queue
     */
    public synchronized void queuePlaylist(Playlist playlist) {
        if (playlist != null && playlist.getSongs() != null) {
            queueSongs(playlist.getSongs());
        }
    }

    /**
     * Queue and shuffle a playlist of songs
     * @param playlist playlist to shuffle and queue
     */
    public synchronized void queueShuffledPlaylist(Playlist playlist) {
        if (playlist != null && playlist.getShuffledSongs() != null) {
            queueSongs(playlist.getShuffledSongs());
        }
    }

    /**
     * Collects the next queued song and adds the song to the back of the queue if looping is enabled
     * @return queued song
     */
    public synchronized Song poll() {
        if (!priorityQueue.isEmpty()) {
            return priorityQueue.poll();
        } else {
            Song song = queue.poll();

            if (this.isLooping() && song != null) {
                queueSong(song);
            }

            return song;
        }
    }

    /**
     * Remove all songs from the queue
     */
    public synchronized void clearQueue() {
        priorityQueue.clear();
        queue.clear();
    }

    /**
     * @return whether the queue is looping
     */
    public synchronized boolean isLooping() {
        return loop;
    }

    /**
     * @return whether this queue contains no elements
     */
    public synchronized boolean isEmpty() {
        return priorityQueue.isEmpty() && queue.isEmpty();
    }

    /**
     * Set the queue loop status
     * @param loop whether the queue should loop
     */
    public synchronized void loop(boolean loop) {
        this.loop = loop;
    }

    /**
     * Shuffle the queue
     */
    public synchronized void shuffle() {
        List<Song> queueSnapshot = new ArrayList<>(queue);

        Collections.shuffle(queueSnapshot);

        queue.clear();
        queue.addAll(queueSnapshot);
    }
}
