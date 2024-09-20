package net.gahvila.gahvilacore.Music;

import com.destroystokyo.paper.MaterialTags;
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.model.playmode.MonoStereoMode;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.gahvila.gahvilacore.API.Utils.WorldGuardRegionChecker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.gahvila.gahvilacore.API.Utils.MiniMessageUtils.toUndecoratedMM;
import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class MusicMenu {
    private final MusicManager musicManager;

    public static ArrayList<Player> cooldown = new ArrayList<>();


    public MusicMenu(MusicManager musicManager) {
        this.musicManager = musicManager;
    }

    public void showGUI(Player player) {
        ChestGui gui = new ChestGui(5, ComponentHolder.of(toUndecoratedMM("<dark_purple><b>Musiikkivalikko")));
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        Pattern pattern = new Pattern(
                "111111111",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "111AAA111"
        );
        PatternPane border = new PatternPane(0, 0, 9, 5, Pane.Priority.LOWEST, pattern);
        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toUndecoratedMM(""));
        backgroundMeta.setHideTooltip(true);
        background.setItemMeta(backgroundMeta);
        border.bindItem('1', new GuiItem(background));
        gui.addPane(border);

        PaginatedPane pages = new PaginatedPane(1, 1, 7, 3);
        List<ItemStack> items = new ArrayList<>();
        NamespacedKey key = new NamespacedKey(instance, "aula");

        ArrayList<Material> discs = new ArrayList<>(MaterialTags.MUSIC_DISCS.getValues());
        discs.remove(Material.MUSIC_DISC_11);

        if (!musicManager.getSongs().isEmpty()){
            Random random = new Random();
            for (Song song : musicManager.getSongs()) {
                Material randomDisc = discs.get(random.nextInt(discs.size()));
                ItemStack item = new ItemStack(randomDisc);;
                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, song.getTitle());
                meta.displayName(toUndecoratedMM("<white>" + song.getTitle()));
                meta.lore(List.of(toUndecoratedMM("<gray>" + song.getOriginalAuthor()), toUndecoratedMM("<gray>" + musicManager.songLength(song))));
                JukeboxPlayableComponent component = meta.getJukeboxPlayable();
                component.setShowInTooltip(false);
                meta.setJukeboxPlayable(component);
                item.setItemMeta(meta);
                items.add(item);
            }
        }
        pages.populateWithItemStacks(items);
        gui.addPane(pages);

        pages.setOnClick(event -> {
            if (event.getCurrentItem() == null) return;

            if (cooldown.contains(player)) return;
            cooldown.add(player);
            Bukkit.getScheduler().runTaskLater(instance, () -> cooldown.remove(player), 10);

            String songName = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            Song song = musicManager.getSong(songName);
            if (songName != null && song != null){
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6F, 1F);
                musicManager.clearSongPlayer(player);
                if (!musicManager.getSpeakerEnabled(player)) {
                    createSP(player, song, null);
                } else if (musicManager.getSpeakerEnabled(player)) {
                    createESP(player, song, null);
                }
                player.sendRichMessage("<white>Laitoit kappaleen <yellow>" + songName + "</yellow> <white>soimaan.");
                gui.update();
            }else {
                player.closeInventory();
                Bukkit.getLogger().severe("player attempted to play a song that doesn't exist");
            }
        });

        StaticPane navigationPane = new StaticPane(0, 4, 9, 1);


        ItemStack pause = new ItemStack(Material.BARRIER);
        ItemMeta pauseMeta = pause.getItemMeta();
        pauseMeta.displayName(toUndecoratedMM("<white><b>Keskeytä/Jatka"));
        pauseMeta.lore(List.of(toUndecoratedMM("<white>Vasen: <yellow>keskeytä/jatka"), toUndecoratedMM("<white>Oikea: <yellow>lopeta soitto")));
        pause.setItemMeta(pauseMeta);
        navigationPane.addItem(new GuiItem(pause, event -> {
            if (cooldown.contains(player)) return;
            cooldown.add(player);
            Bukkit.getScheduler().runTaskLater(instance, () -> cooldown.remove(player), 10);

            if (event.getClick().isLeftClick()) {
                if (musicManager.getSongPlayer(player) != null) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 1F);
                    musicManager.getSongPlayer(player).setPlaying(!musicManager.getSongPlayer(player).isPlaying());
                }
            } else if (event.getClick().isRightClick()) {
                player.playSound(player.getLocation(), Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1F, 1F);
                musicManager.clearSongPlayer(player);
            }
            gui.update();
        }), 1, 0);

        ItemStack autoplay = new ItemStack(Material.REDSTONE);
        ItemMeta autoplayMeta = autoplay.getItemMeta();
        autoplayMeta.displayName(toUndecoratedMM("<b>Jatkuva toisto"));
        if (musicManager.getAutoEnabled(player)){
            autoplayMeta.lore(List.of(toUndecoratedMM("<gray>Toistaa jatkuvasti"), toUndecoratedMM("<gray>uusia kappaleita."), toUndecoratedMM("<green>Päällä")));
        } else {
            autoplayMeta.lore(List.of(toUndecoratedMM("<gray>Toistaa jatkuvasti"), toUndecoratedMM("<gray>uusia kappaleita."), toUndecoratedMM("<red>Pois päältä")));
        }
        autoplay.setItemMeta(autoplayMeta);
        navigationPane.addItem(new GuiItem(autoplay, event -> {
            if (cooldown.contains(player)) return;
            cooldown.add(player);
            Bukkit.getScheduler().runTaskLater(instance, () -> cooldown.remove(player), 20);

            if (musicManager.getAutoEnabled(player)){
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.7F);
                player.sendRichMessage("Jatkuva toisto kytketty pois päältä.");
                musicManager.setAutoEnabled(player, false);
                if (musicManager.getSpeakerEnabled(player)){
                    if (musicManager.getSongPlayer(player) != null) {
                        SongPlayer sp = musicManager.getSongPlayer(player);
                        createESP(player, sp.getSong(), sp.getTick());
                    }
                } else {
                    if (musicManager.getSongPlayer(player) != null) {
                        SongPlayer sp = musicManager.getSongPlayer(player);
                        createSP(player, sp.getSong(), sp.getTick());
                    }
                }
                autoplay.lore(List.of(toUndecoratedMM("<gray>Toistaa jatkuvasti"), toUndecoratedMM("<gray>uusia kappaleita."), toUndecoratedMM("<red>Pois päältä")));
            }else {
                if (musicManager.getSpeakerEnabled(player)){
                    player.sendRichMessage("<red>Jatkuva toisto ei ole käytössä kaiutintilan päällä ollessa!");
                    if (musicManager.getSongPlayer(player) != null) {
                        SongPlayer sp = musicManager.getSongPlayer(player);
                        createESP(player, sp.getSong(), sp.getTick());
                    }
                } else {
                    if (musicManager.getSongPlayer(player) != null) {
                        SongPlayer sp = musicManager.getSongPlayer(player);
                        createSP(player, sp.getSong(), sp.getTick());
                    }
                }
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);
                player.sendRichMessage("Jatkuva toisto kytketty päälle.");
                musicManager.setAutoEnabled(player, true);
                autoplay.lore(List.of(toUndecoratedMM("<gray>Toistaa jatkuvasti"), toUndecoratedMM("<gray>uusia kappaleita."), toUndecoratedMM("<green>Päällä")));
            }
            gui.update();
        }), 2, 0);

        ItemStack speaker = new ItemStack(Material.NOTE_BLOCK);
        ItemMeta speakerMeta = speaker.getItemMeta();
        speakerMeta.displayName(toUndecoratedMM("<b>Kaiutintila"));
        if (musicManager.getSpeakerEnabled(player)){
            speakerMeta.lore(List.of(toUndecoratedMM("<gray>Soittaa kappaleesi ympärillä"), toUndecoratedMM("<gray>oleville pelaajille."), toUndecoratedMM("<green>Päällä")));
        } else {
            speakerMeta.lore(List.of(toUndecoratedMM("<gray>Soittaa kappaleesi ympärillä"), toUndecoratedMM("<gray>oleville pelaajille."), toUndecoratedMM("<red>Pois päältä")));
        }
        speaker.setItemMeta(speakerMeta);
        navigationPane.addItem(new GuiItem(speaker, event -> {
            if (cooldown.contains(player)) return;
            cooldown.add(player);
            Bukkit.getScheduler().runTaskLater(instance, () -> cooldown.remove(player), 20);

            if (musicManager.getSpeakerEnabled(player)){
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.7F);
                player.sendRichMessage("Kaiutintila kytketty pois päältä.");
                musicManager.setSpeakerEnabled(player, false);
                if (musicManager.getSongPlayer(player) != null) {
                    SongPlayer sp = musicManager.getSongPlayer(player);
                    createSP(player, sp.getSong(), sp.getTick());
                }
                speaker.lore(List.of(toUndecoratedMM("<gray>Soittaa kappaleesi ympärillä"), toUndecoratedMM("<gray>oleville pelaajille."), toUndecoratedMM("<red>Pois päältä")));
            }else {
                if (musicManager.getAutoEnabled(player)){
                    player.sendRichMessage("<red>Jatkuva toisto ei ole käytössä kaiutintilan päällä ollessa!");
                }
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);
                player.sendRichMessage("Kaiutintila kytketty päälle.");
                musicManager.setSpeakerEnabled(player, true);
                if (musicManager.getSongPlayer(player) != null) {
                    SongPlayer sp = musicManager.getSongPlayer(player);
                    createESP(player, sp.getSong(), sp.getTick());
                }
                speaker.lore(List.of(toUndecoratedMM("<gray>Soittaa kappaleesi ympärillä"), toUndecoratedMM("<gray>oleville pelaajille."), toUndecoratedMM("<green>Päällä")));
            }
            gui.update();
        }), 3, 0);

        ItemStack volume = new ItemStack(Material.BELL);
        ItemMeta volumeMeta = volume.getItemMeta();
        volumeMeta.displayName(toUndecoratedMM("<b>Volyymi</b>: <yellow>" + musicManager.getVolume(player) + "</yellow><gray>/</gray><yellow>10</yellow>"));
        volumeMeta.lore(List.of(toUndecoratedMM("<white>Vasen: <yellow>nosta"), toUndecoratedMM("<white>Oikea: <yellow>laske")));
        volume.setItemMeta(volumeMeta);
        navigationPane.addItem(new GuiItem(volume, event -> {
            if (cooldown.contains(player)) return;
            cooldown.add(player);
            Bukkit.getScheduler().runTaskLater(instance, () -> cooldown.remove(player), 2);

            if (musicManager.getSpeakerEnabled(player)){
                player.sendRichMessage("<red>Äänenvoimakkuuden säätö ei ole käytössä kaiutintilassa!");
            }

            if (event.isLeftClick()) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);
                musicManager.increaseVolume(player);
            } else if (event.isRightClick()) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.7F);
                musicManager.reduceVolume(player);
            }

            if (musicManager.getSongPlayer(player) != null) {
                SongPlayer sp = musicManager.getSongPlayer(player);
                if (sp instanceof RadioSongPlayer){
                    sp.setVolume(musicManager.volumeConverter(musicManager.getVolume(player)));
                }
            }

            volumeMeta.displayName(toUndecoratedMM("<b>Volyymi</b>: <yellow>" + musicManager.getVolume(player) + "</yellow><gray>/</gray><yellow>10</yellow>"));
            volume.setItemMeta(volumeMeta);
            gui.update();
        }), 4, 0);

        ItemStack previous = new ItemStack(Material.MANGROVE_BUTTON);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.displayName(toUndecoratedMM("<b>Takaisin"));
        previous.setItemMeta(previousMeta);
        navigationPane.addItem(new GuiItem(previous, event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.7F);

                gui.update();
            }
        }), 6, 0);
        ItemStack next = new ItemStack(Material.WARPED_BUTTON);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(toUndecoratedMM("<b>Seuraava"));
        next.setItemMeta(nextMeta);
        navigationPane.addItem(new GuiItem(next, event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);

                gui.update();
            }
        }), 7, 0);
        gui.addPane(navigationPane);

        gui.update();
    }

    public void createSP(Player player, Song song, Short tick) {
        musicManager.clearSongPlayer(player);
        Playlist playlist = new Playlist(song);
        RadioSongPlayer rsp = new RadioSongPlayer(playlist);
        rsp.setChannelMode(new MonoStereoMode());
        rsp.setVolume(musicManager.volumeConverter(musicManager.getVolume(player)));
        rsp.addPlayer(player);
        if (tick != null){
            rsp.setTick(tick);
        }
        rsp.setPlaying(true);
        if (musicManager.getAutoEnabled(player)) {
            ArrayList<Song> songs = musicManager.getSongs();
            for (Song playlistSong : songs) {
                playlist.add(playlistSong);
            }
            rsp.setRandom(true);
            rsp.setRepeatMode(RepeatMode.ALL);
        } else {
            rsp.setRandom(false);
            rsp.setRepeatMode(RepeatMode.NO);
        }
        musicManager.saveSongPlayer(player, rsp);
        Bukkit.getScheduler().runTaskLater(instance, () -> musicManager.songPlayerSchedule(player, rsp), 5);
    }

    public void createESP(Player player, Song song, Short tick) {
        musicManager.clearSongPlayer(player);
        EntitySongPlayer esp = new EntitySongPlayer(song);
        esp.setEntity(player);
        esp.setVolume((byte) 45);
        esp.setDistance(24);
        if (tick != null){
            esp.setTick(tick);
        }
        esp.setPlaying(true);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!WorldGuardRegionChecker.isInRegion(onlinePlayer, "spawn")){
                if(Bukkit.getServer().getPluginManager().getPlugin("CarbonChat") != null) {
                    CarbonPlayer carbonPlayer = CarbonChatProvider.carbonChat().userManager().user(onlinePlayer.getUniqueId()).getNow(null);
                    if (!carbonPlayer.ignoring(esp.getEntity().getUniqueId())) {
                        esp.addPlayer(onlinePlayer);
                    }
                }
            }
        }
        musicManager.saveSongPlayer(player, esp);
        Bukkit.getScheduler().runTaskLater(instance, () -> musicManager.songPlayerSchedule(player, esp), 5);
    }
}

