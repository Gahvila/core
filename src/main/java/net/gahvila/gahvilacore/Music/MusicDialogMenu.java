package net.gahvila.gahvilacore.Music;

import cz.koca2000.nbs4j.Song;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.Prefix;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixTypes;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.PrefixManager;
import net.gahvila.gahvilacore.nbsminecraft.player.SongPlayer;
import net.gahvila.gahvilacore.nbsminecraft.player.emitter.GlobalSoundEmitter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.ClientboundClearDialogPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Float.MAX_VALUE;
import static net.gahvila.gahvilacore.GahvilaCore.instance;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toUndecoratedMM;

public class MusicDialogMenu {

    private final MusicManager musicManager;

    public MusicDialogMenu(MusicManager musicManager) {
        this.musicManager = musicManager;
    }

    public void show(Player player, int page) {
        if (musicManager.getRadioEnabled(player)) {
            player.showDialog(createRadioMusicDialog(player));
        } else {
            player.showDialog(createNormalMusicDialog(player, page));
        }
    }

    public void showSettings(Player player) {
        player.showDialog(createSettingsDialog(player));
    }

    public void showVanillaMusic(Player player) {
        player.showDialog(createVanillaMusicDialog(player));
    }

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;

    private static final int ENTRIES_PER_PAGE = COLUMNS * ROWS;

