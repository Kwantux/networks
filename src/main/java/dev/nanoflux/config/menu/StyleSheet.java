package dev.nanoflux.config.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class StyleSheet {

    public Component booleanFalse = Component.text("[").append(Component.text("X").color(TextColor.color(255, 0, 0))).append(Component.text("]"));
    public Component booleanTrue = Component.text("[").append(Component.text("V").color(TextColor.color(0, 255, 0))).append(Component.text("]"));

    public Component editSign = Component.text("[").append(Component.text("E")).append(Component.text("]"));


    private StyleSheet() {}

    public static StyleSheet defaultStyle = new StyleSheet();
}
