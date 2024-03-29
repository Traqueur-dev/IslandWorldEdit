package fr.traqueur.smeltblock.worldedit.managers.worldedit;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.guillaumevdn.questcreator.data.BoardLocalQC;
import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.traqueur.smeltblock.worldedit.IslandWorldEdit;
import fr.traqueur.smeltblock.worldedit.api.jsons.DiscUtil;
import fr.traqueur.smeltblock.worldedit.api.jsons.JsonPersist;
import fr.traqueur.smeltblock.worldedit.api.utils.Cuboid;
import fr.traqueur.smeltblock.worldedit.api.utils.InventoryUtils;
import fr.traqueur.smeltblock.worldedit.api.utils.Utils;
import fr.traqueur.smeltblock.worldedit.exceptions.TooManyBlocksException;
import fr.traqueur.smeltblock.worldedit.gui.clazz.GUItem;
import fr.traqueur.smeltblock.worldedit.managers.profiles.ProfileManager;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.clazz.Config;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.clazz.TypeCommand;
import fr.traqueur.smeltblock.worldedit.managers.worldedit.clazz.Wand;
import fr.traqueur.smeltblock.worldedit.tasks.BlockRunnable;
import fr.traqueur.smeltblock.worldedit.tasks.VisualizeRunnable;
import fr.traqueur.smeltblock.worldedit.tasks.destroy.DestroyBlockToBlockRunnable;
import fr.traqueur.smeltblock.worldedit.tasks.place.PlaceBlockToBlockRunnable;
import fr.traqueur.smeltblock.worldedit.tasks.replace.ReplaceBlockToBlockRunnable;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.IBlockData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

public class WorldEditManager implements JsonPersist {

    private static WorldEditManager singleton;

    private Set<Player> tempVizualize;
    private HashMap<UUID, Location[]> corners;
    private HashMap<UUID, BlockRunnable> inWE;
    private Config config;
    private Wand wand;

    public WorldEditManager() {
        singleton = this;
        this.tempVizualize = new HashSet<>();
        this.inWE = Maps.newHashMap();
        this.corners = Maps.newHashMap();
        this.config = new Config();
        this.wand = new Wand(Material.BLAZE_ROD, "Baton Magique", Arrays.asList("Lore 1", "Lore 2"));
    }

    public static WorldEditManager getSingleton() {
        return singleton;
    }

    public void setBlockInNativeWorld(Player player, Location loc, BlockData blockData, boolean applyPhysics) {
        //loc.getBlock().setMetadata("worldEdited", new FixedMetadataValue(IslandWorldEdit.getInstance(), true));
        NBTBlock block = new NBTBlock(loc.getBlock());
        block.getData().setBoolean("worldEdited", true);
        net.minecraft.server.v1_16_R3.World nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        IBlockData data = ((CraftBlockData) blockData).getState();
        nmsWorld.setTypeAndData(new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), data, applyPhysics ? 3 : 2);

