package fr.traqueur.smeltblock.worldedit.managers.worldedit.clazz;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.traqueur.smeltblock.worldedit.api.utils.ItemBuilder;

public class Wand {

	private String item;
	private String name;
	private List<String> lore;

	public Wand() {}
	
	public Wand(Material item, String name, List<String> lore) {
		this.item = item.name();
		this.name = name;
		this.lore = lore;
	}

	public ItemStack getWand(Player player, int use) {
		ItemStack item =  new ItemBuilder(Material.valueOf(this.item)).addFlags(ItemFlag.HIDE_ENCHANTS).enchant(Enchantment.DURABILITY, 1).displayname(name).lore(lore).build();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = Lists.newArrayList();
		for(String s: meta.getLore()) {
			lore.add(s.replace("%pseudo%", player.getName()).replace("%utilisations%", use < 0 ? "Illimitées" : use + ""));
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		NBTItem nbtItem = new NBTItem(item);
		nbtItem.setString("ownerName", player.getName());
		nbtItem.setInteger("uses", use);
		nbtItem.setBoolean("set-wand", true);
		
		return nbtItem.getItem();
	}
	
	public String getItem() {
		return item;
	}

	public void setItem(Material item) {
		this.item = item.name();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}
	
	
}
