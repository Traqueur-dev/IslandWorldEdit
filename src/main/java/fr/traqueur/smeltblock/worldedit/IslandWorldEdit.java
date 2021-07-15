package fr.traqueur.smeltblock.worldedit;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.traqueur.smeltblock.worldedit.api.MultiThreading;
import fr.traqueur.smeltblock.worldedit.api.jsons.JsonPersist;
import fr.traqueur.smeltblock.worldedit.api.jsons.adapters.LocationAdapter;
import fr.traqueur.smeltblock.worldedit.api.utils.Logger;
import fr.traqueur.smeltblock.worldedit.commands.*;
import fr.traqueur.smeltblock.worldedit.commands.admin.GiveWandCommand;
import fr.traqueur.smeltblock.worldedit.commands.gestion.CancelCommand;
import fr.traqueur.smeltblock.worldedit.commands.gestion.CountCommand;
import fr.traqueur.smeltblock.worldedit.commands.gestion.UndoCommand;
import fr.traqueur.smeltblock.worldedit.listeners.InteractListener;
import fr.traqueur.smeltblock.worldedit.listeners.JobsListener;
import fr.traqueur.smeltblock.worldedit.listeners.PlayerListener;
import fr.traqueur.smeltblock.worldedit.managers.WorldEditManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IslandWorldEdit extends JavaPlugin {

	private static IslandWorldEdit instance;

	private Gson gson;
	private Economy economy;
	private List<JsonPersist> persists;

	public IslandWorldEdit() {
		instance = this;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		this.getDataFolder().mkdir();
		this.gson = this.createGsonBuilder().create();
		this.persists = Lists.newArrayList();

		this.persists.add(new WorldEditManager());

		if(!this.setupEconomy()) {
			Bukkit.shutdown();
			Logger.error("No Economy Plugin detected...");
		}

		this.loadPersists();
		this.loadCommands();
		this.loadListeners();

		MultiThreading.schedule(this::save, 0, 30, TimeUnit.HOURS);
	}

	private void loadListeners() {
		PluginManager pluginManager = Bukkit.getPluginManager();
		
		pluginManager.registerEvents(new PlayerListener(), this);
		pluginManager.registerEvents(new InteractListener(), this);
		pluginManager.registerEvents(new JobsListener(), this);
		
	}

	private void loadCommands() {
		this.getCommand("givewand").setExecutor(new GiveWandCommand());
		this.getCommand("cancel").setExecutor(new CancelCommand());
		this.getCommand("count").setExecutor(new CountCommand());
		this.getCommand("undo").setExecutor(new UndoCommand());
		this.getCommand("cut").setExecutor(new CutCommand());
		this.getCommand("cyl").setExecutor(new CylCommand());
		this.getCommand("fill").setExecutor(new FillCommand());
		this.getCommand("replace").setExecutor(new ReplaceCommand());
		this.getCommand("set").setExecutor(new SetCommand());
		this.getCommand("sphere").setExecutor(new SphereCommand());
		this.getCommand("walls").setExecutor(new WallsCommand());
	}

	@Override
	public void onDisable() {
		super.onDisable();
		MultiThreading.POOL.shutdown();
		MultiThreading.RUNNABLE_POOL.shutdown();
	}

	private GsonBuilder createGsonBuilder() {
		GsonBuilder ret = new GsonBuilder();

		ret.setPrettyPrinting();
		ret.disableHtmlEscaping();
		ret.excludeFieldsWithModifiers(Modifier.TRANSIENT);

		ret.registerTypeAdapter(Location.class, new LocationAdapter(this)).create();

		return ret;
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (economyProvider != null)
			this.economy = economyProvider.getProvider();
		return (this.economy != null);
	}

	public void loadPersists() {
		for (JsonPersist persist : this.persists) {
			persist.loadData();
		}
	}

	public void savePersists() {
		for (JsonPersist persist : this.persists) {
			persist.saveData();
		}
	}

	public void save() {
		long time = System.currentTimeMillis();
		this.savePersists();
		time = System.currentTimeMillis() - time;
		Logger.success("Sauvegarde des données (" + time + "ms)");
	}

	public Gson getGson() {
		return this.gson;
	}

	public static IslandWorldEdit getInstance() {
		return instance;
	}

	public Economy getEconomy() {
		return economy;
	}

}