        BoardLocalQC.inst().setPlacedByPlayer(loc.getBlock());
        player.spawnParticle(config.getParticle(), loc, 3, 0.5,0.5,0.5);

    }

    public int getAmount(Player p, Material m) {
        int count = 0;
        for (ItemStack i : p.getInventory().getContents()) {
            if (i != null && i.getType().equals(m)) {
                count += i.getAmount();
            }
        }
        if (p.hasPermission("we.gui.use")) {
            GUItem g = ProfileManager.getSingleton().getProfile(p).get(m);
            if (g != null) {
                count += g.getAmount();
            }
        }

        return count;
    }

    public void setBlock(Player player, LinkedList<Block> cuboid, int size, Material item,
                         boolean replace, TypeCommand command) {
        BlockRunnable runnable = new PlaceBlockToBlockRunnable(player, cuboid, size, item, command, replace);
        if(!runnable.isCancel()) {
            runnable.runTaskTimer(IslandWorldEdit.getInstance(), 0, 1);
            inWE.put(player.getUniqueId(), runnable);
            this.decrementWand(player);
        }
    }

    public void replaceBlocks(Player player, LinkedList<Block> cuboid,int size, Material item,
                              Material newItem, TypeCommand command) {
        BlockRunnable runnable = new ReplaceBlockToBlockRunnable(player, cuboid,size, item, command, newItem);
        if(!runnable.isCancel()) {
            runnable.runTaskTimer(IslandWorldEdit.getInstance(), 0, 1);
            inWE.put(player.getUniqueId(), runnable);
            this.decrementWand(player);
        }
    }

    public void destroyBlocks(Player player, LinkedList<Block> cuboid, int size, Material item) {
        BlockRunnable runnable = new DestroyBlockToBlockRunnable(player, cuboid, size, item, TypeCommand.CUT);
        if(!runnable.isCancel()) {
            runnable.runTaskTimer(IslandWorldEdit.getInstance(), 0, 1);
            inWE.put(player.getUniqueId(), runnable);
            this.decrementWand(player);
        }
    }

    public LinkedList<Block> getWalls(Player p) throws TooManyBlocksException {
        Cuboid cube = this.getCuboid(p);
        LinkedList<Block> list = new LinkedList<>();
        int minX = cube.getLowerX();
        int maxX = cube.getUpperX();
        int minY = cube.getLowerY();
        int maxY = cube.getUpperY();
        int minZ = cube.getLowerZ();
        int maxZ = cube.getUpperZ();
        World w = cube.getWorld();
        for (int i = minX; i <= maxX; i++) {
            for (int y = minY; y <= maxY; y++)
                list.add(new Location(w, i, y, minZ).getBlock());
        }
        int z;
        for (z = minZ; z <= maxZ; z++) {
            for (int y = minY; y <= maxY; y++)
                list.add(new Location(w, minX, y, z).getBlock());
        }
        for (z = minZ; z <= maxZ; z++) {
            for (int y = minY; y <= maxY; y++)
                list.add(new Location(w, maxX, y, z).getBlock());
        }
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++)
                list.add(new Location(w, x, y, maxZ).getBlock());
        }

        if(list.size() >= getConfig().getQuantityLimit() && getConfig().getQuantityLimit() != -1) {
            p.sendMessage(getConfig().getPrefix() + " §cLa zone sélectionée est trop grande.");
            getInWE().remove(p.getUniqueId());
            throw new TooManyBlocksException();
        }

        return list;
    }

    public LinkedList<Block> getCyl(Player p, int height, boolean filled) throws TooManyBlocksException {
        LinkedList<Block> list = new LinkedList<>();
        Cuboid cube = this.getCuboid(p);
        double radius = Math.max(cube.getUpperX() - cube.getLowerX(), cube.getUpperZ() - cube.getLowerZ()) / 2 + 0.5;
        int ceilRadius = (int) Math.ceil(radius);

        for (int y = 0; y < height; y++) {
            for (int x = -ceilRadius; x <= ceilRadius; x++) {
                for (int z = -ceilRadius; z <= ceilRadius; z++) {
                    if (x * x + z * z < radius * radius) {
                        if (!filled) {
                            if (lengthSq(x, z) <= (radius * radius) - (radius * 2)) {
                                continue;
                            }
                        }
                        list.add(cube.getCenter().getBlock().getRelative(x, y, z));
                    }

                }
            }
        }

        if(list.size() >= getConfig().getQuantityLimit() && getConfig().getQuantityLimit() != -1) {
            p.sendMessage(getConfig().getPrefix() + " §cLa zone sélectionée est trop grande.");
            getInWE().remove(p.getUniqueId());
            throw new TooManyBlocksException();
        }

        return list;
    }

    public LinkedList<Block> getSphere(Player p, boolean filled) throws TooManyBlocksException {
        LinkedList<Block> list = new LinkedList<>();
        Cuboid cube = this.getCuboid(p);
        double radius = Math.max(cube.getUpperX() - cube.getLowerX(), cube.getUpperZ() - cube.getLowerZ()) / 2 + 0.5;
        int ceilRadius = (int) Math.ceil(radius);
        for (int y = -ceilRadius; y <= ceilRadius; y++) {
            for (int x = -ceilRadius; x <= ceilRadius; x++) {
                for (int z = -ceilRadius; z <= ceilRadius; z++) {
                    if (x * x + z * z + y * y < radius * radius) {
                        if (!filled) {
                            if (lengthSq(x, y, z) <= (radius * radius) - (radius * 2)) {
                                continue;
                            }
                        }
                        list.add(cube.getCenter().getBlock().getRelative(x, y, z));
                    }

                }
            }
        }

        if(list.size() >= getConfig().getQuantityLimit() && getConfig().getQuantityLimit() != -1) {
            p.sendMessage(getConfig().getPrefix() + " §cLa zone sélectionée est trop grande.");
            getInWE().remove(p.getUniqueId());
            throw new TooManyBlocksException();
        }

        return list;
    }

    private double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
    }

    private double lengthSq(double x, double z) {
        return (x * x) + (z * z);
    }

    public double getPrice(Material m, int quantity, TypeCommand command) {
        double price = m != null && config.getPriceBlocks().containsKey(m.name()) ? config.getPriceBlocks().get(m.name())
                : config.getDefaultPrice();
        double pourcent;
        switch (command) {
            case CUT:
                pourcent = config.getPourcentCutCommand();
                price = price + (price * pourcent);
                break;
            case FILL:
                pourcent = config.getPourcentFillCommand();
                price = price + (price * pourcent);
                break;
            case SET:
                pourcent = config.getPourcentSetCommand();
                price = price + (price * pourcent);
                break;
            case CYL:
                pourcent = config.getPourcentCylCommand();
                price = price + (price * pourcent);
                break;
            case SPHERE:
                pourcent = config.getPourcentSphereCommand();
                price = price + (price * pourcent);
                break;
            case REPLACE:
                pourcent = config.getPourcentReplaceCommand();
                price = price + (price * pourcent);
                break;
            case WALLS:
                pourcent = config.getPourcentWallsCommand();
                price = price + (price * pourcent);
                break;
            case UNDO:
                pourcent = config.getPourcentUndoCommand();
                price = config.getPriceUndo() + (config.getPriceUndo() * pourcent);
            default:
                break;
        }
        return price * quantity;
    }

    public List<String> getAllowedBlocks() {
        List<String> list = Lists.newArrayList();
        for (Material mat : Material.values()) {
            if (mat.isBlock() && !config.getIgnoredBlocks().contains(mat.name()))
                list.add(mat.name());
        }
        return list;
    }

    public boolean goodCommand(CommandSender sender, String perm) {
        if (!(sender instanceof Player)) {
            Utils.sendMessage(sender, "&cLa commande ne peut-être exécuté qu'en jeu.");
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(perm)) {
            Utils.sendMessage(player, "&cVous n'avez pas la permission de faire cela !");
            return false;
        }

        Cuboid cuboid = this.getCuboid(player);

        if (this.isOnWE(player)) {
            Utils.sendMessage(player, "&cVous êtes déjà en train de faire une commande.");
            return false;
        }

        if (cuboid == null) {
            Utils.sendMessage(player, "&cVos positions ne sont pas définies.");
            return false;
        }

        if (!this.positionsIsSameWorld(player)) {
            Utils.sendMessage(player, "&cVos positions ne sont pas dans le même monde.");
            return false;
        }

        int count = this.haveWand(player);
        if (count == 0) {
            Utils.sendMessage(player, "&cVous n'avez pas de bâton magique dans votre inventaire.");
            return false;
        }

        if (count > 1) {
            Utils.sendMessage(player, "&cVous avez trop  de bâtons magiques dans votre inventaire.");
            return false;
        }

        return true;
    }

    public boolean isOnWE(Player player) {
        return inWE.containsKey(player.getUniqueId());
    }

    public Cuboid getCuboid(Player player) {
        Location loc1 = corners.get(player.getUniqueId())[0];
        Location loc2 = corners.get(player.getUniqueId())[1];
        if (loc1 == null || loc2 == null)
            return null;
        return new Cuboid(loc1, loc2);
    }

    public boolean positionsIsSameWorld(Player profile) {
        Island island = SuperiorSkyblockAPI.getPlayer(profile.getName()).getIsland();
        Location loc1 = corners.get(profile.getUniqueId())[0];
        Location loc2 = corners.get(profile.getUniqueId())[1];
        if (island == null || loc1 == null || loc2 == null)
            return false;
        return loc1.getWorld().equals(loc2.getWorld()) && island.isInsideRange(loc1) && island.isInsideRange(loc2);
    }

    public void setupCorners(Player player) {
        corners.put(player.getUniqueId(), new Location[]{null, null});
    }

    public void eraseCorners(Player player) {
        corners.remove(player.getUniqueId());
    }

    public void applyWand(Player player, int use) {
        if (InventoryUtils.isFullInventory(player)) {
            player.getWorld().dropItem(player.getLocation(), wand.getWand(player, use));
        } else {
            InventoryUtils.addItem(player, wand.getWand(player, use));
        }
    }

    private void decrementWand(Player p) {
        Inventory inv = p.getInventory();
        for (int count = 0; count < inv.getSize(); count++) {
            ItemStack i = inv.getItem(count);
            if (isWand(i, p)) {
                NBTItem compound = new NBTItem(i);
                int uses = compound.getInteger("uses");
                if (uses < 0) {
                    return;
                }
                uses -= 1;
                compound.setInteger("uses", uses);
                i = compound.getItem();

                ItemMeta meta = i.getItemMeta();
                List<String> lores = Lists.newArrayList();

                for (String s : meta.getLore()) {
                    if (s.contains(config.getWordUses())) {
                        String[] tab = s.split(config.getSeparateUses());
                        String color = ChatColor.getLastColors(tab[1]);
                        int use = Integer.parseInt(ChatColor.stripColor(tab[1].replaceAll(" ", "")));
                        use -= 1;

                        StringBuilder builder = new StringBuilder();
                        builder.append(tab[0]);
                        builder.append(config.getSeparateUses() + " ");
                        builder.append(color + "" + use);
                        lores.add(builder.toString());
                    } else {
                        lores.add(s);
                    }

                }
                meta.setLore(lores);
                i.setItemMeta(meta);
                p.getInventory().setItem(count, i);
                if (uses == 0) {
                    p.getInventory().setItem(count, new ItemStack(Material.AIR));
                }
                return;
            }
        }
    }

    public boolean isMember(Player player, Island island) {
        for (SuperiorPlayer p : island.getIslandMembers(true)) {
            if (p.getName().equals(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public int haveWand(Player player) {
        int count = 0;
        for (ItemStack i : player.getInventory().getContents()) {
            if (isWand(i, player)) {
                count++;
                if(i.getAmount() > 1) {
                    count += i.getAmount();
                }
            }
        }
        return count;
    }

    public boolean isWand(ItemStack item, Player player) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        NBTItem compound = new NBTItem(item);
        if (!compound.hasKey("ownerName") || !compound.hasKey("set-wand") || !compound.hasKey("uses")) {
            return false;
        }

        return compound.getString("ownerName").equals(player.getName()) && compound.getBoolean("set-wand")
                && item.getType().name().equals(wand.getItem());
    }

    public void addCorner(Player p, Location location, int pos) {
        Location[] locs = new Location[2];

        if (pos == 0) {
            locs[0] = location;
            locs[1] = corners.get(p.getUniqueId())[1];
        } else if (pos == 1) {
            locs[1] = location;
            locs[0] = corners.get(p.getUniqueId())[0];
        }

        corners.put(p.getUniqueId(), locs);
    }

    @Override
    public File getFile() {
        return new File(IslandWorldEdit.getInstance().getDataFolder(), "config.json");
    }

    public File getWandFile() {
        return new File(IslandWorldEdit.getInstance().getDataFolder(), "wand.json");
    }

    @Override
    public void loadData() {
        String content = DiscUtil.readCatch(this.getFile());
        if (content != null) {
            Type type = new TypeToken<Config>() {
            }.getType();
            this.config = IslandWorldEdit.getInstance().getGson().fromJson(content, type);
        }

        String content2 = DiscUtil.readCatch(this.getWandFile());
        if (content2 != null) {
            Type type = new TypeToken<Wand>() {
            }.getType();
            this.wand = IslandWorldEdit.getInstance().getGson().fromJson(content2, type);
        }
    }

    @Override
    public void saveData() {
        DiscUtil.writeCatch(this.getFile(), IslandWorldEdit.getInstance().getGson().toJson(config));
        DiscUtil.writeCatch(this.getWandFile(), IslandWorldEdit.getInstance().getGson().toJson(wand));
    }

    public HashMap<UUID, Location[]> getCorners() {
        return corners;
    }

    public Config getConfig() {
        return config;
    }

    public Wand getWand() {
        return wand;
    }

    public HashMap<UUID, BlockRunnable> getInWE() {
        return inWE;
    }

    public Set<Player> getTempVizualize() {
        return tempVizualize;
    }
}
