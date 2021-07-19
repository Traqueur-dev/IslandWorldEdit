package fr.traqueur.smeltblock.worldedit.tasks.undo;

import fr.traqueur.smeltblock.worldedit.api.utils.EconomyUtils;
import fr.traqueur.smeltblock.worldedit.api.utils.InventoryUtils;
import fr.traqueur.smeltblock.worldedit.api.utils.Utils;
import fr.traqueur.smeltblock.worldedit.gui.clazz.GUItem;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import fr.traqueur.smeltblock.worldedit.managers.profiles.clazz.BlockLocation;
import fr.traqueur.smeltblock.worldedit.managers.profiles.clazz.Profile;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.clazz.TypeCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;

public class UndoRunnable extends BukkitRunnable {

    private Player player;
    private Profile profile;
    private LinkedList<BlockLocation> undo;
    private double price;
    private boolean payed;

    public UndoRunnable(Player player, Profile profile, TypeCommand command) {
        this.player = player;
        this.profile = profile;
        this.undo = profile.getUndo();
        this.price = WorldEditManager.getSingleton().getPrice(Material.AIR, undo.size(), command);
        payed = false;
    }

    @Override
    public void run() {
        if (!payed) {
            if (!EconomyUtils.has(player.getName(), price)) {
                Utils.sendMessage(player, "&cVous n'avez pas assez d'argent.");
                this.cancel();
                return;
            }
            EconomyUtils.withdraw(player.getName(), price);
            payed = true;
        }

        if (undo.size() == 0) {
            Utils.sendMessage(player, "&bVous &7avez &9revoqué &7votre dernière action pour §9"
                    + price + "⛁ §&7.");
            this.cancel();
            return;
        }

        BlockLocation b = undo.getFirst();
        b.undo(profile);
        undo.removeFirst();

        if (undo.size() == 0) {
            Utils.sendMessage(player, "&bVous &7avez &9revoqué &7votre dernière action pour §9"
                    + price + "⛁ &7.");
            this.cancel();
        }
    }
}
