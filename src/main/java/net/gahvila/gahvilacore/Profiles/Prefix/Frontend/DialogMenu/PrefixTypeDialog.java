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
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.Prefix;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.PrefixManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.gahvila.gahvilacore.GahvilaCore.instance;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class PrefixTypeDialog {

    private final PrefixManager prefixManager;

    public PrefixTypeDialog(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
    }

    public void show(Player player) {
        player.showDialog(createTypeDialog(player));
    }

    public Dialog createTypeDialog(Player player) {
        List<ActionButton> dynamicButtons = new ArrayList<>();

        for (Prefix prefix : Prefix.values()) {
            if (player.hasPermission(prefix.getPermissionNode())) {
                dynamicButtons.add(
                        ActionButton.builder(toMM("<b>" + prefix.getDisplayName()))
                                .width(75)
                                .action(DialogAction.customClick((response, audience) -> {
                                            prefixManager.setPrefix(player, prefix);
                                            show(player);
                                        },
                                        ClickCallback.Options.builder().build()
                                ))
                                .build()
                );
            }
        }

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Prefix | Prefixin valinta</b>"))
                        .body(List.of(
                                DialogBody.plainMessage(toMM("Tällä hetkellä etuliitteesi näyttää tältä:")),
                                DialogBody.plainMessage(toMM(prefixManager.generatePrefixAndName(player)
                                        + " <yellow><b>></b></yellow> <white>moi, mun nimi on "
                                        + player.getName()
                                        + " ja mun lemppari prefix on "
                                        + prefixManager.getPrefix(player).getDisplayName()
                                        + "!")),
                                DialogBody.plainMessage(toMM("<st>                                           </st>")),
                                DialogBody.plainMessage(toMM("Valitse alta etuliite minkä haluat itsellesi käyttöön:"))
                        ))
                        .build())
                .type(DialogType.multiAction(
                        dynamicButtons,
                        ActionButton.builder(toMM("Päävalikko"))
                                .action(DialogAction.staticAction(ClickEvent.runCommand("prefix")))
                                .build(),
                        4
                )));
    }
}
