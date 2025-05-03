package net.gahvila.gahvilacore.Music;

import com.destroystokyo.paper.MaterialTags;
import cz.koca2000.nbs4j.Song;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.gahvila.gahvilacore.nbsminecraft.player.SongPlayer;
import net.gahvila.gahvilacore.nbsminecraft.player.emitter.GlobalSoundEmitter;
import net.gahvila.inventoryframework.adventuresupport.ComponentHolder;
import net.gahvila.inventoryframework.gui.GuiItem;
import net.gahvila.inventoryframework.gui.type.ChestGui;
import net.gahvila.inventoryframework.pane.PaginatedPane;
import net.gahvila.inventoryframework.pane.Pane;
import net.gahvila.inventoryframework.pane.PatternPane;
import net.gahvila.inventoryframework.pane.StaticPane;
import net.gahvila.inventoryframework.pane.util.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.persistence.PersistentDataType;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.gahvila.gahvilacore.GahvilaCore.instance;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toUndecoratedMM;

public class MusicMenu {
    private final MusicManager musicManager;

    public static ArrayList<Player> cooldown = new ArrayList<>();


    public MusicMenu(MusicManager musicManager) {
        this.musicManager = musicManager;
    }

    public void showGUI(Player player, int openingPage) {
        ChestGui gui = new ChestGui(6, ComponentHolder.of(toUndecoratedMM("<dark_purple><b>Musiikkivalikko")));
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        Pattern pattern = new Pattern(
                "111111111",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "111111111"
        );
        PatternPane border = new PatternPane(0, 0, 9, 6, Pane.Priority.LOWEST, pattern);
        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toUndecoratedMM(""));
        backgroundMeta.setHideTooltip(true);
        background.setItemMeta(backgroundMeta);
        border.bindItem('1', new GuiItem(background));
        gui.addPane(border);

        PaginatedPane pages = new PaginatedPane(1, 1, 7, 4);
        List<ItemStack> items = new ArrayList<>();
        NamespacedKey key = new NamespacedKey(instance, "aula");

        ArrayList<Material> discs = new ArrayList<>(MaterialTags.MUSIC_DISCS.getValues());
        discs.remove(Material.MUSIC_DISC_11);
        discs.remove(Material.MUSIC_DISC_CREATOR);
        discs.remove(Material.MUSIC_DISC_CREATOR_MUSIC_BOX);
        discs.remove(Material.MUSIC_DISC_PRECIPICE);
        discs.remove(Material.MUSIC_DISC_RELIC);
        discs.remove(Material.MUSIC_DISC_PIGSTEP);

