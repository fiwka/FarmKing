package ru.kdev.FarmKing.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.kdev.FarmKing.Main;
import ru.kdev.FarmKing.farm.Farm;

import java.io.File;
import java.io.UnsupportedEncodingException;

@ru.kdev.FarmKing.commands.Command(name="home")
public class HomeCmd implements ICommand {
    private Main plugin;

    public HomeCmd(Main plugin) {
        this.plugin = plugin;
    }

    public void onCommand(CommandSender commandSender, Command command, String[] args) {
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
    }
}
