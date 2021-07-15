package fr.traqueur.smeltblock.worldedit.api.utils;

import fr.traqueur.smeltblock.worldedit.IslandWorldEdit;

public class EconomyUtils {

	public static double getBalance(String name) {
		return IslandWorldEdit.getInstance().getEconomy().getBalance(name);
	}

	public static void setBalance(String name, double value) {
		IslandWorldEdit.getInstance().getEconomy().withdrawPlayer(name,
				IslandWorldEdit.getInstance().getEconomy().getBalance(name));
		IslandWorldEdit.getInstance().getEconomy().depositPlayer(name, value);
	}

	public static void deposit(String name, double value) {
		IslandWorldEdit.getInstance().getEconomy().depositPlayer(name, value);
	}

	public static void withdraw(String name, double value) {
		IslandWorldEdit.getInstance().getEconomy().withdrawPlayer(name, value);
	}

	public static boolean has(String name, double value) {
		return IslandWorldEdit.getInstance().getEconomy().has(name, value);
	}
}