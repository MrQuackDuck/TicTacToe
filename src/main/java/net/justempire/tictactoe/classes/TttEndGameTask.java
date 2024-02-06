package net.justempire.tictactoe.classes;

import org.bukkit.scheduler.BukkitRunnable;

public class TttEndGameTask extends BukkitRunnable {
    private final TttMatch match;

    public TttEndGameTask(TttMatch match) {
        this.match = match;
    }

    @Override
    public void run() {
        match.end();
    }
}