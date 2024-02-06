package net.justempire.tictactoe.commands;

import net.justempire.tictactoe.TicTacToe;
import net.justempire.tictactoe.classes.TtcPlayRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class TtcInviteCommand {
    private final JavaPlugin plugin;

    public  TtcInviteCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TicTacToe.getMessage(this, "only-players"));
            return true;
        }

        // Return if receiver's name wasn't provided
        if (strings.length != 2) {
            commandSender.sendMessage(TicTacToe.getMessage(this, "provide-a-name"));
            return true;
        }

        String receiverName = strings[1];
        Player sender = (Player) commandSender;
        Player receiver = null;

        // Checking if provided receiver is present online
        Collection<Player> players = (Collection<Player>) plugin.getServer().getOnlinePlayers();
        for (Player p : players) {
            if (!p.getDisplayName().equals(receiverName)) continue;
            receiver = p;
            break;
        }

        // Return if receiver wasn't found
        if (receiver == null) {
            commandSender.sendMessage(TicTacToe.getMessage(this, "player-wasnt-found"));
            return true;
        }

        // Return if request was sent by sender to himself
        if (receiver.getDisplayName().equals(sender.getDisplayName())) {
            sender.sendMessage(TicTacToe.getMessage(this, "cant-send-to-yourself"));
            return true;
        }

        // Removing requests the sander made before
        for (int i = 0; i < TicTacToe.requests.size(); i++) {
            TtcPlayRequest request = TicTacToe.requests.get(i);
            if (request.getSender() != sender) continue;

            TicTacToe.requests.remove(request);
            sender.sendMessage(TicTacToe.getMessage(this, "previous-request-was-deleted"));
            i--;
        }

        // Generating a request and adding it to pending requests list
        TtcPlayRequest request = new TtcPlayRequest(sender, receiver);
        TicTacToe.requests.add(request);

        // Sending notifying messages to players
        sender.sendMessage(TicTacToe.getMessage(this, "request-sent-successfully"));
        receiver.sendMessage(TicTacToe.getMessage(this, "received-a-request"));

        return true;
    }
}