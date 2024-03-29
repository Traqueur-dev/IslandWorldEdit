package fr.traqueur.smeltblock.worldedit.gui.clazz;

import fr.traqueur.smeltblock.worldedit.api.inventory.ClickableItem;
import fr.traqueur.smeltblock.worldedit.api.utils.ItemBuilder;
import fr.traqueur.smeltblock.worldedit.api.utils.Utils;
import fr.traqueur.smeltblock.worldedit.gui.WorldEditProvider;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import fr.traqueur.smeltblock.worldedit.managers.profiles.clazz.Profile;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.WorldEditManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class GUItem {

    private String profile;
    private String material;
    private int amount;

    public GUItem(Profile profile, Material material, int amount) {
        this.material = material.name();
        this.amount = amount;
        this.profile = profile.getName();
    }

    public GUItem(Profile profile, Material material) {
        this(profile, material, 0);
    }

    public int retrieve(Player player, int quantity) {
        if (amount <= 0) {
            ProfileManager.getSingleton().getProfile(player).removeItem(this);
            return 0;
        }

        int available = (amount - quantity >= 0) ? quantity : amount;
        player.getInventory().addItem(new ItemStack(getMaterial(), available));
        amount -= available;

        if (amount <= 0) {
            ProfileManager.getSingleton().getProfile(player).removeItem(this);
            return available;
        }

        return available;
    }

    public boolean hasAvailableSlot(Inventory inv, int amount) {
        if (inv.firstEmpty() == -1) {
            for (ItemStack i : inv.getContents()) {
                if (i != null && i.getType() ==  getMaterial()) {
                    int itemAmount = i.getAmount();
                    if ((itemAmount + amount) > 64) {
                        continue;
                    }
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    public void add(int quantity) {
        amount = amount + quantity;
    }

    public void remove(int quantity) {
        amount -= Math.min(amount, quantity);
        if (amount <= 0) {
            ProfileManager.getSingleton().getProfile(profile).removeItem(this);
        }
    }

    public ClickableItem toClickableItem(int page) {
        List<String> lore = WorldEditManager.getSingleton().getConfig().getLoreItems();
        lore = lore.stream().map(s -> s.replaceAll("%amount%", String.valueOf(amount))).collect(Collectors.toList());
        ItemStack item = new ItemBuilder(getMaterial()).amount(1).lore(lore).build();

        return ClickableItem.of(item, event -> {
            Player player = (Player) event.getWhoClicked();
            int quantity = 0;

            if (event.getClick() == ClickType.RIGHT) {
                if (this.hasAvailableSlot(player.getInventory(), 64)) {
                    quantity = this.retrieve(player, 64);
                } else {
                    player.sendMessage("§c✘ Vous n'avez pas assez de place dans l'inventaire pour retirer §6x" + quantity + " " + material + "§c ✘");
                    return;
                }
            } else if (event.getClick() == ClickType.MIDDLE) {
                for (int i = 0; i < player.getInventory().getContents().length; i++) {
                    if (this.hasAvailableSlot(player.getInventory(), 64)) {
                        quantity = quantity + this.retrieve(player, 64);
                    }
                }
                if (quantity == 0) {
                    player.sendMessage("§c✘ Vous n'avez pas assez de place dans l'inventaire pour retirer des " + material + "§c ✘");
                }
            } else {
                if (this.hasAvailableSlot(player.getInventory(), 1)) {
                    quantity = this.retrieve(player, 1);
                } else {
                    player.sendMessage("§c✘ Vous n'avez pas assez de place dans l'inventaire pour retirer §6x" + quantity + " " + material + "§c ✘");
                    return;
                }
            }
            Utils.sendMessage(player, "§7Vous avez retiré §b" + quantity + " " + material);
            WorldEditProvider.getInventory(player, ProfileManager.getSingleton().getProfile(profile)).open(player, page);
        });
    }

    public int getAmount() {
        return amount;
    }

    public Material getMaterial() {
        return Material.getMaterial(material);
    }
}
