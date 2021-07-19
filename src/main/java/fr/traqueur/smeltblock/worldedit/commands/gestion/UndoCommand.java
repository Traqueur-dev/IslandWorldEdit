package fr.traqueur.smeltblock.worldedit.commands.gestion;

import fr.traqueur.smeltblock.worldedit.IslandWorldEdit;
import fr.traqueur.smeltblock.worldedit.api.utils.Utils;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import fr.traqueur.smeltblock.worldedit.managers.profiles.clazz.Profile;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.clazz.TypeCommand;
import fr.traqueur.smeltblock.worldedit.tasks.undo.UndoRunnable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UndoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        WorldEditManager manager = WorldEditManager.getSingleton();

        if(!manager.goodCommand(sender, "we.commands.undo")) {
            return false;
        }

        Player player = (Player) sender;
        Profile profile = ProfileManager.getSingleton().getProfile(player);

        if (profile.getUndo().isEmpty()) {
            Utils.sendMessage(player, "&cVous n'avez rien a révoquer.");
            return true;
        }

        if (!profile.canUndo()) {
            Utils.sendMessage(player, "&cVous n'avez pas les blocks nécessaire pour révoquer votre action.");
            return true;
        }

        new UndoRunnable(player, profile, TypeCommand.UNDO)
				.runTaskTimer(IslandWorldEdit.getInstance(), 0, 1);

        profile.init();
        return true;
    }

}
