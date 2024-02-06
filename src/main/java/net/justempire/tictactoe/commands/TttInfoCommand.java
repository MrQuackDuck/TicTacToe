package net.justempire.tictactoe.commands;

import net.justempire.tictactoe.TicTacToe;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TttInfoCommand {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(TicTacToe.getMessage(this, "info"));

        return true;
    }
}
