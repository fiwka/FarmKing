package ru.kdev.FarmKing.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.kdev.FarmKing.Main;

import java.util.HashMap;
import java.util.Map;

public class CommandManager implements CommandExecutor {
    private Main plugin;
    private static Map<String, ICommand> commandMap = new HashMap<>();

    public CommandManager(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if(commandMap.containsKey(command.getName())) {
            commandMap.get(command.getName()).onCommand(commandSender, command, strings);
        }
        return false;
    }

    public void register(ICommand iCommand) {
        if(iCommand.getClass().isAnnotationPresent(Command.class)) {
            Command command = iCommand.getClass().getAnnotation(Command.class);
            commandMap.put(command.name(), iCommand);
        }
    }
}
