package fr.traqueur.smeltblock.worldedit.commands.gestion;

import com.google.common.collect.Lists;
import fr.traqueur.smeltblock.worldedit.api.particles.ParticleEffect;
import fr.traqueur.smeltblock.worldedit.api.utils.Cuboid;
import fr.traqueur.smeltblock.worldedit.api.utils.Utils;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import fr.traqueur.smeltblock.worldedit.managers.profiles.clazz.Profile;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VisualizeCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        WorldEditManager manager = WorldEditManager.getSingleton();

        if (!(sender instanceof Player)) {
            Utils.sendMessage(sender, "&cLa commande ne peut-être exécuté qu'en jeu.");
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("we.commands.visualize")) {
            Utils.sendMessage(player, "&cVous n'avez pas la permission de faire cela !");
            return false;
        }

        if (args.length == 0) {
            Cuboid cuboid = manager.getCuboid(player);

            if (manager.isOnWE(player)) {
                Utils.sendMessage(player, "&cVous êtes déjà en train de faire une commande.");
                return false;
            }

            if (cuboid == null) {
                Utils.sendMessage(player, "&cVos positions ne sont pas définies.");
                return false;
            }

            if (!manager.positionsIsSameWorld(player)) {
                Utils.sendMessage(player, "&cVos positions ne sont pas dans le même monde.");
                return false;
            }

            manager.getTempVizualize().add(player);
            return true;
        }


        Profile profile = ProfileManager.getSingleton().getProfile(player);
        switch (args[0]) {
            case "activer":
            case "enabled":
            case "enable":
            case "on":
                profile.setVisualize(true);
                Utils.sendMessage(player, "&9Visualiseur&f: &aON");
                break;
            case "désactiver":
            case "desactiver":
            case "disable":
            case "off":
                profile.setVisualize(false);
                Utils.sendMessage(player, "&9Visualiseur&f: &cOFF");
                break;
            case "toggle":
            case "basculer":
                profile.toggleVisualize();
                Utils.sendMessage(player,"&9Visualiseur&f: " + (profile.isVisualize() ?"&aON" : "&cOFF"));
                break;
            default:
                Utils.sendMessage(player, "&cCommande non reconnue");
                break;
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> array = Lists.newArrayList("activer", "enable", "enabled",
                "désactiver", "desactiver", "disable", "toggle", "basculer", "on", "off");

        if (args.length == 1) {
            return array.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }

        return array;
    }
}
