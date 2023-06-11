package net.quantum625.config.menu;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Page {

    private List<Component> components = new ArrayList<>();
    @Nullable Component footer = null;


    public Page(Component footer) {this.footer = footer;}
    public Page() {}

    public boolean addComponent(Component component) {
        components.add(component);
        if (countLines() > 14) return false;
        return true;
    }

    public boolean addComponents(List<Component> component) {
        for (Component c : component) {
            components.add(c);
        }
        if (countLines() > 14) return false;
        return true;
    }

    private int countLines() {
        if (footer != null) return components.size() + 1;
        return components.size();
    }

    public Component construct() {

        Component result = Component.text("");

        for (Component component : components) {

            if (component == null) {
                result = result.appendNewline();
                continue;
            }
            result = result.append(component);

            result = result.appendNewline();
        }

        if (footer != null) {
            result = result.append(footer);
        }

        return result;
    }
}
