package fr.traqueur.smeltblock.worldedit.tasks;

import fr.traqueur.smeltblock.worldedit.api.particles.ParticleEffect;
import fr.traqueur.smeltblock.worldedit.api.utils.Cuboid;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import fr.traqueur.smeltblock.worldedit.managers.profiles.clazz.Profile;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class VisualizeRunnable extends BukkitRunnable {

    private WorldEditManager worldEditManager;
    private ProfileManager manager;

    public VisualizeRunnable() {
        worldEditManager = WorldEditManager.getSingleton();
        manager = ProfileManager.getSingleton();
    }

    @Override
    public void run() {
        List<Player> players = Bukkit.getOnlinePlayers().stream().filter(p -> manager.getProfile(p).isVisualize()).collect(Collectors.toList());
        players.addAll(worldEditManager.getTempVizualize());
        for(Player player : players) {
            if(player != null) {
                Cuboid cuboid = worldEditManager.getCuboid(player);
                if(cuboid != null) {
                    cuboid.getHollowCube(0.5).forEach(l -> ParticleEffect.FLAME.display(l, player));
                }
            }
        }
    }
}
