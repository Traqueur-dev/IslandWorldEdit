package fr.traqueur.smeltblock.worldedit.commands.gestion;

import java.util.List;

import fr.traqueur.smeltblock.worldedit.managers.WorldEditManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.traqueur.smeltblock.worldedit.api.utils.Utils;

public class CountCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		
		WorldEditManager manager = WorldEditManager.getSingleton();
		List<Block> blocks = manager.getCuboid((Player) sender).getBlocks();
		blocks.removeIf(b -> b.getType() == Material.AIR);
		Utils.sendMessage(sender, "&bVous &7avez &bx" + blocks.size() + " &7blocs dans votre selection.");
		return false;
	}

}
