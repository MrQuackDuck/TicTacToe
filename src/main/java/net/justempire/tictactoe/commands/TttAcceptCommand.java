package net.justempire.tictactoe.commands;

import net.justempire.tictactoe.TicTacToe;
import net.justempire.tictactoe.classes.TttPlayRequest;
import net.justempire.tictactoe.classes.TttMatch;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TttAcceptCommand {
    private final JavaPlugin plugin;

    public TttAcceptCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TicTacToe.getMessage(this, "only-players"));
            return true;
        }

        Player player = (Player) commandSender;

        // Getting the latest pending request to play Tic Tac Toe
        TttPlayRequest request = null;
        for (int i = 0; i < TicTacToe.requests.size(); i++) {
            if (!TicTacToe.requests.get(i).getReceiver().getDisplayName().equals(player.getDisplayName())) continue;

            request = TicTacToe.requests.get(i);
            TicTacToe.requests.remove(request);
            i--;
        }

        // Return if not a single play request wasn't found
        if (request == null) {
            player.sendMessage(TicTacToe.getMessage(this, "request-to-play-wasnt-found"));
            return true;
        }

        Player sender = request.getSender();

        // Initializing a new TicTacToe match
        TttMatch match = new TttMatch(sender, player, plugin);
        TicTacToe.matches.add(match);

        // Sending notifying messages to players
        sender.sendMessage(TicTacToe.getMessage(this, "request-was-accepted"));
        player.sendMessage(String.format(TicTacToe.getMessage(this, "you-accepted-request"), sender.getDisplayName()));

        return true;
    }
}
