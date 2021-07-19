package fr.traqueur.smeltblock.worldedit.managers.profiles.clazz;

import fr.traqueur.smeltblock.worldedit.api.utils.InventoryUtils;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

public class BlockLocation {

    private Material material;
    private Location location;
    private BlockData data;

    public BlockLocation(Material material, Location location, BlockData data) {
        this.material = material;
        this.location = location;
        this.data = data;
    }

    public Material getMaterial() {
        return material;
    }

    public Location getLocation() {
        return location;
    }

    public void undo(Profile profile) {
        Block block = location.getWorld().getBlockAt(getLocation());
        if (block.getType() != Material.AIR) {
            if(Bukkit.getPlayer(profile.getName()).hasPermission("we.gui.use")) {
                profile.addItem(new ItemStack(block.getType()), 1);
            } else {
                InventoryUtils.addItem(Bukkit.getPlayer(profile.getName()), new ItemStack(block.getType()), 1);
            }

        }

        WorldEditManager.getSingleton().setBlockInNativeWorld(Bukkit.getPlayer(profile.getName()), getLocation(), getMaterial().createBlockData(), false);
        block.setBlockData(data);

        if (getMaterial() != Material.AIR) {
            if(Bukkit.getPlayer(profile.getName()).hasPermission("we.gui.use")) {
                if(profile.has(getMaterial())) {
                    profile.get(getMaterial()).remove(1);
                } else {
                    InventoryUtils.decrementItem(Bukkit.getPlayer(profile.getName()), new ItemStack(getMaterial()), 1);
                }
            } else {
                InventoryUtils.decrementItem(Bukkit.getPlayer(profile.getName()), new ItemStack(getMaterial()), 1);
            }
        }

    }
}
