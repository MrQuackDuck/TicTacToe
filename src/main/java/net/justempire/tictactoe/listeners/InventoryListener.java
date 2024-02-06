package net.justempire.tictactoe.listeners;

import net.justempire.tictactoe.TicTacToe;
import net.justempire.tictactoe.classes.TtcMatch;
import net.justempire.tictactoe.classes.TtcPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        TtcMatch match = null;

        for (int i = 0; i < TicTacToe.matches.size(); i++) {
            TtcMatch ttcMatch = TicTacToe.matches.get(i);
            if (!ttcMatch.getGui().equals(inventory)) continue;
            if (!ttcMatch.containsPlayer(player)) continue;

            match = ttcMatch;
        }

        // Preventing further execution if event was produced not in TicTacToe game inventory
        if (match == null) return;

        Player firstPlayer = match.getFirstPlayer();
        Player secondPlayer = match.getSecondPlayer();
        int slot = event.getSlot();

        // Sending player's move to TicTacToe match
        if (player == firstPlayer) match.move(TtcPlayer.FIRST, slot);
        else if (player == secondPlayer) match.move(TtcPlayer.SECOND, slot);

        // Preventing a player from taking items from the game inventory
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        for (int i = 0; i < TicTacToe.matches.size(); i++) {
            TtcMatch match = TicTacToe.matches.get(i);
            // Determining if the closed inventory was exactly TicTacToe game inventory
            if (!match.getGui().equals(inventory)) continue;
            if (!match.containsPlayer(player)) continue;

            Player firstPlayer = match.getFirstPlayer();
            Player secondPlayer = match.getSecondPlayer();

            // Aborting the game
            if (player == firstPlayer) match.gameAbortedByPlayer(TtcPlayer.FIRST);
            else if (player == secondPlayer) match.gameAbortedByPlayer(TtcPlayer.SECOND);

            break;
        }
    }
}
