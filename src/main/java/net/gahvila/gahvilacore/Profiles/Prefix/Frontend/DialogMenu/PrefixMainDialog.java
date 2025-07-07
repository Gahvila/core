package net.gahvila.gahvilacore.Profiles.Prefix.Frontend.DialogMenu;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.PrefixManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class PrefixMainDialog {

    private final PrefixManager prefixManager;

    public PrefixMainDialog(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
    }

    public void show(Player player) {
        player.showDialog(createMainDialog(player));
    }
    public Dialog createMainDialog(Player player) {
        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Prefix | Päävalikko</b>"))
                        .body(Arrays.asList(
                                DialogBody.plainMessage(toMM("Tällä hetkellä etuliitteesi näyttää tältä:")),
                                DialogBody.plainMessage(toMM(prefixManager.generatePrefixAndName(player)
                                        + " <yellow><b>></b></yellow> <white>moi, mun nimi on "
                                        + player.getName()
                                        + " ja mun lemppari prefix on "
                                        + prefixManager.getPrefix(player).getDisplayName()
                                        + "!")),
                                DialogBody.plainMessage(toMM("<st>                                           </st>"))
                        ))
                        .build())
                .type(DialogType.multiAction(
                        List.of(
                                ActionButton.builder(toMM("<b>Prefixin valinta</b>"))
                                        .action(DialogAction.staticAction((ClickEvent.runCommand("prefix setprefix")))).build(),
                                ActionButton.builder(toMM("<b>Värin valinta</b>"))
                                        .action(DialogAction.staticAction((ClickEvent.runCommand("prefix setcolor")))).build()
                        ),
                        ActionButton.builder(Component.text("Sulje valikko")).build(),
                        2
                )));
    }
}
