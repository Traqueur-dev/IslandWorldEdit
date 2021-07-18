package fr.traqueur.smeltblock.worldedit.commands;

import java.util.List;
import java.util.stream.Collectors;

import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import fr.traqueur.smeltblock.worldedit.api.utils.Utils;

public class CutCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		WorldEditManager manager = WorldEditManager.getSingleton();

		if (!manager.goodCommand(sender, "we.commands.cut")) {
			return false;
		}

		if(args.length > 2) {
			Utils.sendMessage(sender, "&cUsage: /casser <block>");
			return false;
		}
		
		Material material = null;
		if(args.length == 1) {
			material = Material.matchMaterial(args[0]);
			if(material == null || !manager.getAllowedBlocks().contains(material.name())) {
				Utils.sendMessage(sender, "&cBlock pas autorisé par le plugin");
				return false;
			}
		}
		
		manager.destroyBlocks((Player) sender, manager.getCuboid((Player) sender).getBlocks(),material);
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("we.commands.cut")) {
			return Lists.newArrayList();
		}
		
		if (args.length == 1) {
			return WorldEditManager.getSingleton().getAllowedBlocks().stream()
					.filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
		}

		return WorldEditManager.getSingleton().getAllowedBlocks();
	}

}
