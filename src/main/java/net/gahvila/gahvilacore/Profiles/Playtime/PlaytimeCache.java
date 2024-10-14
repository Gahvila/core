package net.gahvila.gahvilacore.Profiles.Playtime;

public class PlaytimeCache {
    private Long playtime;
    private long lastJoinTime;

    public PlaytimeCache(Long playtime, long lastJoinTime) {
        this.playtime = playtime;
        this.lastJoinTime = lastJoinTime;
    }

    public Long getPlaytime() {
        return playtime;
    }

    public void addPlaytime(Long additionalTime) {
        this.playtime = playtime + additionalTime;
    }

    public void subtractPlaytime(Long additionalTime) {
        this.playtime = playtime - additionalTime;
    }

    public void setLastJoinTime(long lastJoinTime) {
        this.lastJoinTime = lastJoinTime;
    }

    public long getElapsedTime() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastJoinTime) / 1000;
    }
}