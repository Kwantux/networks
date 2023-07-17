package net.quantum625.config.menu;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class MenuManager {

    private final JavaPlugin plugin;

    private final ArrayList<BookMenu> menus = new ArrayList<>();

    public MenuManager(JavaPlugin plugin) {
        this.plugin = plugin;

    }

    public List<Component> constructMenu() {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();

        pages.add(current_page);

        return pages;
    }

    public void showMenu(String menu, Player player) {
        player.openBook(Book.book(Component.text("QuillConfig"),Component.text("QuillConfig"), constructMenu()));
    }

}
