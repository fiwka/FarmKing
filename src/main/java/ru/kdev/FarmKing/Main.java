package ru.kdev.FarmKing;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kdev.FarmKing.commands.CommandManager;
import ru.kdev.FarmKing.commands.HomeCmd;
import ru.kdev.FarmKing.listener.FarmListener;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private FileConfiguration farms = null;
    private File farmsFile = null;
    private CommandManager commandManager = null;

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
        world.setPVP(false);
        Bukkit.getPluginManager().registerEvents(new FarmListener(this), this);
        commandManager = new CommandManager(this);
        this.getCommand("home").setExecutor(commandManager);
        commandManager.register(new HomeCmd(this));
        this.saveDefaultConfig();
        try {
            if(!farmsFile.exists()) this.reloadFarms();
            else this.saveFarms();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
