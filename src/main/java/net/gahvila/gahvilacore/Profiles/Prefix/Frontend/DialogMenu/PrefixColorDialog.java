package net.gahvila.gahvilacore.Profiles.Prefix.Frontend.DialogMenu;

import com.mojang.datafixers.util.Pair;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.Prefix;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType.Gradient;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType.Single;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixTypes;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.PrefixManager;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class PrefixColorDialog {

    private final PrefixManager prefixManager;

    public PrefixColorDialog(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
    }

    public void show(Player player) {
        player.showDialog(createTypeDialog(player));
    }

    public Dialog createTypeDialog(Player player) {
        List<Pair<String, ActionButton>> buttonPairs = new ArrayList<>();

        for (Gradient gradient : Gradient.values()) {
            if (player.hasPermission(gradient.getPermissionNode())) {
                String displayName = gradient.getDisplayName();
                ActionButton button = ActionButton.builder(toMM("<gradient:" + gradient.getGradient() + ">" + displayName))
                        .width(75)
                        .tooltip(toMM("<gradient:" + gradient.getGradient() + ">" + displayName))
                        .action(DialogAction.customClick((response, audience) -> {
                                    prefixManager.setPrefixType(player, PrefixTypes.GRADIENT);
                                    prefixManager.setGradient(player, gradient);
                                    show(player);
                                },
                                ClickCallback.Options.builder().build()
                        ))
                        .build();
                buttonPairs.add(Pair.of(displayName, button));
            }
        }

        for (Single single : Single.values()) {
            if (player.hasPermission(single.getPermissionNode())) {
                String displayName = single.getDisplayName();
                ActionButton button = ActionButton.builder(toMM("<" + single.getColor() + ">" + displayName))
                        .width(75)
                        .tooltip(toMM("<" + single.getColor() + ">" + displayName))
                        .action(DialogAction.customClick((response, audience) -> {
                                    prefixManager.setPrefixType(player, PrefixTypes.SINGLE);
                                    prefixManager.setSingle(player, single);
                                    show(player);
                                },
                                ClickCallback.Options.builder().build()
                        ))
                        .build();
                buttonPairs.add(Pair.of(displayName, button));
            }
        }

        buttonPairs.sort(Comparator.comparing(Pair::getFirst));

        List<ActionButton> dynamicButtons = buttonPairs.stream()
                .map(Pair::getSecond)
                .toList();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(toMM("<b>Prefix | Värin valinta</b>"))
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
