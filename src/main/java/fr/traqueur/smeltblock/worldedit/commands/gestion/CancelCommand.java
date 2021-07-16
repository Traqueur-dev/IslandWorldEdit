package fr.traqueur.smeltblock.worldedit.commands.gestion;

import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.traqueur.smeltblock.worldedit.api.utils.Utils;

public class CancelCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		
		WorldEditManager manager = WorldEditManager.getSingleton();
		Player player = (Player) sender;

		if (!manager.isOnWE(player)) {
			Utils.sendMessage(sender, "&cVous n'avez pas d'action en cours.");
			return false;
		}

		manager.getInWE().get(player.getUniqueId()).setCancel(true);
		Utils.sendMessage(sender, "&bVous &7venez de stopper une action.");
		return false;
	}

}
