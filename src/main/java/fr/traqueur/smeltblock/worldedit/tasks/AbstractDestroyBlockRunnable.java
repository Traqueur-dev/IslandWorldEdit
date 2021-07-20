package fr.traqueur.smeltblock.worldedit.tasks;

import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

public abstract class AbstractDestroyBlockRunnable extends BlockRunnable {

	private int quantity;
	
	public AbstractDestroyBlockRunnable(Player player, LinkedList<Block> blocks, Material item) {
		super(player, blocks, item);
		quantity = blocks.size();
	}
	
	private boolean isNotSame(Material mat, Material item) {
		return mat != item;
	}
	
	public void getExactVolume() {
		this.getBlocks().removeIf(b -> this.isIgnoredBlock(b, player));
		if(item != null) {
			this.getBlocks().removeIf(b -> this.isNotSame(b.getType(), item));
		} else {
			this.getBlocks().removeIf(b -> b.getType() == Material.AIR);
		}
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