    private Dialog createNormalMusicDialog(Player player, int page) {
        List<Song> songs = musicManager.getSongsSorted(player);
        List<ActionButton> buttons = new ArrayList<>();

        int totalPages = (int) Math.ceil(songs.size() / (double) ENTRIES_PER_PAGE);
        int start = page * ENTRIES_PER_PAGE;
        int end = Math.min(start + ENTRIES_PER_PAGE, songs.size());

        for (int i = start; i < end; i++) {
            Song song = songs.get(i);
            buttons.add(
                    ActionButton.builder(Component.text("♪").color(getColorFromTitle(song.getMetadata().getTitle())))
                            .width(20)
                            .tooltip(toMM(song.getMetadata().getTitle() + "<br><gray>" + song.getMetadata().getOriginalAuthor()
                                    + "</gray><br><b>" + musicManager.songLength(song)))
                            .action(DialogAction.customClick((response, audience) -> {
                                        if (song != null) {
                                            show(player, page);
                                            musicManager.clearSongPlayer(player);
                                            musicManager.createSongPlayer(player, song, 0, true);
                                            player.sendRichMessage("<white>Laitoit kappaleen <yellow>" + song.getMetadata().getTitle() + "</yellow> <white>soimaan.");
                                        } else {
                                            show(player, page);
                                            instance.getLogger().severe("player attempted to play a song that doesn't exist");
                                        }
                                    },
                                    ClickCallback.Options.builder().build()
                            ))
                            .build()
            );
        }

        // fill empty space with buttons to prevent navigation buttons shifting
        while (buttons.size() < ENTRIES_PER_PAGE) {
            buttons.add(
                    ActionButton.builder(toMM(" "))
                            .width(20)
                            .action(DialogAction.customClick(
                                    (response, audience) -> {
                                        show(player, page);
                                    },
                                    ClickCallback.Options.builder().build()
                            ))
                            .build()
            );
        }

        // navigation buttons
        buttons.add(ActionButton.builder(toMM("<red>⏹"))
                .width(20)
                .tooltip(toMM("Lopeta toisto"))
                .action(DialogAction.customClick((response, audience) -> {
                            player.playSound(player.getLocation(), Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1F, 1F);
                            musicManager.clearSongPlayer(player);
                            show(player, page);
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());


        buttons.add(ActionButton.builder(
                        toMM((musicManager.getSongPlayer(player) != null && musicManager.getSongPlayer(player).isPlaying()
                                ? "<aqua>⏯"
                                : "<red>⏯")))
                .width(20)
                .action(DialogAction.customClick((response, audience) -> {
                            var songPlayer = musicManager.getSongPlayer(player);
                            if (songPlayer != null) {
                                if (songPlayer.isPlaying()) {
                                    musicManager.pauseSong(player, songPlayer);
                                } else {
                                    musicManager.playSong(player, songPlayer);
                                }
                                musicManager.savePauseToCookie(player);
                            }
                            show(player, page);
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());

        buttons.add(ActionButton.builder(toMM("<red>-"))
                .width(20)
                .tooltip(toMM("Laske äänenvoimakkuutta: <yellow>" + musicManager.getVolume(player) + "</yellow><gray>/</gray><yellow>10</yellow>"))
                .action(DialogAction.customClick((response, audience) -> {
                            musicManager.reduceVolume(player);
                            if (musicManager.getSongPlayer(player) != null) {
                                SongPlayer sp = musicManager.getSongPlayer(player);

                                if (sp.getSoundEmitter() instanceof GlobalSoundEmitter){
                                    sp.setVolume(musicManager.volumeConverter(musicManager.getVolume(player)));
                                }
                            }
                            show(player, page);
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());

        buttons.add(ActionButton.builder(toMM("<green>+"))
                .width(20)
                .tooltip(toMM("Nosta äänenvoimakkuutta: <yellow>" + musicManager.getVolume(player) + "</yellow><gray>/</gray><yellow>10</yellow>"))
                .action(DialogAction.customClick((response, audience) -> {
                            musicManager.increaseVolume(player);
                            if (musicManager.getSongPlayer(player) != null) {
                                SongPlayer sp = musicManager.getSongPlayer(player);

                                if (sp.getSoundEmitter() instanceof GlobalSoundEmitter){
                                    sp.setVolume(musicManager.volumeConverter(musicManager.getVolume(player)));
                                }
                            }
                            show(player, page);
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());


        buttons.add(ActionButton.builder(toMM("⚙"))
                .width(20)
                .tooltip(toMM("Asetukset"))
                .action(DialogAction.customClick((response, audience) -> {
                            showSettings(player);
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());

        buttons.add(ActionButton.builder(toMM("<gray><b><"))
                .width(20)
                .tooltip(toMM("Aikaisempi sivu"))
                .action(DialogAction.customClick((response, audience) -> {
                            if (page > 0) {
                                int newPage = page - 1;
                                show(player, newPage);
                                musicManager.setPage(player, newPage);
                            } else {
                                show(player, page);
                            }
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());

        buttons.add(ActionButton.builder(toMM("<green><b>>"))
                .width(20)
                .tooltip(toMM("Seuraava sivu"))
                .action(DialogAction.customClick((response, audience) -> {
                            if (page < totalPages - 1) {
                                int newPage = page + 1;
                                show(player, newPage);
                                musicManager.setPage(player, newPage);
                            } else {
                                show(player, page);
                            }
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Musiikkivalikko</b> <dark_gray>(<yellow>" + (page + 1) +
                        "</yellow><dark_gray>/</dark_gray><yellow>" + totalPages + "</yellow><dark_gray>)</dark_gray>"))
                        .afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)
                        .build())
                .type(DialogType.multiAction(
                        buttons,
                        ActionButton.builder(toMM("Sulje valikko"))
                                .width(150)
                                .action(DialogAction.customClick(
                                        (response, audience) -> {
                                            player.showDialog(createClosingDialog());
                                            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                                            serverPlayer.connection.send(ClientboundClearDialogPacket.INSTANCE); // no method for clearing dialogs in paper api
                                            },
                                        ClickCallback.Options.builder().build()
                                ))
                                .build(),
                        COLUMNS
                )));
    }

    private Dialog createRadioMusicDialog(Player player) {
        List<ActionButton> buttons = new ArrayList<>();

        // navigation buttons
        buttons.add(ActionButton.builder(
                        toMM((musicManager.getRadioPlayer() != null
                                && musicManager.getRadioPlayer().getListeners().containsKey(player.getUniqueId()))
                                ? "<red>⏸ Lopeta radion kuuntelu"
                                : "<green>▶ Aloita radion kuuntelu"))
                .width(130)
                .action(DialogAction.customClick((response, audience) -> {
                            if (musicManager.getRadioPlayer() == null) return;
                            if (musicManager.getRadioPlayer().getListeners().containsKey(player.getUniqueId())) {
                                musicManager.removeRadioListener(player);
                            } else {
                                musicManager.addRadioListener(player);
                            }

                            show(player, 0);
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());

        buttons.add(ActionButton.builder(toMM("⚙"))
                .width(20)
                .tooltip(toMM("Asetukset"))
                .action(DialogAction.customClick((response, audience) -> {
                            showSettings(player);
                        },
                        ClickCallback.Options.builder().build()
                ))
                .build());

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Musiikkivalikko</b> <dark_gray>(<yellow>Radio</yellow><dark_gray>)</dark_gray>"))
                        .body(List.of(
                                DialogBody.plainMessage(toMM("<yellow>Sinulla on radiotila käytössä. Voit muuttaa asiaa asetuksista.</yellow>")),
                                DialogBody.plainMessage(toMM("<gray>Äänenvoimakkuuden, nopeuden tai kaiutintilan säätäminen ei ole saatavilla radiotilassa.</gray>"))

                        ))
                        .afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)
                        .build())
                .type(DialogType.multiAction(
                        buttons,
                        ActionButton.builder(toMM("Sulje valikko"))
                                .width(150)
                                .action(DialogAction.customClick(
                                        (response, audience) -> {
                                            player.showDialog(createClosingDialog());
                                            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                                            serverPlayer.connection.send(ClientboundClearDialogPacket.INSTANCE); // no method for clearing dialogs in paper api
                                        },
                                        ClickCallback.Options.builder().build()
                                ))
                                .build(),
                        COLUMNS
                )));
    }

    private Dialog createSettingsDialog(Player player) {
        String currentSpeed = String.valueOf((int) musicManager.getSpeed(player));
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Musiikkivalikko</b> <dark_gray>(<yellow>Asetukset</yellow><dark_gray>)</dark_gray>"))
                        .body(Arrays.asList(
                                DialogBody.plainMessage(toMM("Asetukset ovat pelimuotokohtaisia.")),
                                DialogBody.plainMessage(toMM("Muutokset tallentuvat automaattisesti jos poistut tästä valikosta"))
                        ))
                        .inputs(Arrays.asList(
                                DialogInput.bool("radio", Component.text("Radiotila"))
                                        .initial(musicManager.getRadioEnabled(player))
                                        .build(),
                                DialogInput.bool("speaker", Component.text("Kaiutintila"))
                                        .initial(musicManager.getSpeakerEnabled(player))
                                        .build(),
                                DialogInput.bool("auto", Component.text("Jatkuva toisto"))
                                        .initial(musicManager.getAutoEnabled(player))
                                        .build(),
                                DialogInput.singleOption("speed", Component.text("Musiikin toistonopeus"),
                                        Arrays.asList(
                                                SingleOptionDialogInput.OptionEntry.create("1", toMM("1x"), currentSpeed.equals("1")),
                                                SingleOptionDialogInput.OptionEntry.create("2", toMM("2x"), currentSpeed.equals("2")),
                                                SingleOptionDialogInput.OptionEntry.create("4", toMM("4x"), currentSpeed.equals("4")),
                                                SingleOptionDialogInput.OptionEntry.create("8", toMM("8x"), currentSpeed.equals("8"))
                                        ))
                                    .build(),
                                DialogInput.singleOption("sorting", Component.text("Valikon järjestys"),
                                        Arrays.asList(
                                                SingleOptionDialogInput.OptionEntry.create(MusicSorting.ALPHABETICAL.name(), toMM(MusicSorting.ALPHABETICAL.getDisplayName() + " \uD83D\uDD20\uD83D\uDD3D"), musicManager.getSorting(player) == MusicSorting.ALPHABETICAL),
                                                SingleOptionDialogInput.OptionEntry.create(MusicSorting.ARTIST.name(), toMM(MusicSorting.ARTIST.getDisplayName() + " \uD83C\uDFBC\uD83D\uDD3D"), musicManager.getSorting(player) == MusicSorting.ARTIST),
                                                SingleOptionDialogInput.OptionEntry.create(MusicSorting.LENGTH.name(), toMM(MusicSorting.LENGTH.getDisplayName() + " \uD83D\uDD52\uD83D\uDD3D"), musicManager.getSorting(player) == MusicSorting.LENGTH)
                                        ))
                                    .build()
                        ))
                        .build())
                .type(DialogType.notice(
                        ActionButton.builder(Component.text("Päävalikko"))
                                .action(DialogAction.customClick((response, audience) -> {
                                            musicManager.setRadioEnabled(player, response.getBoolean("radio"));
                                            musicManager.setSpeakerEnabled(player, response.getBoolean("speaker"));
                                            musicManager.setAutoEnabled(player, response.getBoolean("auto"));
                                            musicManager.setSpeed(player, Byte.valueOf(response.getText("speed")));
                                            musicManager.setSorting(player, MusicSorting.valueOf(response.getText("sorting")));
                                            if (musicManager.getRadioEnabled(player)) {
                                                musicManager.clearSongPlayer(player);
                                            } else {
                                                if (musicManager.getSongPlayer(player) != null) {
                                                    SongPlayer sp = musicManager.getSongPlayer(player);
                                                    if (sp.isPlaying()) {
                                                        musicManager.createSongPlayer(player, sp.getCurrentSong(), sp.getTick(), true);
                                                    }
                                                }
                                            }
                                            show(player, musicManager.getPage(player));
                                        },
                                        ClickCallback.Options.builder().build()))
                                .width(150)
                                .build()

                )));
    }

    private Dialog createClosingDialog() {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Musiikkivalikko</b> <dark_gray>(<yellow>" + 0 +
                        "</yellow><dark_gray>/</dark_gray><yellow>" + 0 + "</yellow><dark_gray>)</dark_gray>")).build())
                .type(DialogType.notice(
                        ActionButton.builder(Component.text("Sulje valikko"))
                                .width(150)
                                .build()
                )));
    }

    private Dialog createVanillaMusicDialog(Player player) {
        List<ActionButton> buttons = new ArrayList<>();

        for (VanillaSongList track : VanillaSongList.values()) {

            buttons.add(
                    ActionButton.builder(toMM("<u>" + track.getDisplayName()))
                            .width(150)
                            .tooltip(toMM("<gray>Soita <white>" + track.getDisplayName()))
                            .action(DialogAction.customClick((response, audience) -> {

                                        player.stopSound(org.bukkit.SoundCategory.RECORDS);
                                        player.stopSound(org.bukkit.SoundCategory.MUSIC);

                                        playPacketSound(player, track.getResourceKey(), track.getSeed());

                                    },
                                    ClickCallback.Options.builder().build()
                            ))
                            .build()
            );
        }

        buttons.add(
                ActionButton.builder(toMM("<red>Lopeta toisto"))
                        .width(150)
                        .action(DialogAction.customClick((response, audience) -> {
                            player.stopSound(org.bukkit.SoundCategory.RECORDS);
                            player.stopSound(org.bukkit.SoundCategory.MUSIC);
                            player.sendMessage(toMM("<red>Musiikki pysäytetty"));
                        }, ClickCallback.Options.builder().build()))
                        .build()
        );

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Vanillamusiikki</b>")).build())
                .type(DialogType.multiAction(
                        buttons,
                        ActionButton.builder(toMM("Sulje valikko")).build(),
                        1
                )));
    }

    private void playPacketSound(Player player, String resourceKey, long seed) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        Identifier location = Identifier.parse(resourceKey);
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.getValue(location);

        if (soundEvent == null) {
            player.sendMessage(toMM("<red>Error: Sound '" + resourceKey + "' not found."));
            return;
        }

        Holder<SoundEvent> soundHolder = Holder.direct(soundEvent);

        SoundSource source = resourceKey.contains("music_disc") ? SoundSource.RECORDS : SoundSource.MUSIC;

        Location loc = player.getLocation();

        ClientboundSoundPacket packet = new ClientboundSoundPacket(
                soundHolder,
                source,
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                MAX_VALUE, // Volume
                1.0f, // Pitch
                seed
        );

        // 4. Send the packet directly via the connection
        serverPlayer.connection.send(packet);
    }

    private TextColor getColorFromTitle(String title) {
        int hash = title.hashCode();
        float hue = (hash & 0xFFFFFFF) / (float) 0xFFFFFFF;
        Color color = Color.getHSBColor(hue, 1.0f, 1.0f);
        return TextColor.color(color.getRed(), color.getGreen(), color.getBlue());
    }
}
