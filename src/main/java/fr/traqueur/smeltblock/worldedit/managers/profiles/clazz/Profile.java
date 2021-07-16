package fr.traqueur.smeltblock.worldedit.managers.profiles.clazz;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import fr.traqueur.smeltblock.worldedit.gui.clazz.GUItem;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Profile {

    private String name;
    private List<GUItem> itemStock;

    public Profile() {}

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
}
