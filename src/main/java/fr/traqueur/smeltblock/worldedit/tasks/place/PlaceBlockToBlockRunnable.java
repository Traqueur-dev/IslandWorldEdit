package fr.traqueur.smeltblock.worldedit.tasks.place;

import fr.traqueur.smeltblock.worldedit.api.utils.EconomyUtils;
import fr.traqueur.smeltblock.worldedit.api.utils.InventoryUtils;
import fr.traqueur.smeltblock.worldedit.gui.clazz.GUItem;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.clazz.TypeCommand;
import fr.traqueur.smeltblock.worldedit.tasks.AbstractPlaceBlockRunnable;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public class PlaceBlockToBlockRunnable extends AbstractPlaceBlockRunnable {

    private WorldEditManager manager;
    private double price;
    private boolean payed;
    private Block b;

    public PlaceBlockToBlockRunnable(Player player, LinkedList<Block> blocks, Material item, TypeCommand command, boolean replace) {
        super(player, blocks, item, replace);
        this.manager = WorldEditManager.getSingleton();
        payed = false;
        price = manager.getPrice(item, getQuantity(), command);
    }


    @Override
    public void run() {
        if (getQuantity() >= 50000) {
            player.sendMessage(manager.getConfig().getPrefix() + " §cLa zone sélectionée est trop grande.");
            manager.getInWE().remove(player.getUniqueId());
            this.cancel();
            return;
        }

        if (this.getQuantity() == 0) {
            player.sendMessage(manager.getConfig().getPrefix() + " §cIl n'y a aucun bloc à changer dans votre selection.");
            manager.getInWE().remove(player.getUniqueId());
            this.cancel();
            return;
        }

        if (!payed) {
            if (!EconomyUtils.has(player.getName(), price)) {
                player.sendMessage(manager.getConfig().getPrefix() + " §cVous n'avez pas assez d'argent.");
                manager.getInWE().remove(player.getUniqueId());
                this.cancel();
                return;
            }

            int globalQuantity = getQuantity();
            int quantityInInventory = InventoryUtils.getItemCount(player, new ItemStack(item));
            int toRemoveInInv = Math.min(globalQuantity, quantityInInventory);
            globalQuantity -= toRemoveInInv;
            InventoryUtils.decrementItem(player, new ItemStack(item), toRemoveInInv);
            if(globalQuantity > 0) {
                GUItem guItem = ProfileManager.getSingleton().getProfile(player).get(item);
                if(player.hasPermission("we.gui.use") && guItem != null) {
                    guItem.remove(globalQuantity);
                }
            }
            EconomyUtils.withdraw(player.getName(), price);
            payed = true;
        }

        if (this.isCancel()) {
            this.cancel();
            this.giveBlocks();
            int placed = this.getQuantity() - this.getBlocks().size();
            if (player.hasPermission("we.gui.use")) {
                ProfileManager.getSingleton().getProfile(player).addItem(new ItemStack(item), this.getBlocks().size());
            } else {
                InventoryUtils.addItem(player, new ItemStack(item), this.getBlocks().size());
            }

            player.sendMessage(manager.getConfig().getPrefix() + " §bVous §7avez placé §9x" + (placed) + " " + item + "§7 pour §9"
                    + price + "⛁ §7.");
            manager.getInWE().remove(player.getUniqueId());
            return;
        }

        b = this.getBlocks().getFirst();
        this.saveBlock(b);

        manager.setBlockInNativeWorld(player, b.getLocation(), this.getItem().createBlockData(), false);
        this.getBlocks().removeFirst();

        if (blocks.size() == 0) {
            this.giveBlocks();
            player.sendMessage(manager.getConfig().getPrefix() + " §bVous §7avez placé §9x" + (this.getQuantity()) + " " + item + "§7 pour §9"
                    + price + "⛁ §7.");
            manager.getInWE().remove(player.getUniqueId());
            this.cancel();
            return;
        }
    }
}
