package fr.traqueur.smeltblock.worldedit.listeners;

import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		WorldEditManager manager = WorldEditManager.getSingleton();
		ProfileManager profileManager = ProfileManager.getSingleton();
		manager.setupCorners(event.getPlayer());
		profileManager.createProfile(event.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		WorldEditManager manager = WorldEditManager.getSingleton();
		manager.eraseCorners(event.getPlayer());
		manager.getTempVizualize().remove(event.getPlayer());
	}
	
}
