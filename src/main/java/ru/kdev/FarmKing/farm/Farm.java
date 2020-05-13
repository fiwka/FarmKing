package ru.kdev.FarmKing.farm;

import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import ru.kdev.FarmKing.Main;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Farm {
    private File schematic;
    private Player owner;
    private Main plugin;
    private Location location;

    public Farm(Main plugin, Player owner, File schematic) throws UnsupportedEncodingException {
        this.schematic = schematic;
        this.owner = owner;
        this.plugin = plugin;
        if(exists()) {
            this.location = getLocation();
        }
    }

    public void generate() {
        double x = getRandom(-5000, 5000);
        double y = 50;
        double z = getRandom(-5000, 5000);
        org.bukkit.World world = Bukkit.getWorld(plugin.getConfig().getString("farmWorld"));
        Location location = new Location(world, x, y, z);
        org.bukkit.util.Vector vector = new org.bukkit.util.Vector(location.getX(), location.getY(), location.getZ());
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            while (getNearbyBlocks(location, 96).size() > 0) {
                generate();
            }
            paste(schematic, world, vector);
            this.location = location;
        });
    }

    public void teleport(Player player) {
        Location fixLocation = this.location;
        fixLocation.setY(fixLocation.getY()-6.0);
        player.teleport(location);
    }

    public Location getLocation() throws UnsupportedEncodingException {
        return new Location(Bukkit.getWorld(plugin.getFarms().getString(owner.getName() + ".location.world")),
                plugin.getFarms().getDouble(owner.getName() + ".location.x"),
                plugin.getFarms().getDouble(owner.getName() + ".location.y"),
                plugin.getFarms().getDouble(owner.getName() + ".location.z"));
    }

    public int getLevel() throws UnsupportedEncodingException {
        return plugin.getFarms().getInt(owner.getName() + ".settings.level");
    }

    public int getBalance() throws UnsupportedEncodingException {
        return plugin.getFarms().getInt(owner.getName() + ".settings.balance");
    }

    public int getCollected() throws UnsupportedEncodingException {
        return plugin.getFarms().getInt(owner.getName() + ".settings.collected");
    }

    public boolean isPublic() throws UnsupportedEncodingException {
        return plugin.getFarms().getBoolean(owner.getName() + ".settings.public");
    }

    public void set(String option, Object value) throws UnsupportedEncodingException {
        plugin.getFarms().set(owner.getName() + ".settings." + option, value);
        plugin.saveFarms();
    }

    public void saveFirst() throws UnsupportedEncodingException {
        plugin.getFarms().set(owner.getName() + ".location.x", location.getX());
        plugin.getFarms().set(owner.getName() + ".location.y", location.getY());
        plugin.getFarms().set(owner.getName() + ".location.z", location.getZ());
        plugin.getFarms().set(owner.getName() + ".location.world", location.getWorld().getName());
        plugin.getFarms().set(owner.getName() + ".settings.public", false);
        plugin.getFarms().set(owner.getName() + ".settings.level", 1);
        plugin.getFarms().set(owner.getName() + ".settings.balance", 0);
        plugin.getFarms().set(owner.getName() + ".settings.collected", 0);
        plugin.saveFarms();
    }

    public boolean exists() throws UnsupportedEncodingException {
        return plugin.getFarms().contains(owner.getName());
    }

    public Player getOwner() {
        return owner;
    }

    private void paste(File file, org.bukkit.World bworld, org.bukkit.util.Vector vector) {
        try {
            Vector position = new Vector(vector.getX(),vector.getY(),vector.getZ());
            World world = FaweAPI.getWorld(bworld.getName());
            ClipboardFormat.SCHEMATIC.load(file).paste(world, position, true, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getRandom(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    if(location.getWorld().getBlockAt(x, y, z).getType() == Material.AIR) continue;
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }
}
