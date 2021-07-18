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
import fr.traqueur.smeltblock.worldedit.managers.worldedit.clazz.TypeCommand;

public class ReplaceCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		WorldEditManager manager = WorldEditManager.getSingleton();

		if (!manager.goodCommand(sender,"we.commands.replace")) {
			return false;
		}

		if(args.length != 2) {
			Utils.sendMessage(sender, "&cUsage: /remplacer <block> <block>");
			return false;
		}
		
		Material materialOld = Material.matchMaterial(args[0]);
		Material materialNew = Material.matchMaterial(args[1]);
		if(materialOld == null || !manager.getAllowedBlocks().contains(materialOld.name())) {
			Utils.sendMessage(sender, "&cBlock remplacé pas autorisé par le plugin");
			return false;
		}
		
		if(materialNew == null || !manager.getAllowedBlocks().contains(materialNew.name())) {
			Utils.sendMessage(sender, "&cBlock remplaçant pas autorisé par le plugin");
			return false;
		}
		
		int amount = manager.getAmount((Player) sender, materialNew);
		if (amount == 0) {
			Utils.sendMessage(sender, "&cVous n'avez pas ce bloc dans votre inventaire.");
			return false;
		}

		manager.replaceBlocks((Player) sender, manager.getCuboid((Player) sender).getBlocks(), materialOld, materialNew, TypeCommand.SET);
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("we.commands.replace")) {
			return Lists.newArrayList();
		}
	
		if (args.length == 1) {
			return WorldEditManager.getSingleton().getAllowedBlocks().stream()
					.filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
		}
		
		if (args.length == 2) {
			return WorldEditManager.getSingleton().getAllowedBlocks().stream()
					.filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
		}

		return WorldEditManager.getSingleton().getAllowedBlocks();
	}

}
