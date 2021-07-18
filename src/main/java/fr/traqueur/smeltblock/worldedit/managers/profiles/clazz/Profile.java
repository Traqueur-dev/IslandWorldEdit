package fr.traqueur.smeltblock.worldedit.managers.profiles.clazz;

import com.google.common.collect.Lists;
import fr.traqueur.smeltblock.worldedit.api.utils.InventoryUtils;
import fr.traqueur.smeltblock.worldedit.gui.clazz.GUItem;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Profile {

    private String name;
    private List<GUItem> itemStock;
    private transient LinkedList<BlockLocation> undo;

    public Profile(Player player) {
        this.name = player.getName();
        this.itemStock = Lists.newArrayList();
        this.init();
        //itemStock.add(new GUItem(this, Material.COBBLESTONE, 9999));
    }

    public void init() {
        undo = Lists.newLinkedList();
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
        if (guItem == null) {
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

    public void save(Block b) {

        undo.add(new BlockLocation(b.getType(), b.getLocation(), b.getBlockData()));
    }

    public LinkedList<BlockLocation> getUndo() {
        return undo;
    }

    public boolean canUndo() {
        HashMap<Material, Integer> countBlocks = new HashMap<>();

        for (BlockLocation blockLocation : getUndo()) {
            Material blockMaterial = blockLocation.getMaterial();
            if (!countBlocks.containsKey(blockMaterial)) {
                countBlocks.put(blockMaterial, 1);
            } else {
                countBlocks.put(blockMaterial, countBlocks.get(blockMaterial) + 1);
            }
        }

        for (Map.Entry<Material, Integer> entry : countBlocks.entrySet()) {
            Material mat = entry.getKey();
            if (mat == Material.AIR)
                continue;

            int amount = entry.getValue();
            if (this.has(mat)) {
                amount -= this.get(mat).getAmount();
            }
            amount -= InventoryUtils.getItemCount(Bukkit.getPlayer(name), new ItemStack(mat));
            if (amount > 0) {
                return false;
            }
        }
        return true;
    }
}
