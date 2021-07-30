package fr.traqueur.smeltblock.worldedit.listeners;

import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPaymentEvent;
import de.tr7zw.changeme.nbtapi.NBTBlock;
import me.mraxetv.beasttokens.api.events.tokendrops.blocks.BTMiningDropEvent;
import me.mraxetv.beasttokens.api.events.tokendrops.blocks.BTPreMiningDropEvent;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
	public void onGainFarmPoint(BTMiningDropEvent event) {
		handle(event);
	}

	@EventHandler
	public void onGainFarmPoint(BTPreMiningDropEvent event) {
		handle(event);
	}

	private void handle(Event event) {
		Block b = null;
		if(event instanceof BTMiningDropEvent) {
			b = ((BTMiningDropEvent) event).getBlock();
		} else if(event instanceof BTPreMiningDropEvent) {
			b = ((BTPreMiningDropEvent) event).getBlock();
		}

		if (b != null) {
			NBTBlock block = new NBTBlock(b);
			if (!block.getData().hasKey("worldEdited"))
				return;
			if (block.getData().getBoolean("worldEdited")) {
				if(event instanceof BTMiningDropEvent) {
					((BTMiningDropEvent) event).setCancelled(true);
				} else if(event instanceof BTPreMiningDropEvent) {
					((BTPreMiningDropEvent) event).setCancelled(true);
				}
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
