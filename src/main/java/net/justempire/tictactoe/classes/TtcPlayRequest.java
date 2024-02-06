package net.justempire.tictactoe.classes;

import org.bukkit.entity.Player;

public class TtcPlayRequest {
    private Player sender;
    private Player receiver;

    public TtcPlayRequest(Player sender, Player receiver) {
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
