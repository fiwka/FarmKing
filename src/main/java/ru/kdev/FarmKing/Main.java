package ru.kdev.FarmKing;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kdev.FarmKing.commands.CommandManager;
import ru.kdev.FarmKing.commands.FarmCmd;
import ru.kdev.FarmKing.commands.HomeCmd;
import ru.kdev.FarmKing.commands.LevelCmd;
import ru.kdev.FarmKing.listener.FarmListener;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private FileConfiguration farms = null;
    private File farmsFile = null;
    private CommandManager commandManager = null;
    public static Map<Material, Double> prices = new HashMap<>();
    public static Map<Material, Integer> levels = new HashMap<>();
    public static Map<Integer, Integer> lvlPrice = new HashMap<>();

    @Override
    public void onEnable() {
        if(Files.notExists(Paths.get(getDataFolder().getAbsolutePath() + File.separator + "schematics"))) {
            File file = new File(getDataFolder().getAbsolutePath() + File.separator + "schematics");
            file.mkdirs();
        }
        World world = Bukkit.getWorld(getConfig().getString("farmWorld"));
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setTime(2000);
        world.setGameRuleValue("randomTickSpeed", "100");
        world.setGameRuleValue("doTileDrops", "false");
        world.setPVP(false);
        this.saveDefaultConfig();
        this.saveFarms();
        loadPrices();
        loadLevels();
        loadLvlPrice();
        Bukkit.getPluginManager().registerEvents(new FarmListener(this), this);
        commandManager = new CommandManager(this);
        this.getCommand("home").setExecutor(commandManager);
        this.getCommand("farm").setExecutor(commandManager);
        this.getCommand("level").setExecutor(commandManager);
        commandManager.register(new HomeCmd(this));
        commandManager.register(new FarmCmd(this));
        commandManager.register(new LevelCmd(this));
    }

    public void loadPrices() {
        ConfigurationSection configurationSection = getConfig().getConfigurationSection("prices");
        if(!prices.isEmpty()) prices.clear();
        for(String path : configurationSection.getKeys(false)) {
            prices.put(Material.getMaterial(path), getConfig().getDouble("prices." + path));
        }
    }

    public void loadLevels() {
        ConfigurationSection configurationSection = getConfig().getConfigurationSection("levels");
        if(!levels.isEmpty()) levels.clear();
        for(String path : configurationSection.getKeys(false)) {
            levels.put(Material.getMaterial(path), getConfig().getInt("levels." + path));
        }
    }

    private void loadLvlPrice() {
        lvlPrice.put(2, 1000);
        lvlPrice.put(3, 2500);
        lvlPrice.put(4, 7500);
        lvlPrice.put(5, 12500);
        lvlPrice.put(6, 20000);
        lvlPrice.put(7, 50000);
        lvlPrice.put(8, 250000);
    }

    public void reloadFarms() throws UnsupportedEncodingException {
        if (farmsFile == null) {
            farmsFile = new File(getDataFolder(), "farms.yml");
        }
        farms = YamlConfiguration.loadConfiguration(farmsFile);

        // Look for defaults in the jar
        Reader defConfigStream = new InputStreamReader(this.getResource("farms.yml"), "UTF8");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            farms.setDefaults(defConfig);
        }
    }

    public FileConfiguration getFarms() throws UnsupportedEncodingException {
        if (farms == null) {
            reloadFarms();
        }
        return farms;
    }

    public void saveFarms() {
        if (farms == null || farmsFile == null) {
            return;
        }
        try {
            getFarms().save(farmsFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + farmsFile, ex);
        }
    }
}
