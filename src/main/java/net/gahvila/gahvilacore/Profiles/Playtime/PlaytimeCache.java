package net.gahvila.gahvilacore.Profiles.Playtime;

public class PlaytimeCache {
    private long playtime;
    private long lastJoinTime;
    private long afkStartTime; // Time when player went AFK
    private boolean isAfk;

    public PlaytimeCache(long playtime, long lastJoinTime) {
        this.playtime = playtime;
        this.lastJoinTime = lastJoinTime;
        this.isAfk = false;
        this.afkStartTime = 0;
    }

    public Long getPlaytime() {
        return playtime;
    }

    public void addPlaytime(long additionalTime) {
        this.playtime += additionalTime;
    }

    public void subtractPlaytime(long additionalTime) {
        this.playtime -= additionalTime;
    }

    public void setLastJoinTime(long lastJoinTime) {
        this.lastJoinTime = lastJoinTime;
    }

    public long getElapsedTime() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - lastJoinTime) / 1000;

        if (isAfk && afkStartTime != 0) {
            long afkDuration = (currentTime - afkStartTime) / 1000;
            elapsedTime -= afkDuration;
        }

        return elapsedTime;
    }

    public void setAfk(boolean afk) {
        long currentTime = System.currentTimeMillis();

        if (afk && !isAfk) {
            afkStartTime = currentTime;
        } else if (!afk && isAfk) {
            long afkDuration = (currentTime - afkStartTime) / 1000;
            lastJoinTime += afkDuration * 1000;
            afkStartTime = 0;
        }

        this.isAfk = afk;
    }
}