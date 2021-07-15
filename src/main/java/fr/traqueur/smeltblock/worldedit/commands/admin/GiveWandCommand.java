package fr.traqueur.smeltblock.worldedit.commands.admin;

import java.util.List;
import java.util.stream.Collectors;

import fr.traqueur.smeltblock.worldedit.managers.WorldEditManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import fr.traqueur.smeltblock.worldedit.api.utils.Utils;

public class GiveWandCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player target = null;
		if (sender instanceof Player) {
			target = (Player) sender;
		}

		if (!sender.hasPermission("we.commands.givewand")) {
			Utils.sendMessage(sender, "&cVous n'avez pas la permission de faire cela !");
			return false;
		}

		switch (args.length) {
		case 1:
			this.command(sender, target, args);
			break;
		case 2:
			target = Bukkit.getPlayer(args[1]);
			this.command(sender, target, args);
			break;
		default:
			Utils.sendMessage(sender, "&cUsage: /givewand <utilisation> [joueur]");
			return false;
		}

		return false;
	}

	private boolean command(CommandSender sender, Player target, String[] args) {
		WorldEditManager manager = WorldEditManager.getSingleton();

		Integer uses = Ints.tryParse(args[0]);
		if (uses == null) {
			Utils.sendMessage(sender, "&cLe nombre d'utilisation est incorrecte.");
			return false;
		}
		if (target == null) {
			Utils.sendMessage(sender, "Le joueur n'est pas valide");
			return false;
		}

		manager.applyWand(target, uses);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("we.commands.givewand")) {
			return Lists.newArrayList();
		}

		switch (args.length) {
		case 2:
			return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList()).stream()
					.filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
		default:
			return Lists.newArrayList();
		}

	}

}
