package fr.traqueur.smeltblock.worldedit.api.utils;

import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import org.bukkit.command.CommandSender;

public class Utils {
	
	public static void sendMessage(CommandSender player, String message) {
		player.sendMessage(
				WorldEditManager.getSingleton().getConfig().getPrefix() + " " + message.replaceAll("&", "§"));
	}
}