        if (musicManager.getLoadState() && !musicManager.getSongsSorted(player).isEmpty()) {
            int discsSize = discs.size();

            for (Song song : musicManager.getSongsSorted(player)) {
                int hash = Math.abs((song.getMetadata().getAuthor() + song.getMetadata().getTitle()).hashCode());
                Material disc = discs.get(hash % discsSize);
                ItemStack item = new ItemStack(disc);
                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, song.getMetadata().getTitle());
                if (musicManager.isFavorited(player, song)){
                    meta.displayName(toUndecoratedMM("<white>" + song.getMetadata().getTitle() + "</white> <yellow><b>⭐</b></yellow>"));
                    meta.setEnchantmentGlintOverride(true);
                } else {
                    meta.displayName(toUndecoratedMM("<white>" + song.getMetadata().getTitle()));
                    meta.setEnchantmentGlintOverride(false);
                }
                meta.lore(List.of(
                        toUndecoratedMM("<gray>" + song.getMetadata().getOriginalAuthor()),
                        toUndecoratedMM("<gray>" + musicManager.songLength(song))
                ));

                if (musicManager.isFavorited(player, song)){
                    meta.setEnchantmentGlintOverride(true);
                }
                item.setItemMeta(meta);
                items.add(item);
                item.setData(DataComponentTypes.TOOLTIP_DISPLAY,
                        TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.JUKEBOX_PLAYABLE).build());
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
                if (event.getClick().isShiftClick()) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6F, 1F);
                    musicManager.toggleFavorited(player, song);

                    if (musicManager.isFavorited(player, song)){
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 0.6F, 1F);
                    } else {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.6F, 1F);
                    }
                    showGUI(player, pages.getPage());
                    return;
                }

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6F, 1F);
                musicManager.clearSongPlayer(player);
                if (!musicManager.getSpeakerEnabled(player)) {
                    musicManager.createSongPlayer(player, song, 0, true);
                } else if (musicManager.getSpeakerEnabled(player)) {
                    musicManager.createSongPlayer(player, song, 0, true);
                }
                player.sendRichMessage("<white>Laitoit kappaleen <yellow>" + songName + "</yellow> <white>soimaan.");
                gui.update();
            }else {
                player.closeInventory();
                instance.getLogger().severe("player attempted to play a song that doesn't exist");
            }
        });

        StaticPane navigationPane = new StaticPane(0, 5, 9, 1);


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
                    if (musicManager.getSongPlayer(player).isPlaying()) {
                        musicManager.getSongPlayer(player).pause();
                    } else {
                        musicManager.getSongPlayer(player).play();
                    }
                    musicManager.savePauseToCookie(player);
                }
            } else if (event.getClick().isRightClick()) {
                player.playSound(player.getLocation(), Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1F, 1F);
                musicManager.clearSongPlayer(player);
            }
            gui.update();
        }), 0, 0);

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
                        musicManager.createSongPlayer(player, sp.getCurrentSong(), sp.getTick(), true);
                    }
                } else {
                    if (musicManager.getSongPlayer(player) != null) {
                        SongPlayer sp = musicManager.getSongPlayer(player);
                        musicManager.createSongPlayer(player, sp.getCurrentSong(), sp.getTick(), true);
                    }
                }
                autoplay.lore(List.of(toUndecoratedMM("<gray>Toistaa jatkuvasti"), toUndecoratedMM("<gray>uusia kappaleita."), toUndecoratedMM("<red>Pois päältä")));
            }else {
                if (musicManager.getSpeakerEnabled(player)){
                    player.sendRichMessage("<red>Jatkuva toisto ei ole käytössä kaiutintilan päällä ollessa!");
                    if (musicManager.getSongPlayer(player) != null) {
                        SongPlayer sp = musicManager.getSongPlayer(player);
                        musicManager.createSongPlayer(player, sp.getCurrentSong(), sp.getTick(), true);
                    }
                } else {
                    if (musicManager.getSongPlayer(player) != null) {
                        SongPlayer sp = musicManager.getSongPlayer(player);
                        musicManager.createSongPlayer(player, sp.getCurrentSong(), sp.getTick(), true);
                    }
                }
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);
                player.sendRichMessage("Jatkuva toisto kytketty päälle.");
                musicManager.setAutoEnabled(player, true);
                autoplay.lore(List.of(toUndecoratedMM("<gray>Toistaa jatkuvasti"), toUndecoratedMM("<gray>uusia kappaleita."), toUndecoratedMM("<green>Päällä")));
            }
            gui.update();
        }), 1, 0);

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
                    if (sp.isPlaying()){
                        musicManager.createSongPlayer(player, sp.getCurrentSong(), sp.getTick(), true);
                    }
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
                    if (sp.isPlaying()){
                        musicManager.createSongPlayer(player, sp.getCurrentSong(), sp.getTick(), true);
                    }
                }
                speaker.lore(List.of(toUndecoratedMM("<gray>Soittaa kappaleesi ympärillä"), toUndecoratedMM("<gray>oleville pelaajille."), toUndecoratedMM("<green>Päällä")));
            }
            gui.update();
        }), 2, 0);

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

                if (sp.getSoundEmitter() instanceof GlobalSoundEmitter){
                    sp.setVolume(musicManager.volumeConverter(musicManager.getVolume(player)));
                }
            }

            volumeMeta.displayName(toUndecoratedMM("<b>Volyymi</b>: <yellow>" + musicManager.getVolume(player) + "</yellow><gray>/</gray><yellow>10</yellow>"));
            volume.setItemMeta(volumeMeta);
            gui.update();
        }), 3, 0);

        ItemStack sorting = new ItemStack(Material.NETHER_STAR);
        ItemMeta sortingMeta = sorting.getItemMeta();
        sortingMeta.displayName(toUndecoratedMM("<b>Listaus"));
        sortingMeta.displayName(toUndecoratedMM("<white><b>Järjestys: " + musicManager.getSorting(player).getDisplayName()));
        sorting.setItemMeta(sortingMeta);
        navigationPane.addItem(new GuiItem(sorting, event -> {
            musicManager.changeSorting(player);
            player.playSound(player.getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 0.8F, 1F);
            player.closeInventory();
            showGUI(player, 0);
        }), 4, 0);

        ItemStack random = new ItemStack(Material.ENDER_PEARL);
        ItemMeta randomMeta = random.getItemMeta();
        randomMeta.displayName(toUndecoratedMM("<b>Satunnainen kappale"));
        random.setItemMeta(randomMeta);
        Random rand = new Random();
        navigationPane.addItem(new GuiItem(random, event -> {
            if (cooldown.contains(player)) return;
            cooldown.add(player);
            Bukkit.getScheduler().runTaskLater(instance, () -> cooldown.remove(player), 10);

            int randomIndex = rand.nextInt(musicManager.getSongs().size());
            Song randomSong = musicManager.getSongs().get(randomIndex);
            if (randomSong != null){
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6F, 1F);
                musicManager.clearSongPlayer(player);
                if (!musicManager.getSpeakerEnabled(player)) {
                    musicManager.createSongPlayer(player, randomSong, 0, true);
                } else if (musicManager.getSpeakerEnabled(player)) {
                    musicManager.createSongPlayer(player, randomSong, 0, true);
                }
                player.sendRichMessage("<white>Laitoit kappaleen <yellow>" + randomSong.getMetadata().getTitle() + "</yellow> <white>soimaan.");
                gui.update();
            }else {
                player.closeInventory();
                instance.getLogger().severe("player attempted to play a song that doesn't exist");
            }
        }), 5, 0);

        ItemStack previous = new ItemStack(Material.MANGROVE_BUTTON);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.displayName(toUndecoratedMM("<b>Takaisin"));
        previous.setItemMeta(previousMeta);
        navigationPane.addItem(new GuiItem(previous, event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.7F);
                gui.setTitle(ComponentHolder.of(toUndecoratedMM("<dark_purple><b>Musiikkivalikko</b></dark_purple> <dark_gray>(<yellow>" + (pages.getPage() + 1) + "</yellow><dark_gray>/</dark_gray><yellow>" + pages.getPages() + "</yellow><dark_gray>)</dark_gray>")));

                musicManager.setPage(player, pages.getPage());

                gui.update();
            }
        }), 7, 0);
        ItemStack next = new ItemStack(Material.WARPED_BUTTON);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(toUndecoratedMM("<b>Seuraava"));
        next.setItemMeta(nextMeta);
        navigationPane.addItem(new GuiItem(next, event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);
                gui.setTitle(ComponentHolder.of(toUndecoratedMM("<dark_purple><b>Musiikkivalikko</b></dark_purple> <dark_gray>(<yellow>" + (pages.getPage() + 1) + "</yellow><dark_gray>/</dark_gray><yellow>" + pages.getPages() + "</yellow><dark_gray>)</dark_gray>")));

                musicManager.setPage(player, pages.getPage());

                gui.update();
            }
        }), 8, 0);
        gui.addPane(navigationPane);

        if (openingPage <= pages.getPages() - 1) {
            pages.setPage(openingPage);
        }
        gui.setTitle(ComponentHolder.of(toUndecoratedMM("<dark_purple><b>Musiikkivalikko</b></dark_purple> <dark_gray>(<yellow>" + (pages.getPage() + 1) + "</yellow><dark_gray>/</dark_gray><yellow>" + pages.getPages() + "</yellow><dark_gray>)</dark_gray>")));

        gui.update();
    }
}

