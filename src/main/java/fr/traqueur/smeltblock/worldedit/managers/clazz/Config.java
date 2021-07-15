package fr.traqueur.smeltblock.worldedit.managers.clazz;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Config {

	private String prefix;
	private String separateUses;
	private String wordUses;
	private double defaultPrice;
	private HashMap<String, Double> priceBlocks;
	private List<String> ignoredBlocks;
	private double pourcentSetCommand;
	private double pourcentWallsCommand;
	private double pourcentCutCommand;
	private double pourcentFillCommand;
	private double pourcentReplaceCommand;
	private double pourcentCylCommand;

	public Config() {
		this.prefix = "§7(§9PlayerWorldEdit§7)";
		this.separateUses = ":";
		this.wordUses = "Utilisations";
		priceBlocks = Maps.newHashMap();
		ignoredBlocks = Lists.newArrayList();
		pourcentSetCommand = 0;
		pourcentWallsCommand = 0;
		pourcentCutCommand = 0;
		pourcentFillCommand = 0;
		pourcentReplaceCommand = 0;
		pourcentCylCommand = 0;
		defaultPrice = 10;
		
		priceBlocks.put(Material.DIAMOND_BLOCK.name(), 20d);
		
		ignoredBlocks.add(Material.HOPPER.name());
		ignoredBlocks.add(Material.END_PORTAL.name());
		ignoredBlocks.add(Material.END_PORTAL_FRAME.name());
		ignoredBlocks.add(Material.CHEST.name());
		ignoredBlocks.add(Material.ENDER_CHEST.name());
		ignoredBlocks.add(Material.TRAPPED_CHEST.name());
	}

	public List<String> getIgnoredBlocks() {
		return ignoredBlocks;
	}

	public void setIgnoredBlocks(List<String> ignoredBlocks) {
		this.ignoredBlocks = ignoredBlocks;
	}

	public double getPourcentSetCommand() {
		return pourcentSetCommand;
	}

	public void setPourcentSetCommand(double pourcentSetCommand) {
		this.pourcentSetCommand = pourcentSetCommand;
	}

	public double getPourcentWallsCommand() {
		return pourcentWallsCommand;
	}

	public void setPourcentWallsCommand(double pourcentWallsCommand) {
		this.pourcentWallsCommand = pourcentWallsCommand;
	}

	public double getPourcentCutCommand() {
		return pourcentCutCommand;
	}

	public void setPourcentCutCommand(double pourcentCutCommand) {
		this.pourcentCutCommand = pourcentCutCommand;
	}

	public double getPourcentFillCommand() {
		return pourcentFillCommand;
	}

	public void setPourcentFillCommand(double pourcentFillCommand) {
		this.pourcentFillCommand = pourcentFillCommand;
	}

	public double getPourcentReplaceCommand() {
		return pourcentReplaceCommand;
	}

	public void setPourcentReplaceCommand(double pourcentReplaceCommand) {
		this.pourcentReplaceCommand = pourcentReplaceCommand;
	}

	public double getPourcentCylCommand() {
		return pourcentCylCommand;
	}

	public void setPourcentCylCommand(double pourcentCylCommand) {
		this.pourcentCylCommand = pourcentCylCommand;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public HashMap<String, Double> getPriceBlocks() {
		return priceBlocks;
	}

	public void setPriceBlocks(HashMap<String, Double> priceBlocks) {
		this.priceBlocks = priceBlocks;
	}

	public double getDefaultPrice() {
		return defaultPrice;
	}

	public void setDefaultPrice(double defaultPrice) {
		this.defaultPrice = defaultPrice;
	}

	public void addBlock(Material mat, double d) {
		priceBlocks.put(mat.name(), d);
	}

	public String getSeparateUses() {
		return separateUses;
	}

	public void setSeparateUses(String separateUses) {
		this.separateUses = separateUses;
	}

	public String getWordUses() {
		return wordUses;
	}

	public void setWordUses(String wordUses) {
		this.wordUses = wordUses;
	}

}
