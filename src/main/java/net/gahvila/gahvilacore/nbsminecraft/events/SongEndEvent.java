package net.gahvila.gahvilacore.nbsminecraft.events;

import net.gahvila.gahvilacore.nbsminecraft.player.SongPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SongEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final SongPlayer song;

    public SongEndEvent(@NotNull SongPlayer song) {
        this.song = song;
    }

    @NotNull
    public SongPlayer getSongPlayer() {
        return song;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
