package fr.traqueur.smeltblock.worldedit.api.utils;

import fr.traqueur.smeltblock.worldedit.IslandWorldEdit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Logger {

	public enum Level {
		INFO("§e", "INFO: "), SUCCESS("§a", "SUCCESS: "), WARNING("§c", "WARNING: "), DEBUG("§5", "DEBUG: "),
		SEVERE("§4", "ERROR: ");

		private String color;
		private String prefix;

		Level(String color, String prefix) {
			this.color = color;
			this.prefix = prefix;
		}

		public String getColor() {
			return this.color;
		}

		public String getPrefix() {
			return this.prefix;
		}
	}

	public static void log(String message) {
		log(Logger.Level.INFO, message);
	}

	public static void warning(String message) {
		log(Logger.Level.WARNING, message);
	}

	public static void error(String message) {
		log(Logger.Level.SEVERE, message);
	}

	public static void success(String message) {
		log(Logger.Level.SUCCESS, message);
	}

	public static void log(Logger.Level level, String message) {
		log(level, IslandWorldEdit.getInstance(), message);
	}
	
	public static void log(Level level, JavaPlugin plugin, String message) {
		log(level, plugin, message, true);
	}

	public static void log(Level level, JavaPlugin plugin, String message, boolean color) {
		log(level, plugin.getName(), message, color);
	}

	public static void log(Level level, String prefix, String message) {
		log(level, prefix, message, true);
	}

	public static void log(Level level, String prefix, String message, boolean color) {
		if ((level.equals(Level.DEBUG))) {
			return;
		}
		Bukkit.getConsoleSender()
				.sendMessage((color ? level.getColor() : "") + "[" + prefix + "] " + level.getPrefix() + message);
	}
}