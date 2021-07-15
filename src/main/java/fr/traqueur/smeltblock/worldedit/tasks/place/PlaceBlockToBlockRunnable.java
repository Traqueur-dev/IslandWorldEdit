package fr.traqueur.smeltblock.worldedit.tasks.place;

import java.util.LinkedList;

import fr.traqueur.smeltblock.worldedit.managers.WorldEditManager;
import fr.traqueur.smeltblock.worldedit.managers.clazz.TypeCommand;
import fr.traqueur.smeltblock.worldedit.tasks.AbstractPlaceBlockRunnable;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.traqueur.smeltblock.worldedit.api.utils.EconomyUtils;
import fr.traqueur.smeltblock.worldedit.api.utils.InventoryUtils;

public class PlaceBlockToBlockRunnable extends AbstractPlaceBlockRunnable {

	private WorldEditManager manager;
	private double price;
	private boolean payed;
	private Block b;
	
	public PlaceBlockToBlockRunnable(Player player, LinkedList<Block> blocks, Material item, TypeCommand command, boolean replace) {
		super(player, blocks, item, replace);
		this.manager = WorldEditManager.getSingleton();
		payed = false;
		price = manager.getPrice(item, getQuantity(), command);		
	}

	
	@Override
	public void run() {
		if(getQuantity() >= 50000) {
			player.sendMessage(manager.getConfig().getPrefix() + " §cLa zone sélectionée est trop grande.");
			manager.getInWE().remove(player.getUniqueId());
			this.cancel();
			return;
		}
		
		if(this.getQuantity() == 0) {
			player.sendMessage(manager.getConfig().getPrefix() + " §cIl n'y a aucun bloc à changer dans votre selection.");
			manager.getInWE().remove(player.getUniqueId());
			this.cancel();
			return;
		}
		
		if(!payed) {
			if(!EconomyUtils.has(player.getName(), price)) {
				player.sendMessage(manager.getConfig().getPrefix() + " §cVous n'avez pas assez d'argent.");
				manager.getInWE().remove(player.getUniqueId());
				this.cancel();
				return;
			}
			InventoryUtils.decrementItem(player, new ItemStack(item), getQuantity());
			EconomyUtils.withdraw(player.getName(), price);
			payed = true;
		}
		
		if(this.isCancel()) {
			this.cancel();
			this.giveBlocks();
			int placed = this.getQuantity() - this.getBlocks().size();
			InventoryUtils.addItem(player, new ItemStack(item), this.getBlocks().size());
			player.sendMessage(manager.getConfig().getPrefix() + " §bVous §7avez placé §9x" + (placed) + " " + item + "§7.");
			manager.getInWE().remove(player.getUniqueId());
			return;
		}
		
		b = this.getBlocks().getFirst();
		this.saveBlock(b);
		
		manager.setBlockInNativeWorld(b.getLocation(), this.getItem().createBlockData(), false);
		this.getBlocks().removeFirst();
		
		if(blocks.size() == 0) {
			this.giveBlocks();
			player.sendMessage(manager.getConfig().getPrefix() + " §bVous §7avez placé §9x" + (this.getQuantity()) + " " + item + "§7.");
			manager.getInWE().remove(player.getUniqueId());
			this.cancel();
			return;
		}
	}
}
