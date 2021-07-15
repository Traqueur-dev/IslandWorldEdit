package fr.traqueur.smeltblock.worldedit.api.jsons.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import fr.traqueur.smeltblock.worldedit.IslandWorldEdit;

public abstract class GsonAdapter<T> extends TypeAdapter<T> {

	private IslandWorldEdit plugin;

	public GsonAdapter(IslandWorldEdit plugin) {
		this.plugin = plugin;
	}

	public Gson getGson() {
		return this.plugin.getGson();
	}
}