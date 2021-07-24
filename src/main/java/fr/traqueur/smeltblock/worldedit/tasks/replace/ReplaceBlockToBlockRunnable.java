package fr.traqueur.smeltblock.worldedit.tasks.replace;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import fr.traqueur.smeltblock.worldedit.gui.clazz.GUItem;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.clazz.TypeCommand;
import fr.traqueur.smeltblock.worldedit.tasks.AbstractReplaceBlockRunnable;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.traqueur.smeltblock.worldedit.api.utils.EconomyUtils;
import fr.traqueur.smeltblock.worldedit.api.utils.InventoryUtils;

public class ReplaceBlockToBlockRunnable extends AbstractReplaceBlockRunnable {

	private WorldEditManager manager;
	private double price;
	private boolean payed;
	private Block b;
	
	public ReplaceBlockToBlockRunnable(Player player, LinkedList<Block> blocks,int size, Material item, TypeCommand command, Material newItem) {
		super(player, blocks, item, newItem);
		this.manager = WorldEditManager.getSingleton();
		payed = false;
		price = manager.getPrice(item, getQuantity(), command);

		if(size >= manager.getConfig().getQuantityLimit() && manager.getConfig().getQuantityLimit() != -1) {
			player.sendMessage(manager.getConfig().getPrefix() + " §cLa zone sélectionée est trop grande.");
			manager.getInWE().remove(player.getUniqueId());
			this.setCancel(true);
		}

		if(getQuantity() < size) {
			this.getBlocks().subList(getQuantity(), size).clear();
		}
	}

	@Override
	public void run() {
		if(!payed) {
			if(!EconomyUtils.has(player.getName(), price)) {
				player.sendMessage(manager.getConfig().getPrefix() + " §cVous n'avez pas assez d'argent.");
				manager.getInWE().remove(player.getUniqueId());
				this.cancel();
				return;
			}
			int globalQuantity = getQuantity();
			int quantityInInventory = InventoryUtils.getItemCount(player, new ItemStack(this.getNewItem()));
			int toRemoveInInv = Math.min(globalQuantity, quantityInInventory);
			globalQuantity -= toRemoveInInv;
			InventoryUtils.decrementItem(player, new ItemStack(this.getNewItem()), toRemoveInInv);
			if(globalQuantity > 0) {
				GUItem guItem = ProfileManager.getSingleton().getProfile(player).get(this.getNewItem());
				if(player.hasPermission("we.gui.use") && guItem != null) {
					guItem.remove(globalQuantity);
				}
			}
			EconomyUtils.withdraw(player.getName(), price);
			payed = true;
		}
		
		if(this.isCancel()) {
			this.cancel();
			this.giveBlocks();
			this.getExactVolume();
			int placed = this.getQuantity() - this.getBlocks().size();
			if(player.hasPermission("we.gui.use")) {
				ProfileManager.getSingleton().getProfile(player).addItem(new ItemStack(b.getType()), this.getBlocks().size());
			} else {
				InventoryUtils.addItem(player, new ItemStack(b.getType()), this.getBlocks().size());
			}
			player.sendMessage(manager.getConfig().getPrefix() + " §bVous §7avez placé §9x" + (placed) + " " + newItem.name() + "§7 pour §9"
					+ price + "⛁ §7.");
			manager.getInWE().remove(player.getUniqueId());
			return;
		}

		b = this.getBlocks().removeFirst();

		if(this.isIgnoredBlock(b, player) || b.getType() == newItem) {
			this.saveBlock(newItem);
			return;
		}

		//b.getType() == this.getItem().parseMaterial() && b.getData() == this.getItem().getData()
		if(b.getBlockData().equals(this.getItem().createBlockData())) {
			this.saveBlock(b);
			if(!b.getChunk().isLoaded()) {
				b.getChunk().load();
			}
			manager.setBlockInNativeWorld(player, b.getLocation(), this.getNewItem().createBlockData(), false);
		}
		
		if(blocks.size() == 0) {
			this.giveBlocks();
			player.sendMessage(manager.getConfig().getPrefix() + " §bVous §7avez placé §9x" + (this.getQuantity()) + " " + newItem.name() + "§7 pour §9"
					+ price + "⛁ §7.");
			manager.getInWE().remove(player.getUniqueId());
			this.cancel();
		}
	}
}
