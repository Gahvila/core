package net.gahvila.gahvilacore.Music;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Sound;

public class MusicCommand {

    private final MusicMenu musicMenu;
    private final MusicManager musicManager;

    public MusicCommand(MusicManager musicManager, MusicMenu musicMenu) {
        this.musicManager = musicManager;
        this.musicMenu = musicMenu;
    }

    public void registerCommands() {
        new CommandAPICommand("music")

                .withSubcommand(new CommandAPICommand("musicreload")
                        .withPermission("music.reload")
                        .executes((sender, args) -> {
                            sender.sendMessage("Ladataan musiikit uudelleen...");
                            musicManager.loadSongs(executionTime -> {
                                sender.sendMessage("Ladattu musiikit uudelleen " + executionTime + " millisekuntissa.");
                            });
                        }))
                .executesPlayer((p, args) -> {
                    p.playSound(p.getLocation(), Sound.ENTITY_LLAMA_SWAG, 0.6F, 1F);
                    musicMenu.showGUI(p, 0);
                })
                .register();
    }
}
