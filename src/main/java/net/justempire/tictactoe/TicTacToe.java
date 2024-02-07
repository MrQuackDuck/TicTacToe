package net.justempire.tictactoe;

import net.justempire.tictactoe.classes.TttPlayRequest;
import net.justempire.tictactoe.classes.TttMatch;
import net.justempire.tictactoe.commands.TttCommand;
import net.justempire.tictactoe.listeners.InventoryListener;
import net.justempire.tictactoe.utils.MessageColorizer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TicTacToe extends JavaPlugin {
    public static List<TttMatch> matches = new ArrayList<>();
    public static List<TttPlayRequest> requests = new ArrayList<>();

    private static Map<String, String> messages = new HashMap<>();

    @Override
    public void onEnable() {
        // Creating a config if it doesn't exist
        saveDefaultConfig();

        // Setting up the config
        configure();

        // Setting up the /tictactoe command
        TttCommand ttcCommand = new TttCommand(this);
        getCommand("tictactoe").setExecutor(ttcCommand);

        // Registering inventory interaction events
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        System.out.println("[TicTacToe] Enabled successfully!");
    }

    @Override
    public void onDisable() {
        System.out.println("[TicTacToe] Shutting down!");
    }

    private void configure() {
        // Getting the messages from the config
        ConfigurationSection configSection = getConfig().getConfigurationSection("messages");
        if (configSection != null) {
            Map<String, Object> messages = configSection.getValues(true);
            for (Map.Entry<String, Object> pair : messages.entrySet()) {
                this.messages.put(pair.getKey(), pair.getValue().toString());
            }
        }

        saveDefaultConfig();
    }

    public void reload() {
        // Ending all active matches before reloading the plugin
        for (int i = 0; i < matches.size(); i++) {
            matches.get(i).end();
            i--;
        }

        reloadConfig();
        configure();
    }

    // Returns a message from the config by key
    public static String getMessage(Object sender, String key, boolean showGuiPrefix) {
        if (messages == null) return String.format("Message %s wasn't found (messages list is null)", key);
        if (messages.get(key) == null) return String.format("Message %s wasn't found", key);
        if (messages.get("prefix") == null) return String.format("Prefix message wasn't found!", key);

        String prefix = "";
        if (!showGuiPrefix) prefix = messages.get("prefix");
        else prefix = messages.get("gui-prefix");

        return MessageColorizer.colorize(sender, prefix + messages.get(key));
    }

    // Simplified getMessage() overload
    // Returns the message by key from config
    public static String getMessage(Object sender, String key) {
        return getMessage(sender, key, false);
    }
}
