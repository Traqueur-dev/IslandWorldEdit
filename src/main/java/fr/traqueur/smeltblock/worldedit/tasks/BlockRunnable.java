package fr.traqueur.smeltblock.worldedit.tasks;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.google.common.collect.Maps;
import fr.dorvak.smeltblock.IslandMobCubes;
import fr.dorvak.smeltblock.core.Cube;
import fr.traqueur.smeltblock.worldedit.IslandWorldEdit;
import fr.traqueur.smeltblock.worldedit.api.utils.InventoryUtils;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import fr.traqueur.smeltblock.worldedit.managers.profiles.clazz.Profile;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.clazz.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.lynuxcraft.deadsilenceiv.gencubes.GenCubesAPI;
import us.lynuxcraft.deadsilenceiv.gencubes.gencube.GenCube;
import xzot1k.plugins.ds.DisplayShopsAPI;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class BlockRunnable extends BukkitRunnable {

    protected Player player;
    protected boolean cancel;
    protected LinkedList<Block> blocks;
    protected Material item;

    protected HashMap<ItemStack, Integer> blockForSave;

    public BlockRunnable(Player player, LinkedList<Block> blocks, Material item) {
        this.player = player;
        this.blocks = blocks;
        this.item = item;
        this.cancel = false;
        this.blockForSave = Maps.newHashMap();
        ProfileManager.getSingleton().getProfile(player).init();
    }

    public boolean isIgnoredBlock(Block b, Player player) {
        GenCube cube = GenCubesAPI.getCubeManager().getCubeByLocation(b.getLocation(), true);
        Cube mobcube = IslandMobCubes.getInstance().getCubeManager().getCube(b.getLocation());
        if (cube != null || mobcube != null) return true;

        Island island = SuperiorSkyblockAPI.getPlayer(player).getIsland();
        if (island == null) {
            return true;
        }
        if (!island.isInsideRange(b.getLocation())) return true;

        if(!b.getMetadata("PROCOSMETICS_BLOCK").isEmpty()) {
            return true;
        }


        DisplayShopsAPI api = IslandWorldEdit.getDisplayShopsAPI();
        if (api.getManager().getShop(b.getLocation()) != null) {
            return true;
        }

        Config config = WorldEditManager.getSingleton().getConfig();
        Material mat = b.getType();
        return config.getIgnoredBlocks().contains(mat.name());
    }

    public void saveBlock(Block b) {
        this.saveBlock(b.getType());
        ProfileManager.getSingleton().getProfile(player).save(b);
    }

    public void saveBlock(Material mat) {
        ItemStack toSave = new ItemStack(mat);
        if (blockForSave.containsKey(toSave)) {
            blockForSave.put(toSave, blockForSave.get(toSave) + 1);
        } else {
            blockForSave.put(toSave, 1);
        }
    }

    public void giveBlocks() {
        Profile profile = ProfileManager.getSingleton().getProfile(player);
        WorldEditManager.getSingleton().setupCorners(player);
        WorldEditManager.getSingleton().getTempVizualize().remove(player);
        blockForSave.forEach((item, q) -> {
            if (item != null && item.getType() != Material.AIR) {
                if (player.hasPermission("we.gui.use")) {
                    profile.addItem(item, q);
                } else {
                    InventoryUtils.addItem(player, item, q);
                }
            }
        });
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public HashMap<ItemStack, Integer> getBlockForSave() {
        return blockForSave;
    }

    public void setBlockForSave(HashMap<ItemStack, Integer> blockForSave) {
        this.blockForSave = blockForSave;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public LinkedList<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(LinkedList<Block> blocks) {
        this.blocks = blocks;
    }

    public Material getItem() {
        return item;
    }

    public void setItem(Material item) {
        this.item = item;
    }


}
