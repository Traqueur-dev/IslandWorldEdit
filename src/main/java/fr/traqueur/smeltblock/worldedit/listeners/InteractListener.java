package fr.traqueur.smeltblock.worldedit.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;

import fr.traqueur.smeltblock.worldedit.api.utils.Utils;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;

public class InteractListener implements Listener {

	@EventHandler
	public void onInteractWand(PlayerInteractEvent event) {
		WorldEditManager manager = WorldEditManager.getSingleton();
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		ItemStack item = player.getInventory().getItemInMainHand();
		Action action = event.getAction();
		
		if (!manager.isWand(item, player) || block == null || block.getType() == Material.AIR
				|| event.getHand() == EquipmentSlot.OFF_HAND) {
			return;
		}
		Island island = SuperiorSkyblockAPI.getIslandAt(block.getLocation());
		if (island == null || !manager.isMember(player, island)) {
			Utils.sendMessage(player, "&bVous &7ne pouvez pas définir de &bpositions &7en dehors de votre île.");
			return;
		}

		int pos = 0;
		
		switch (action) {
		case LEFT_CLICK_BLOCK :
			pos = 0;
			break;
		case RIGHT_CLICK_BLOCK :
			pos = 1;
			break;
		default:
			return;
		}
		manager.addCorner(player, block.getLocation(), pos);
		
		event.setCancelled(true);
		Utils.sendMessage(player, "&bVous &7venez de définir la position &bn°" + (pos+1) + ".");
	}
	
}
