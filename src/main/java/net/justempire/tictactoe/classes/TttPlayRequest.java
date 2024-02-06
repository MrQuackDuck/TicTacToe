package net.justempire.tictactoe.classes;

import org.bukkit.entity.Player;

public class TttPlayRequest {
    private Player sender;
    private Player receiver;

    public TttPlayRequest(Player sender, Player receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }
}
