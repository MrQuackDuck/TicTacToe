package net.justempire.tictactoe.commands;

import net.justempire.tictactoe.TicTacToe;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TttCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;

    public TttCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            // Print info if command was empty
            return new TttInfoCommand().onCommand(commandSender, command, s, strings);
        }
        // Delegating command parameters to dedicated handlers
        else if (strings[0].equalsIgnoreCase("info")) {
            return new TttInfoCommand().onCommand(commandSender, command, s, strings);
        }
        else if (strings[0].equalsIgnoreCase("invite") && commandSender.hasPermission("tictactoe.invite")) {
            return new TttInviteCommand(plugin).onCommand(commandSender, command, s, strings);
        }
        else if (strings[0].equalsIgnoreCase("accept") && commandSender.hasPermission("tictactoe.accept")) {
            return new TttAcceptCommand(plugin).onCommand(commandSender, command, s, strings);
        }
        else if (strings[0].equalsIgnoreCase("reload") && commandSender.hasPermission("tictactoe.admin")) {
            return new TttReloadCommand(plugin).onCommand(commandSender, command, s, strings);
        }
        else {
            commandSender.sendMessage(TicTacToe.getMessage(this, "command-not-found"));
        }

        return true;
    }

    // Chat autocomplete with [Tab]
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> options = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        if (args.length > 1 && args[0].equals("invite")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                // Skip iteration to prevent the sender appearing in the invite list (player cannot invite himself)
                if (commandSender instanceof Player && ((Player)commandSender).getDisplayName().equals(p.getDisplayName())) continue;
                options.add(p.getName());
            }

            StringUtil.copyPartialMatches(args[1], options, completions);
            return completions;
        }

        if (args.length != 1) return completions;

        options.add("info");
        if (commandSender.hasPermission("tictactoe.invite")) options.add("invite");
        if (commandSender.hasPermission("tictactoe.accept")) options.add("accept");
        if (commandSender.hasPermission("tictactoe.admin")) options.add("reload");
        StringUtil.copyPartialMatches(args[0], options, completions);

        return completions;
    }
}
