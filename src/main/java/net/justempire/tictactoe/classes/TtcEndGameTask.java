package net.justempire.tictactoe.classes;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TtcEndGameTask extends BukkitRunnable {
    private final TtcMatch match;

    public TtcEndGameTask(TtcMatch match) {
        this.match = match;
    }

    @Override
    public void run() {
        match.end();
    }
}