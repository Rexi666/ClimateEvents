package org.rexi.climateEvents.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.rexi.climateEvents.events.AcidRainEvent;

import static org.rexi.climateEvents.ClimateEvents.prefix;

public class CommandHandler implements CommandExecutor {

    private final AcidRainEvent acidRainEvent;

    public CommandHandler(AcidRainEvent acidRainEvent) {
        this.acidRainEvent = acidRainEvent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("climateevents.admin")) {
            sender.sendMessage(prefix.append(Component.text("No tienes permiso para usar este comando").color(NamedTextColor.RED)));
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage(prefix
                    .append(Component.text("Usa /climateevents acidrain <start|stop>").color(NamedTextColor.RED)));
            return false;
        }

        if (args[0].equalsIgnoreCase("acidrain")) {
            if (!sender.hasPermission("climateevents.admin.events")) {
                sender.sendMessage(prefix.append(Component.text("No tienes permiso para usar este comando").color(NamedTextColor.RED)));
                return false;
            }

            if (args[1].equalsIgnoreCase("start")) {
                if (acidRainEvent.isAcidRainActive()) {
                    sender.sendMessage(prefix.append(Component.text("La lluvia ácida ya está en curso").color(NamedTextColor.RED)));
                } else {
                    acidRainEvent.startAcidRain();
                    sender.sendMessage(prefix.append(Component.text("Lluvia ácida empezada").color(NamedTextColor.GREEN)));
                }
            } else if (args[1].equalsIgnoreCase("stop")) {
                if (!acidRainEvent.isAcidRainActive()) {
                    sender.sendMessage(prefix.append(Component.text("No hay lluvia ácida en curso").color(NamedTextColor.RED)));
                } else {
                    acidRainEvent.stopAcidRain();
                    sender.sendMessage(prefix
                            .append(Component.text("Lluvia ácida terminada").color(NamedTextColor.GREEN)));
                }
            } else {
                sender.sendMessage(prefix
                        .append(Component.text("Usa /climateevents acidrain <start|stop>").color(NamedTextColor.RED)));
            }
        } else {
            sender.sendMessage(prefix
                    .append(Component.text("Usa /climateevents acidrain <start|stop>").color(NamedTextColor.RED)));
        }
        return true;
    }
}
