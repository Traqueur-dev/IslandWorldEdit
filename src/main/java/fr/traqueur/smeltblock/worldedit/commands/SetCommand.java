package fr.traqueur.smeltblock.worldedit.commands;

import java.util.List;
import java.util.stream.Collectors;

import fr.traqueur.smeltblock.worldedit.api.utils.Cuboid;
import fr.traqueur.smeltblock.worldedit.managers.WorldEditManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import fr.traqueur.smeltblock.worldedit.api.utils.Utils;
import fr.traqueur.smeltblock.worldedit.managers.clazz.TypeCommand;

public class SetCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		WorldEditManager manager = WorldEditManager.getSingleton();

		if (!manager.goodCommand(sender)) {
			return false;
		}

		Cuboid cuboid = manager.getCuboid((Player) sender);

		if (args.length != 1) {
			Utils.sendMessage(sender, "&cUsage: /placer <block>");
			return false;
		}

		Material material = Material.matchMaterial(args[0]);
		if (material == null || !manager.getAllowedBlocks().contains(material.name())) {
			Utils.sendMessage(sender, "&cBlock pas autorisé par le plugin");
			return false;
		}

		int amount = manager.getAmount((Player) sender, material);
		if (amount == 0) {
			Utils.sendMessage(sender, "&cVous n'avez pas ce bloc dans votre inventaire.");
			return false;
		}

		manager.setBlock((Player) sender, cuboid.getBlocks(), material, true, TypeCommand.SET);

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("we.commands.set")) {
			return Lists.newArrayList();
		}

		if (args.length == 1) {
			return WorldEditManager.getSingleton().getAllowedBlocks().stream()
					.filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
		}

		return WorldEditManager.getSingleton().getAllowedBlocks();
	}

}
