package net.quantum625.config.menu;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.quantum625.config.menu.Page;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookMenu {

    @Nullable BookMenu parent;

    ArrayList<Page> pages = new ArrayList<>();
    public BookMenu(@Nullable BookMenu parent) {
        this.parent = parent;
    }

    public BookMenu add(Page page) {
        pages.add(page);
        return this;
    }


    /**
     * @param
     * index Index of the page
     *
     * @return
     * Returns the page with given index
     */
    public Page get(int index) {return pages.get(index);}


    /**
     * Converts the pages to Components
     * @return
     * A List of Components, needed for show() method
     *
     */
    public List<Component> construct() {

        List<Component> pages = new ArrayList<>();

        for (Page page : this.pages) {
            pages.add(page.construct());
        }

        return pages;
    }

    /**
     * Opens the book menu for a given player
     */
    public void show(@NotNull Player player) {
        player.openBook(Book.book(Component.text("QuillConfig"), Component.text("QuillConfig"), construct()));
    }

}
