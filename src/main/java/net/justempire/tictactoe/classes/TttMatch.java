package net.justempire.tictactoe.classes;

import net.justempire.tictactoe.TicTacToe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Random;

public class TttMatch {
    public JavaPlugin plugin;

    private final Material firstPlayerItem;
    private final Material secondPlayerItem;

    private final int[] field;
    private final Player firstPlayer;
    private final Player secondPlayer;
    private boolean matchFinished;
    private Inventory gui;
    private TttPlayer whoseTurn;

    private final boolean  broadcastWinEnabled;
    private final boolean broadcastDrawEnabled;
    private final boolean spawnFireworkForWinner;

    public TttMatch(Player firstPlayer, Player secondPlayer, JavaPlugin plugin) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.plugin = plugin;

        // Getting the items from the config
        ConfigurationSection items = plugin.getConfig().getConfigurationSection("items");
        this.firstPlayerItem = Material.valueOf(items.get("first").toString());
        this.secondPlayerItem = Material.valueOf(items.get("second").toString());

        // Getting additional information from the config
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("misc");
        this.broadcastWinEnabled = (boolean) config.get("broadcastWinEnabled");
        this.broadcastDrawEnabled = (boolean) config.get("broadcastDrawEnabled");
        this.spawnFireworkForWinner = (boolean) config.get("spawnFireworkForWinner");

        // Initializing the field
        field = new int[9];
        Arrays.fill(field, -1);

        // Randomly choosing who will have the first turn (except TtcPlayer.NONE)
        whoseTurn = TttPlayer.values()[new Random().nextInt(TttPlayer.values().length - 1) + 1];

