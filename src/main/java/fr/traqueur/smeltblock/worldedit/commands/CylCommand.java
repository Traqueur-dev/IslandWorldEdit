package fr.traqueur.smeltblock.worldedit.commands;

import java.util.List;
import java.util.stream.Collectors;

import fr.traqueur.smeltblock.worldedit.api.utils.Cuboid;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import fr.traqueur.smeltblock.worldedit.api.utils.Utils;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.clazz.TypeCommand;

public class CylCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		WorldEditManager manager = WorldEditManager.getSingleton();
		
		
		if (!manager.goodCommand(sender, "we.commands.cyl")) {
			return false;
		}
		
		Cuboid cuboid = manager.getCuboid((Player) sender);
		
		if(args.length < 2) {
			Utils.sendMessage(sender, "&cUsage: /cercle <block> <hauteur> [-h]");
			return false;
		}
		
		Material material = Material.matchMaterial(args[0]);
		if(material == null || !manager.getAllowedBlocks().contains(material.name())) {
			Utils.sendMessage(sender, "&cBlock pas autorisé par le plugin");
			return false;
		}

		if (cuboid.getUpperY() - cuboid.getLowerY() != 0) {
			Utils.sendMessage(sender, " &cSélectionnez qu'un diamètre du cercle");
			return false;
		}

		if (cuboid.getUpperZ() - cuboid.getLowerZ() != 0 && cuboid.getUpperX() - cuboid.getLowerX() != 0) {
			Utils.sendMessage(sender, " &cSélectionnez qu'un diamètre du cercle");
			return false;
		}
		
		Integer height = Ints.tryParse(args[1]);
		if (height == null) {
			Utils.sendMessage(sender, "&cLa taille du cylindre n'est pas correcte.");
			return false;
		}

		int amount = manager.getAmount((Player) sender,material);
		if (amount == 0) {
			Utils.sendMessage(sender, "&cVous n'avez pas ce bloc dans votre inventaire.");
			return false;
		}
		if (Lists.newArrayList(args).contains("-h") || Lists.newArrayList(args).contains("-c")) {
			manager.setDifferentCuboid((Player) sender, manager.getCyl((Player) sender, height, false), material, TypeCommand.CYL);
		} else {
			manager.setDifferentCuboid((Player) sender, manager.getCyl((Player) sender, height, true), material, TypeCommand.CYL);
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("we.commands.cyl")) {
			return Lists.newArrayList();
		}
	
		if (args.length == 1) {
			return WorldEditManager.getSingleton().getAllowedBlocks().stream()
					.filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
		}

		return WorldEditManager.getSingleton().getAllowedBlocks();
	}

}
