package fr.traqueur.smeltblock.worldedit.tasks;

import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import fr.traqueur.smeltblock.worldedit.api.utils.InventoryUtils;

public abstract class AbstractPlaceBlockRunnable extends BlockRunnable {

	private boolean replace;
	private int quantity;
	
	public AbstractPlaceBlockRunnable(Player player, LinkedList<Block> blocks, Material item, boolean replace) {
		super(player, blocks, item);
		this.replace = replace;
		quantity = InventoryUtils.getItemCount(player, new ItemStack(item));
		this.getExactVolume();
	}
	
	public void getExactVolume() {
		this.getBlocks().removeIf(b -> this.isIgnoredBlock(b, player));
		if(replace) {
			this.getBlocks().removeIf(b -> b.getType() == this.getItem());
		} else {
			this.getBlocks().removeIf(b -> b.getType() != Material.AIR);
		}
		
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
	

	public boolean isReplace() {
		return replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}
	

}
