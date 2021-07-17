package fr.traqueur.smeltblock.worldedit.tasks;

import java.util.LinkedList;

import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import fr.traqueur.smeltblock.worldedit.managers.profiles.clazz.Profile;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import fr.traqueur.smeltblock.worldedit.api.utils.InventoryUtils;

public abstract class AbstractReplaceBlockRunnable extends BlockRunnable {

	private int quantity;
	protected Material newItem;
	
	public AbstractReplaceBlockRunnable(Player player, LinkedList<Block> blocks, Material item, Material newItem) {
		super(player, blocks, item);
		this.setNewItem(newItem);
		quantity = InventoryUtils.getItemCount(player, new ItemStack(newItem));
		if(player.hasPermission("we.gui.use")) {
			Profile profile = ProfileManager.getSingleton().getProfile(player);
			if(profile.has(newItem)) {
				quantity += profile.get(newItem).getAmount();
			}
		}
		this.getExactVolume();
	}
	
	public void getExactVolume() {
		this.getBlocks().removeIf(b -> b.getType() != this.getItem());
		this.getBlocks().removeIf(b -> this.isIgnoredBlock(b, player));
		if(quantity > this.getBlocks().size()) {
			this.setQuantity(this.getBlocks().size());
		}
		
		if(quantity < this.getBlocks().size()) {
			LinkedList<Block> blocks = Lists.newLinkedList();
			for(int i = 0; i < quantity; i++) {
				blocks.add(this.getBlocks().get(i));
			}
			this.setBlocks(blocks);
		}
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Material getNewItem() {
		return newItem;
	}

	public void setNewItem(Material newItem) {
		this.newItem = newItem;
	}

}
