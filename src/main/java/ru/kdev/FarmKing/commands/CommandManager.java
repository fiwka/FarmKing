package ru.kdev.FarmKing.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.kdev.FarmKing.Main;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CommandManager implements CommandExecutor {
    private Main plugin;
    private static Map<String, ICommand> commandMap = new HashMap<>();
    private static Map<String, String> permission = new HashMap<>();

    public CommandManager(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if(commandMap.containsKey(command.getName())) {
            if(permission.containsKey(command.getName())) {
                if(!commandSender.hasPermission(permission.get(command.getName()))) {
                    commandSender.sendMessage(ChatColor.RED + "Нет прав!");
                    return true;
                }
            }
            try {
                commandMap.get(command.getName()).onCommand(commandSender, command, strings);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void register(ICommand iCommand) {
        if(iCommand.getClass().isAnnotationPresent(Command.class)) {
            Command command = iCommand.getClass().getAnnotation(Command.class);
            commandMap.put(command.name(), iCommand);
            if(!command.permission().isEmpty()) {
                permission.put(command.name(), command.permission());
            }
        }
    }
}
