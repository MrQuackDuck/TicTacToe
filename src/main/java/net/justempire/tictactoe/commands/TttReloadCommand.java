package net.justempire.tictactoe.commands;

import net.justempire.tictactoe.TicTacToe;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class TttReloadCommand {
    private final TicTacToe plugin;

    public TttReloadCommand(JavaPlugin plugin) {
        this.plugin = (TicTacToe) plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        try {
            plugin.reload();
            commandSender.sendMessage(TicTacToe.getMessage(this, "plugin-reloaded"));
        }
        catch (Exception e) {
            commandSender.sendMessage(TicTacToe.getMessage(this, "failed-to-reload"));
            commandSender.sendMessage(e.getMessage());
        }

        return true;
    }
}

