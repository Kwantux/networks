package dev.nanoflux.config.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;

public abstract class ComponentBuilder{

    // General text generation:

    public static Component text(String content) {
        return Component.text(content);
    }

    public static Component space() {
        return Component.empty();
    }

    public static Component bold(Component component) {
        return component.decorate(TextDecoration.BOLD);
    }
    public static Component bold(String text) {
        return Component.text(text).decorate(TextDecoration.BOLD);
    }

    public static Component underlined(Component component) {
        return component.decorate(TextDecoration.UNDERLINED);
    }
    public static Component underlined(String text) {
        return Component.text(text).decorate(TextDecoration.UNDERLINED);
    }

    public static Component italic(Component component) {
        return component.decorate(TextDecoration.ITALIC);
    }
    public static Component italic(String text) {
        return Component.text(text).decorate(TextDecoration.ITALIC);
    }

    public static Component strikethrough(Component component) {
        return component.decorate(TextDecoration.STRIKETHROUGH);
    }
    public static Component strikethrough(String text) {
        return Component.text(text).decorate(TextDecoration.STRIKETHROUGH);
    }

    public static Component menu(Component component, String menu) {
        return component.clickEvent(ClickEvent.runCommand("menu open " + menu));
    }
    public static Component menu(String text, String menu) {
        return Component.text(text).clickEvent(ClickEvent.runCommand("menu open " + menu));
    }

    public static Component url(Component component, String url) {
        return component.clickEvent(ClickEvent.openUrl(url));
    }
    public static Component url(String text, String url) {
        return Component.text(text).clickEvent(ClickEvent.openUrl(url));
    }

    public static Component command(Component component, String command) {
        return component.clickEvent(ClickEvent.runCommand(command));
    }
    public static Component command(String text, String command) {
        return Component.text(text).clickEvent(ClickEvent.runCommand(command));
    }




    // Configuration menu generation:

    public static Component valueBoolean(String id, String name, boolean value) {
        if (value) {
            return StyleSheet.defaultStyle.booleanTrue.append(text(name)).hoverEvent(HoverEvent.showText(text(id))).clickEvent(ClickEvent.runCommand("/config set " + id + " false"));
        }
        return StyleSheet.defaultStyle.booleanFalse.append(text(name)).hoverEvent(HoverEvent.showText(text(id))).clickEvent(ClickEvent.runCommand("/config set " + id + " true"));
    }

    public static Component valueInt(String id, String name, int value) {
        return StyleSheet.defaultStyle.editSign.append(text(name + ":  ")).append(Component.text(value)).hoverEvent(HoverEvent.showText(text(id))).clickEvent(ClickEvent.runCommand("/config edit int " + id));
    }

    public static Component valueDouble(String id, String name, double value) {
        return StyleSheet.defaultStyle.editSign.append(text(name + ":  ")).append(Component.text(value)).hoverEvent(HoverEvent.showText(text(id))).clickEvent(ClickEvent.runCommand("/config edit double " + id));
    }

    public static Component valueString(String id, String name, String value) {
        return StyleSheet.defaultStyle.editSign.append(text(name + ":  ")).append(text(value)).hoverEvent(HoverEvent.showText(text(id))).clickEvent(ClickEvent.runCommand("/config edit string " + id));
    }

    //TODO: Selection, Multiselection, Free entry List

}
