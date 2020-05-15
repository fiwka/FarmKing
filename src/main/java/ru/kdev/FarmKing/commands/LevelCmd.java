package ru.kdev.FarmKing.commands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.kdev.FarmKing.Main;
import ru.kdev.FarmKing.farm.Farm;
import ru.kdev.FarmKing.gui.Level;

import java.io.File;
import java.io.UnsupportedEncodingException;

@ru.kdev.FarmKing.commands.Command(name="level")
public class LevelCmd implements ICommand {
    private Main plugin;

    public LevelCmd(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) throws UnsupportedEncodingException {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Farm farm = new Farm(plugin, player, new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator + "test.schematic"));
            if(farm.getLevel() == 8) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "У Вас максимальный уровень!"));
            } else {
                Level.INVENTORY.open(player);
            }
        }
    }
}
