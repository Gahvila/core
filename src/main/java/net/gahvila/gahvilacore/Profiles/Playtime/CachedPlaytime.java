package net.gahvila.gahvilacore.Profiles.Playtime;

import java.time.Duration;

public class CachedPlaytime {
    private Long playtime;
    private long lastJoinTime;

    public CachedPlaytime(Long playtime, long lastJoinTime) {
        this.playtime = playtime;
        this.lastJoinTime = lastJoinTime;
    }

    public Long getPlaytime() {
        return playtime;
    }

    public void addPlaytime(Long additionalTime) {
        this.playtime = playtime + additionalTime;
    }

    public long getLastJoinTime() {
        return lastJoinTime;
    }

    public void setLastJoinTime(long lastJoinTime) {
        this.lastJoinTime = lastJoinTime;
    }

    public long getElapsedTime() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastJoinTime) / 1000;
    }
}