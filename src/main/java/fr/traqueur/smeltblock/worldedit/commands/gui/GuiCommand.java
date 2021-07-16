package fr.traqueur.smeltblock.worldedit.commands.gui;

import com.google.common.collect.Lists;
import fr.traqueur.smeltblock.worldedit.api.utils.Utils;
import fr.traqueur.smeltblock.worldedit.gui.WorldEditProvider;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import fr.traqueur.smeltblock.worldedit.managers.profiles.clazz.Profile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GuiCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
       if(!(sender instanceof Player)) {
           Utils.sendMessage(sender, "&cLa commande ne peut-être exécuté qu'en jeu.");
           return true;
       }
       Player player = (Player) sender;
       if(!player.hasPermission("we.gui.use")) {
           Utils.sendMessage(player, "&cVous n'avez pas la permission de faire cela !");
           return false;
       }

       ProfileManager profileManager = ProfileManager.getSingleton();
       Profile profile = profileManager.getProfile(player);

       WorldEditProvider.getInventory(player, profile).open(player);
       return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Lists.newArrayList();
    }
}
