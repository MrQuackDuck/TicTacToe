package net.justempire.tictactoe.commands;

import net.justempire.tictactoe.TicTacToe;
import net.justempire.tictactoe.classes.TtcPlayRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class TtcInfoCommand {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(TicTacToe.getMessage(this, "info"));

        return true;
    }
}
