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

@ru.kdev.FarmKing.commands.Command(name = "farm", permission = "farmking.admin")
public class FarmCmd implements ICommand {
    private Main plugin;

    public FarmCmd(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) throws UnsupportedEncodingException {
        if(args.length > 0) {
            String subCmd = args[0];
            if(subCmd.equalsIgnoreCase("reload")) {
                plugin.reloadFarms();
                plugin.reloadConfig();
                plugin.loadLevels();
                plugin.loadPrices();
                sender.sendMessage(ChatColor.GREEN + "Плагин перезагружен!");
            }
            else if(subCmd.equalsIgnoreCase("set")) {
                if(args.length > 3) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if(player == null) return;
                    Farm farm = new Farm(plugin, player, new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator + "test.schematic"));
                    farm.set(args[2], Integer.parseInt(args[3]));
                    sender.sendMessage(ChatColor.GREEN + "Значение успешно установлено!");
                } else {
                    sender.sendMessage(ChatColor.RED + "Не указаны аргументы: Игрок, Опция, Значение!");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Доступные подкоманды: reload, set");
        }
    }
}
