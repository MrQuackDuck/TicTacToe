package net.justempire.tictactoe.classes;

import net.justempire.tictactoe.TicTacToe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Random;

public class TtcMatch {
    public JavaPlugin plugin;

    private final Material firstPlayerItem;
    private final Material secondPlayerItem;

    private final int[] field;
    private final Player firstPlayer;
    private final Player secondPlayer;
    private Inventory gui;

    private TtcPlayer whoseTurn;

    public TtcMatch(Player firstPlayer, Player secondPlayer, JavaPlugin plugin) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.plugin = plugin;

        // Getting the items from config
        ConfigurationSection configSection = plugin.getConfig().getConfigurationSection("items");
        firstPlayerItem = Material.valueOf(configSection.get("first").toString());
        secondPlayerItem = Material.valueOf(configSection.get("second").toString());

        // Initializing the field
        field = new int[9];
        Arrays.fill(field, -1);

        // Randomly choosing who will have the first turn (except TtcPlayer.NONE)
        whoseTurn = TtcPlayer.values()[new Random().nextInt(TtcPlayer.values().length - 1) + 1];

        updateGui();
    }

    public void move(TtcPlayer player, int slotIndex) {
        // Checking if the slot is in bounds of the field (3 x 3)
        if (slotIndex < 0 || slotIndex > 8) return;

        // Checking if player is allowed to make a turn
        if (whoseTurn != player) return;

        // Checking if this slot was already taken
        if (field[slotIndex] != -1) return;

        // Setting the value of field cell with corresponding player index
        field[slotIndex] = player.ordinal();

        TtcMatchState state = getFieldState();

        if (state == TtcMatchState.FIRST_PLAYER_WON) {
            whoseTurn = TtcPlayer.NONE;
            win(TtcPlayer.FIRST);
            return;
        }

        if (state == TtcMatchState.SECOND_PLAYER_WON) {
            whoseTurn = TtcPlayer.NONE;
            win(TtcPlayer.SECOND);
            return;
        }

        if (state == TtcMatchState.DRAW) {
            whoseTurn = TtcPlayer.NONE;
            draw();
            return;
        }

        // Choosing who will have the next move
        if (whoseTurn == TtcPlayer.FIRST) whoseTurn = TtcPlayer.SECOND;
        else if (whoseTurn == TtcPlayer.SECOND) whoseTurn = TtcPlayer.FIRST;

        updateGui();
    }

    // Simplified overload of updateGui()
    // Updates the gui with current state of a game
    public void updateGui() {
        updateGui(null);
    }

    // Updates the gui with current state of a game
    public void updateGui(String customTitle) {

        // Setting default window title, if provided is null
        if (customTitle == null) {
            Player playerWhoseMove;
            if (whoseTurn == TtcPlayer.FIRST) playerWhoseMove = firstPlayer;
            else playerWhoseMove = secondPlayer;
            customTitle = String.format(TicTacToe.getMessage(this, "gui-player's-turn", true), playerWhoseMove.getDisplayName());
        }

        // Creating new inventory with updated state
        gui = Bukkit.createInventory(null, InventoryType.DROPPER, customTitle);

        // Filling the inventory
        for (int i = 0; i < field.length; i++) {
            if (field[i] == -1) continue;

            if (TtcPlayer.values()[field[i]] == TtcPlayer.FIRST)
                gui.setItem(i, new ItemStack(firstPlayerItem));
            else if (TtcPlayer.values()[field[i]] == TtcPlayer.SECOND)
                gui.setItem(i, new ItemStack(secondPlayerItem));
        }

        // Opening the updated inventory to players
        firstPlayer.openInventory(gui);
        secondPlayer.openInventory(gui);
    }

    public Inventory getGui() {
        return gui;
    }

    public void win(TtcPlayer player) {
        Player winner;
        Player looser;

        if (player == TtcPlayer.FIRST) {
            winner = firstPlayer;
            looser = secondPlayer;
        }
        else {
            winner = secondPlayer;
            looser = firstPlayer;
        }

        String winMessage = String.format(TicTacToe.getMessage(this, "you-won"), looser.getDisplayName());
        String loseMessage = String.format(TicTacToe.getMessage(this, "you-lost"), winner.getDisplayName());

        winner.sendMessage(winMessage);
        looser.sendMessage(loseMessage);
        updateGui(String.format(TicTacToe.getMessage(this, "gui-player-won", true), winner.getDisplayName()));

        new TtcEndGameTask(this).runTaskLater(this.plugin, 30);
    }

    public void draw() {
        String drawMessage = TicTacToe.getMessage(this, "game-ended-draw");

        firstPlayer.sendMessage(drawMessage);
        secondPlayer.sendMessage(drawMessage);

        updateGui(TicTacToe.getMessage(this, "gui-draw", true));

        this.end();
    }

    public void end() {
        if (TicTacToe.matches.contains(this))
            TicTacToe.matches.remove(this);

        firstPlayer.closeInventory();
        secondPlayer.closeInventory();
    }

    public void gameAbortedByPlayer(TtcPlayer player) {
        String winMessage = TicTacToe.getMessage(this, "opponent-aborted-game");
        String loseMessage = TicTacToe.getMessage(this, "you-aborted-game");

        if (player == TtcPlayer.FIRST)
        {
            firstPlayer.sendMessage(loseMessage);
            secondPlayer.sendMessage(winMessage);
        }
        else
        {
            firstPlayer.sendMessage(winMessage);
            secondPlayer.sendMessage(loseMessage);
        }

        this.end();
    }

    public boolean containsPlayer(Player player) {
        if (firstPlayer == player || secondPlayer == player) return true;
        return false;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    private TtcMatchState getFieldState() {
        int[][] twoDField = new int[3][3];

        // Converting field to 2D-array
        int index = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                twoDField[x][y] = field[index++];
            }
        }

        // Checking verticals
        for (int y = 0; y < 3; y++) {
            if (twoDField[0][y] == -1) continue;

            if (twoDField[0][y] == twoDField[1][y] && twoDField[1][y] == twoDField[2][y]) {
                TtcPlayer winner = TtcPlayer.values()[twoDField[0][y]];
                if (winner == TtcPlayer.FIRST) return TtcMatchState.FIRST_PLAYER_WON;
                if (winner == TtcPlayer.SECOND) return TtcMatchState.SECOND_PLAYER_WON;
            }
        }

        // Checking horizontals
        for (int x = 0; x < 3; x++) {
            if (twoDField[x][0] == -1) continue;

            if (twoDField[x][0] == twoDField[x][1] && twoDField[x][1] == twoDField[x][2]) {
                TtcPlayer winner = TtcPlayer.values()[twoDField[x][0]];
                if (winner == TtcPlayer.FIRST) return TtcMatchState.FIRST_PLAYER_WON;
                if (winner == TtcPlayer.SECOND) return TtcMatchState.SECOND_PLAYER_WON;
            }
        }

        // Checking diagonals
        if (twoDField[0][0] == twoDField[1][1] && twoDField[1][1] == twoDField[2][2] && twoDField[0][0] != -1)
        {
            TtcPlayer winner = TtcPlayer.values()[twoDField[0][0]];
            if (winner == TtcPlayer.FIRST) return TtcMatchState.FIRST_PLAYER_WON;
            if (winner == TtcPlayer.SECOND) return TtcMatchState.SECOND_PLAYER_WON;
        }
        if (twoDField[0][2] == twoDField[1][1] && twoDField[1][1] == twoDField[2][0] && twoDField[0][2] != -1)
        {
            TtcPlayer winner = TtcPlayer.values()[twoDField[0][2]];
            if (winner == TtcPlayer.FIRST) return TtcMatchState.FIRST_PLAYER_WON;
            if (winner == TtcPlayer.SECOND) return TtcMatchState.SECOND_PLAYER_WON;
        }

        // Checking if the field is full
        int emptyFields = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (twoDField[x][y] == -1) emptyFields++;
            }
        }
        if (emptyFields == 0) return TtcMatchState.DRAW;

        return TtcMatchState.GAME_NOT_FINISHED;
    }
}
