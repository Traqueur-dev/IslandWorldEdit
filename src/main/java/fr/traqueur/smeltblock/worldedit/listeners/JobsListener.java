package fr.traqueur.smeltblock.worldedit.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.tr7zw.changeme.nbtapi.NBTBlock;
import me.mraxetv.beasttokens.api.events.tokendrops.blocks.BTBlockTokenDropEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPaymentEvent;

public class JobsListener implements Listener {

	private List<UUID> playerWorldEdited;

	public JobsListener() {
		playerWorldEdited = new ArrayList<>();
	}

	@EventHandler
	public void onGainExp(JobsExpGainEvent event) {
		Block b = event.getBlock();
		if (b != null) {
			NBTBlock block = new NBTBlock(b);
			if (!block.getData().hasKey("worldEdited"))
				return;
			if (block.getData().getBoolean("worldEdited")) {
				playerWorldEdited.add(event.getPlayer().getUniqueId());
				event.setCancelled(true);
			}

		}
	}

	@EventHandler
	public void onGainFarmPoint(BTBlockTokenDropEvent event) {
		Block b = event.getBlock();
		if (b != null) {
			NBTBlock block = new NBTBlock(b);
			if (!block.getData().hasKey("worldEdited"))
				return;
			if (block.getData().getBoolean("worldEdited")) {
				event.setCancelled(true);
			}

		}
	}

	@EventHandler
	public void onPayement(JobsPaymentEvent event) {
		if (playerWorldEdited.contains(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
			playerWorldEdited.remove(event.getPlayer().getUniqueId());
		}
	}

}
