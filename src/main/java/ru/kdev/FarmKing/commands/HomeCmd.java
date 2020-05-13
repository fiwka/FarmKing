package ru.kdev.FarmKing.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.kdev.FarmKing.Main;
import ru.kdev.FarmKing.farm.Farm;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class HomeCmd implements CommandExecutor {
    private Main plugin;

    public HomeCmd(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Farm farm = null;
        try {
            farm = new Farm(plugin, (Player) commandSender, new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator + "test.schematic"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            if(!farm.exists()) {
                farm.generate();
                commandSender.sendMessage(ChatColor.GREEN + "Подождите, операция выполняется");
                Farm finalFarm = farm;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    finalFarm.teleport((Player) commandSender);
                    try {
                        finalFarm.saveFirst();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    commandSender.sendMessage(ChatColor.GREEN + "Операция выполнена!");
                }, 120L);
            } else {
                farm.teleport((Player) commandSender);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
