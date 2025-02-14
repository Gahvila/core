package net.gahvila.gahvilacore.nbsminecraft;

import cz.koca2000.nbs4j.Song;
import cz.koca2000.nbs4j.SongCorruptedException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NBSAPI {
    public static final NBSAPI INSTANCE = new NBSAPI();

    private final ScheduledExecutorService threads =
            Executors.newScheduledThreadPool(1, Thread.ofVirtual().factory());

    /**
     * @return the thread pool used to send sounds to players
     */
    public ScheduledExecutorService getThreadPool() {
        return threads;
    }

    /**
     * Read a song from a file
     * @param file file to read
     * @return parsed song
     */
    public Song readSongFile(File file) {
        try {
            return Song.fromFile(file);
        } catch (IOException | SongCorruptedException e) {
            return null;
        }
    }

    /**
     * Read a song from an input stream
     * @param inputStream input stream to read
     * @return parsed song
     */
    public Song readSongInputStream(InputStream inputStream) {
        return Song.fromStream(inputStream);
    }
}
