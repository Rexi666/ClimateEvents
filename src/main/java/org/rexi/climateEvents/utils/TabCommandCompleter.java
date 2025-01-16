package org.rexi.climateEvents.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCommandCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("acidrain");
            suggestions.add("solarflare");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("acidrain")) {
                suggestions.add("start");
                suggestions.add("stop");
            } else if (args[0].equalsIgnoreCase("solarflare")) {
                suggestions.add("start");
                suggestions.add("stop");
            }
        }

        return suggestions;
    }
}
