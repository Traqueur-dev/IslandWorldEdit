package fr.traqueur.smeltblock.worldedit.managers.profiles.clazz;

import com.google.common.collect.Lists;
import fr.traqueur.smeltblock.worldedit.gui.clazz.GUItem;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class Profile {

    private String name;
    private List<GUItem> itemStock;

    public Profile(Player player) {
        this.name = player.getName();
        this.itemStock = Lists.newArrayList();

        //itemStock.add(new GUItem(this, Material.COBBLESTONE, 9999));
    }

    public File getProfileFile() {
        return new File(ProfileManager.getSingleton().getFile(), name + ".json");
    }

    public List<GUItem> getItemStock() {
        return itemStock;
    }

    public String getName() {
        return name;
    }

    public void removeItem(GUItem guItem) {
        itemStock.removeIf(g -> g.getMaterial().name().equals(guItem.getMaterial().name()));
    }

    public void addItem(ItemStack item, int amount) {
        GUItem guItem = itemStock.stream().filter(g -> g.getMaterial().name().equals(item.getType().name())).findFirst().orElse(null);
        if(guItem == null) {
            guItem = new GUItem(this, item.getType());
            itemStock.add(guItem);
        }
        guItem.add(amount);

    }

    public boolean has(Material item) {
        return itemStock.stream().anyMatch(g -> g.getMaterial().equals(item));
    }

    public GUItem get(Material m) {
        return itemStock.stream().filter(g -> g.getMaterial().equals(m)).findFirst().orElse(null);
    }
}
