package fr.traqueur.smeltblock.worldedit.commands.gestion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UndoCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage("§cen cours de dev");
		return false;
	}

}
