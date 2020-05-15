package ru.kdev.FarmKing.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.UnsupportedEncodingException;

public interface ICommand {
    void onCommand(CommandSender commandSender, Command command, String[] args) throws UnsupportedEncodingException;
}
