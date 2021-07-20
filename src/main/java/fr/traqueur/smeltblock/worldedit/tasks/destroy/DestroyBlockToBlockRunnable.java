package fr.traqueur.smeltblock.worldedit.tasks.destroy;

import java.util.LinkedList;

import com.google.common.collect.Lists;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.clazz.TypeCommand;
import fr.traqueur.smeltblock.worldedit.tasks.AbstractDestroyBlockRunnable;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import fr.traqueur.smeltblock.worldedit.api.utils.EconomyUtils;

public class DestroyBlockToBlockRunnable extends AbstractDestroyBlockRunnable {

	private WorldEditManager manager;
	private double price;
	private boolean payed;
	private Block b;
	
	public DestroyBlockToBlockRunnable(Player player, LinkedList<Block> blocks, int size, Material item, TypeCommand command) {
		super(player, blocks, item);
		this.manager = WorldEditManager.getSingleton();
		payed = false;
		price = manager.getPrice(item, getQuantity(), command);

		if(size >= manager.getConfig().getQuantityLimit() && manager.getConfig().getQuantityLimit() != -1) {
			player.sendMessage(manager.getConfig().getPrefix() + " §cLa zone sélectionée est trop grande.");
			manager.getInWE().remove(player.getUniqueId());
			this.setCancel(true);
		}

		if(getQuantity() > 2240 && !player.hasPermission("we.gui.use")) {
			this.setQuantity(2240);
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
			EconomyUtils.withdraw(player.getName(), price);
			payed = true;
		}
		
		if(this.isCancel()) {
			this.cancel();
			this.giveBlocks();
			this.getExactVolume();
			int placed = this.getQuantity() - this.getBlocks().size();
			player.sendMessage(manager.getConfig().getPrefix() + " §bVous §7avez cassé §9x" + (placed) + " blocs§7 pour §9"
					+ price + "⛁ §7.");
			manager.getInWE().remove(player.getUniqueId());
			return;
		}
		
		b = this.getBlocks().getFirst();
		if(this.isIgnoredBlock(b, player)) {
			this.getBlocks().removeFirst();
			return;
		}

		if(item != null) {
			if(item == b.getType()) {
				this.saveBlock(b);
				if(!b.getChunk().isLoaded()) {
					b.getChunk().load();
				}
				manager.setBlockInNativeWorld(player, b.getLocation(), Material.AIR.createBlockData(), false);
				this.getBlocks().removeFirst();
			}
		} else {
			this.saveBlock(b);
			if(!b.getChunk().isLoaded()) {
				b.getChunk().load();
			}
			manager.setBlockInNativeWorld(player, b.getLocation(), Material.AIR.createBlockData(), false);
			this.getBlocks().removeFirst();	
		}
		
		if(blocks.size() == 0) {
			this.giveBlocks();
			int placed = this.getQuantity() - this.getBlocks().size();
			player.sendMessage(manager.getConfig().getPrefix() + " §bVous §7avez cassé §9x" + (placed) + " blocs§7 pour §9"
					+ price + "⛁ §7.");
			manager.getInWE().remove(player.getUniqueId());
			this.cancel();
		}
	}
}
