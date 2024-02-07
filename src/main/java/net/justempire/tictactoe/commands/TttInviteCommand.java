package net.justempire.tictactoe.commands;

import net.justempire.tictactoe.TicTacToe;
import net.justempire.tictactoe.classes.TttPlayRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class TttInviteCommand {
    private final JavaPlugin plugin;

    public TttInviteCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TicTacToe.getMessage(this, "only-players"));
            return true;
        }

        // Return if receiver name wasn't provided in argument
        if (strings.length != 2) {
            commandSender.sendMessage(TicTacToe.getMessage(this, "provide-a-name"));
            return true;
        }

        String receiverName = strings[1];
        Player sender = (Player) commandSender;
        Player receiver = Bukkit.getPlayer(receiverName);

        // Return if receiver wasn't found
        if (receiver == null) {
            commandSender.sendMessage(TicTacToe.getMessage(this, "player-wasnt-found"));
            return true;
        }

        // Return if request was sent by a sender to himself
        if (receiver.getDisplayName().equals(sender.getDisplayName())) {
            sender.sendMessage(TicTacToe.getMessage(this, "cant-send-to-yourself"));
            return true;
        }

        if (!receiver.hasPermission("tictactoe.accept")) {
            sender.sendMessage(TicTacToe.getMessage(this, "receiver-does-not-have-enough-permissions"));
            return true;
        }

        // Removing the requests may be made by a sender before
        for (int i = 0; i < TicTacToe.requests.size(); i++) {
            TttPlayRequest request = TicTacToe.requests.get(i);
            if (request.getSender() != sender) continue;

            TicTacToe.requests.remove(request);
            sender.sendMessage(TicTacToe.getMessage(this, "previous-request-was-deleted"));
            break;
        }

        // Generating a request and adding it to pending requests list
        TttPlayRequest request = new TttPlayRequest(sender, receiver);
        TicTacToe.requests.add(request);

        // Sending notifying messages to players
        sender.sendMessage(TicTacToe.getMessage(this, "request-sent-successfully"));
        receiver.sendMessage(String.format(TicTacToe.getMessage(this, "received-a-request"), sender.getDisplayName()));

        return true;
    }
}