        updateGui();
    }

    public void move(TttPlayer player, int slotIndex) {
        // Return if the slot is not in bounds of the field (3 x 3)
        if (slotIndex < 0 || slotIndex > 8) return;

        // Return if player is not allowed to make a turn
        if (whoseTurn != player) return;

        // Return if provided slot was already taken
        if (field[slotIndex] != -1) return;

        // Setting the value of a field cell with corresponding player index
        field[slotIndex] = player.ordinal();

        TttMatchState state = getFieldState();

        if (state == TttMatchState.FIRST_PLAYER_WON) {
            whoseTurn = TttPlayer.NONE;
            win(TttPlayer.FIRST);
            return;
        }

        if (state == TttMatchState.SECOND_PLAYER_WON) {
            whoseTurn = TttPlayer.NONE;
            win(TttPlayer.SECOND);
            return;
        }

        if (state == TttMatchState.DRAW) {
            whoseTurn = TttPlayer.NONE;
            draw();
            return;
        }

        // Choosing who will have the next move
        if (whoseTurn == TttPlayer.FIRST) whoseTurn = TttPlayer.SECOND;
        else if (whoseTurn == TttPlayer.SECOND) whoseTurn = TttPlayer.FIRST;

        updateGui();
    }

    // Simplified overload of updateGui()
    // Updates the gui with current state of a game
    public void updateGui() {
        updateGui(null);
    }

    // Updates the gui with current state of a game
    public void updateGui(String customTitle) {

        // Setting default window title, if customTitle is null
        if (customTitle == null) {
            Player playerWhoseMove;
            if (whoseTurn == TttPlayer.FIRST) playerWhoseMove = firstPlayer;
            else playerWhoseMove = secondPlayer;
            customTitle = String.format(TicTacToe.getMessage(this, "gui-player's-turn", true), playerWhoseMove.getDisplayName());
        }

        // Creating new inventory with updated state
        gui = Bukkit.createInventory(null, InventoryType.DROPPER, customTitle);

        // Filling up the inventory with current items
        for (int i = 0; i < field.length; i++) {
            if (field[i] == -1) continue;

            if (TttPlayer.values()[field[i]] == TttPlayer.FIRST)
                gui.setItem(i, new ItemStack(firstPlayerItem));
            else if (TttPlayer.values()[field[i]] == TttPlayer.SECOND)
                gui.setItem(i, new ItemStack(secondPlayerItem));
        }

        // Opening the updated inventory to players
        firstPlayer.openInventory(gui);
        secondPlayer.openInventory(gui);
    }

    public Inventory getGui() {
        return gui;
    }

    public void win(TttPlayer player) {
        this.matchFinished = true;

        Player winner;
        Player looser;

        if (player == TttPlayer.FIRST) {
            winner = firstPlayer;
            looser = secondPlayer;
        }
        else {
            winner = secondPlayer;
            looser = firstPlayer;
        }

        if (this.broadcastWinEnabled) {
            String winBroadcastMessage = String.format(TicTacToe.getMessage(this, "broadcast-win-message"), winner.getDisplayName(), looser.getDisplayName());
            Bukkit.broadcastMessage(winBroadcastMessage);
        }

        String winMessage = String.format(TicTacToe.getMessage(this, "you-won"), looser.getDisplayName());
        String loseMessage = String.format(TicTacToe.getMessage(this, "you-lost"), winner.getDisplayName());

        winner.sendMessage(winMessage);
        looser.sendMessage(loseMessage);
        updateGui(String.format(TicTacToe.getMessage(this, "gui-player-won", true), winner.getDisplayName()));

        // Spawn firework near the winner (if enabled in config)
        if (this.spawnFireworkForWinner)
            winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK);

        new TttEndGameTask(this).runTaskLater(this.plugin, 30);
    }

    public void draw() {
        String drawMessage = TicTacToe.getMessage(this, "game-ended-draw");

        firstPlayer.sendMessage(drawMessage);
        secondPlayer.sendMessage(drawMessage);

        updateGui(TicTacToe.getMessage(this, "gui-draw", true));

        if (this.broadcastDrawEnabled) {
            String winBroadcastMessage = String.format(TicTacToe.getMessage(this, "broadcast-draw-message"), firstPlayer.getDisplayName(), secondPlayer.getDisplayName());
            Bukkit.broadcastMessage(winBroadcastMessage);
        }

        this.end();
    }

    public void end() {
        if (TicTacToe.matches.contains(this))
            TicTacToe.matches.remove(this);

        firstPlayer.closeInventory();
        secondPlayer.closeInventory();
    }

    public void gameAbortedByPlayer(TttPlayer player) {
        // Don't do anything when match is already finished
        if (this.matchFinished) return;

        String winMessage = TicTacToe.getMessage(this, "opponent-aborted-game");
        String loseMessage = TicTacToe.getMessage(this, "you-aborted-game");

        if (player == TttPlayer.FIRST)
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

    private TttMatchState getFieldState() {
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
                TttPlayer winner = TttPlayer.values()[twoDField[0][y]];
                if (winner == TttPlayer.FIRST) return TttMatchState.FIRST_PLAYER_WON;
                if (winner == TttPlayer.SECOND) return TttMatchState.SECOND_PLAYER_WON;
            }
        }

        // Checking horizontals
        for (int x = 0; x < 3; x++) {
            if (twoDField[x][0] == -1) continue;

            if (twoDField[x][0] == twoDField[x][1] && twoDField[x][1] == twoDField[x][2]) {
                TttPlayer winner = TttPlayer.values()[twoDField[x][0]];
                if (winner == TttPlayer.FIRST) return TttMatchState.FIRST_PLAYER_WON;
                if (winner == TttPlayer.SECOND) return TttMatchState.SECOND_PLAYER_WON;
            }
        }

        // Checking diagonals
        if (twoDField[0][0] == twoDField[1][1] && twoDField[1][1] == twoDField[2][2] && twoDField[0][0] != -1)
        {
            TttPlayer winner = TttPlayer.values()[twoDField[0][0]];
            if (winner == TttPlayer.FIRST) return TttMatchState.FIRST_PLAYER_WON;
            if (winner == TttPlayer.SECOND) return TttMatchState.SECOND_PLAYER_WON;
        }
        if (twoDField[0][2] == twoDField[1][1] && twoDField[1][1] == twoDField[2][0] && twoDField[0][2] != -1)
        {
            TttPlayer winner = TttPlayer.values()[twoDField[0][2]];
            if (winner == TttPlayer.FIRST) return TttMatchState.FIRST_PLAYER_WON;
            if (winner == TttPlayer.SECOND) return TttMatchState.SECOND_PLAYER_WON;
        }

        // Checking if the field is full
        int emptyFields = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (twoDField[x][y] == -1) emptyFields++;
            }
        }
        if (emptyFields == 0) return TttMatchState.DRAW;

        return TttMatchState.GAME_NOT_FINISHED;
    }
}
