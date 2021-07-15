package fr.traqueur.smeltblock.worldedit.api.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtils {

	public static boolean isNullItem(ItemStack item) {
		return item == null || item.getType() == Material.AIR;
	}

	public static void addItem(Player player, ItemStack item) {
		addItem(player, item, 1);
	}

	public static void addItem(Player player, ItemStack item, int quantity) {
		PlayerInventory inventory = player.getInventory();
		ItemStack current = new ItemStack(item);
		
		if(current.getType() == Material.AIR) return;
		
		if(isFullInventory(player)) {
			int max = current.getMaxStackSize();

			while(quantity > max) {
				current.setAmount(max);
				player.getWorld().dropItem(player.getLocation(), current);
				quantity -= max;
			}

			current.setAmount(quantity);
			player.getWorld().dropItem(player.getLocation(), current);
			return;
		}
		
		int max = current.getMaxStackSize();
		if (quantity > max) {
			int add;
			for (int leftOver = quantity; leftOver > 0; leftOver -= add) {
				add = 0;
				add = leftOver <= max ? (add += leftOver) : (add += max);
				current = current.clone();
				current.setAmount(add);
				inventory.addItem(current);
			}
		} else {
			current = current.clone();
			current.setAmount(quantity);
			inventory.addItem(current);
		}
	}

	public static boolean haveRequiredItem(Player player, ItemStack item) {
		return haveRequiredItem(player, item, 1);
	}

	public static boolean haveRequiredItem(Player player, ItemStack item, int quantity) {
		int quantityInInventory = getItemCount(player, item);
		return quantityInInventory >= quantity;
	}

	public static boolean isFullInventory(Player player) {
		return player.getInventory().firstEmpty() == -1;
	}

	public static boolean hasSpaceInventory(Player player, ItemStack item, int count) {
		int left = count;
		ItemStack[] items = player.getInventory().getContents();
		for (ItemStack stack : items) {
			if (stack == null || stack.getType() == Material.AIR) {
				left -= item.getMaxStackSize();
			} else if (stack.getType() == item.getType() && stack.getData().getData() == item.getData().getData()
					&& item.getMaxStackSize() > 1 && stack.getAmount() < item.getMaxStackSize()) {
				left -= stack.getMaxStackSize() - stack.getAmount();
			}
			if (left <= 0)
				break;
		}
		return left <= 0;
	}

	public static int getItemCount(Player player, ItemStack item) {
		int quantityInInventory = 0;
		PlayerInventory inventory = player.getInventory();
		for (ItemStack current : inventory.getContents()) {
			if (isNullItem(current) || current.getType() != item.getType())
				continue;
			quantityInInventory += current.getAmount();
		}
		return quantityInInventory;
	}

	public static void decrementItem(Player player, ItemStack item, int quantity) {
		int toRemove = quantity;
		PlayerInventory inv = player.getInventory();
		ItemStack itemInHand  = inv.getItemInOffHand();
		if(itemInHand.isSimilar(item)) {
		
			if(quantity > itemInHand.getAmount()) {
				quantity -= itemInHand.getAmount();
				inv.setItemInOffHand(null);
			} else {
				itemInHand.setAmount(itemInHand.getAmount() - quantity);
				return;
			}
		}
		for (ItemStack is : inv.getContents()) {
			if (toRemove <= 0)
				break;
			if (is == null || is.getType() != item.getType()) {
				
				continue;
			}
			int amount = is.getAmount() - toRemove;
			toRemove -= is.getAmount();
			
			if (amount <= 0) {
				player.getInventory().removeItem(is);
				continue;
			}
			is.setAmount(amount);
		}
	}
}