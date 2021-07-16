package fr.traqueur.smeltblock.worldedit.gui;

import fr.traqueur.smeltblock.worldedit.IslandWorldEdit;
import fr.traqueur.smeltblock.worldedit.api.inventory.ClickableItem;
import fr.traqueur.smeltblock.worldedit.api.inventory.SmartInventory;
import fr.traqueur.smeltblock.worldedit.api.inventory.content.InventoryContents;
import fr.traqueur.smeltblock.worldedit.api.inventory.content.InventoryProvider;
import fr.traqueur.smeltblock.worldedit.api.inventory.content.Pagination;
import fr.traqueur.smeltblock.worldedit.api.utils.ItemBuilder;
import fr.traqueur.smeltblock.worldedit.gui.clazz.GUItem;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import fr.traqueur.smeltblock.worldedit.managers.profiles.clazz.Profile;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.clazz.Config;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldEditProvider implements InventoryProvider {

    private static SmartInventory INVENTORY = null;

    private HeadDatabaseAPI headApi;
    private Config config;

    private Player player;
    private Profile profile;

    public static SmartInventory getInventory(Player player, Profile profile) {
        if(INVENTORY == null) {
            INVENTORY = SmartInventory.builder()
                    .provider(new WorldEditProvider(player, profile))
                    .id(profile.getName() + "'s worldedit stock")
                    .closeable(true)
                    .title(WorldEditManager.getSingleton().getConfig().getGuiName())
                    .size(6,9)
                    .build();
        }

        return INVENTORY;
    }

    public WorldEditProvider(Player player, Profile profile) {
        this.profile = profile;
        this.player = player;
        this.config = WorldEditManager.getSingleton().getConfig();
        this.headApi = IslandWorldEdit.getInstance().getHeadApi();
    }

    private void addBlueGlassPane(InventoryContents contents, int row, int column) {
        ItemStack item = new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).displayname("§1")
                .addFlags(ItemFlag.HIDE_ATTRIBUTES).build();
        contents.set(row, column, ClickableItem.empty(item));
    }

    private void setupDecorativeContent(InventoryContents contents, Pagination pagination) {
        int page = pagination.getPage();

        contents.set(0,0, ClickableItem.empty(new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).displayname("§1")
                .addFlags(ItemFlag.HIDE_ATTRIBUTES).build()));

        addBlueGlassPane(contents,0, 1);
        addBlueGlassPane(contents,0, 7);
        ItemStack item;
        ItemMeta itemM;
        item = headApi.getItemHead("5650");
        itemM = item.getItemMeta();
        itemM.setDisplayName("§7Informations");
        itemM.setLore(config.getLoreInventory());
        item.setItemMeta(itemM);
        contents.set(0, 8, ClickableItem.empty(item));

        addBlueGlassPane(contents,1, 0);
        addBlueGlassPane(contents,1, 1);
        addBlueGlassPane(contents,1, 7);
        addBlueGlassPane(contents,1, 8);

        addBlueGlassPane(contents, 2, 0);
        addBlueGlassPane(contents, 2, 8);

        addBlueGlassPane(contents, 3, 0);
        addBlueGlassPane(contents, 3, 8);

        addBlueGlassPane(contents,4, 0);
        addBlueGlassPane(contents,4, 1);
        addBlueGlassPane(contents,4, 7);
        addBlueGlassPane(contents,4, 8);

        addBlueGlassPane(contents,5, 1);
        addBlueGlassPane(contents,5, 2);
        addBlueGlassPane(contents,5, 6);
        addBlueGlassPane(contents,5, 7);

        // Pages
        item = headApi.getItemHead("37794");
        itemM = item.getItemMeta();
        itemM.setDisplayName("§7Page précédente");
        item.setItemMeta(itemM);
        contents.set(5,0, ClickableItem.of(item, e -> getInventory(player, profile).open(player, pagination.previous().getPage())));

        item = headApi.getItemHead("37795");
        itemM = item.getItemMeta();
        itemM.setDisplayName("§7Page suivante");
        item.setItemMeta(itemM);
        contents.set(5,8, ClickableItem.of(item, e -> getInventory(player, profile).open(player, pagination.next().getPage())));
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();
        pagination.setItemsPerPage(32);
        this.setupDecorativeContent(contents, pagination);
        List<GUItem> itemStock = profile.getItemStock();
        pagination.setItems(itemStock.stream().map(g -> g.toClickableItem(pagination.getPage())).toArray(ClickableItem[]::new));
        Stream.of(pagination.getPageItems()).forEach(contents::add);

    }
}
