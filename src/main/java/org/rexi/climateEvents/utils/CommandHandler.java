package org.rexi.climateEvents.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.rexi.climateEvents.ClimateEvents;
import org.rexi.climateEvents.events.AcidRainEvent;
import org.rexi.climateEvents.events.ElectricStormEvent;
import org.rexi.climateEvents.events.SandStormEvent;
import org.rexi.climateEvents.events.SolarFlareEvent;

import static org.rexi.climateEvents.ClimateEvents.prefix;

public class CommandHandler implements CommandExecutor {

    private final AcidRainEvent acidRainEvent;
    private final SolarFlareEvent solarFlareEvent;
    private final ElectricStormEvent electricStormEvent;
    private final SandStormEvent sandStormEvent;

    public CommandHandler(AcidRainEvent acidRainEvent, SolarFlareEvent solarFlareEvent, ElectricStormEvent electricStormEvent, SandStormEvent sandStormEvent) {
        this.acidRainEvent = acidRainEvent;
        this.solarFlareEvent = solarFlareEvent;
        this.electricStormEvent = electricStormEvent;
        this.sandStormEvent = sandStormEvent;
    }

    private void helpCommand(Player sender) {
        sender.sendMessage(prefix.append(Component.text("-----COMMANDS-----").color(NamedTextColor.RED)));
        sender.sendMessage(prefix.append(Component.text("/climateevents acidrain <start|stop>").color(NamedTextColor.RED)));
        sender.sendMessage(prefix.append(Component.text("/climateevents solarflare <start|stop>").color(NamedTextColor.RED)));
        sender.sendMessage(prefix.append(Component.text("/climateevents electricstorm <start|stop>").color(NamedTextColor.RED)));
        sender.sendMessage(prefix.append(Component.text("/climateevents sandstorm <start|stop>").color(NamedTextColor.RED)));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("climateevents.admin")) {
            sender.sendMessage(prefix.append(Component.text("No tienes permiso para usar este comando").color(NamedTextColor.RED)));
            return false;
        }

        if (args.length == 0) {
            helpCommand((Player) sender);
            return false;
        }

        if (args[0].equalsIgnoreCase("help")) {
            helpCommand((Player) sender);
            return false;
        }

        if (args[0].equalsIgnoreCase("acidrain")) {
            if (!sender.hasPermission("climateevents.admin.events")) {
                sender.sendMessage(prefix.append(Component.text("No tienes permiso para usar este comando").color(NamedTextColor.RED)));
                return false;
            }
            if (args.length == 1) {
                sender.sendMessage(prefix.append(Component.text("Usa /climateevents acidrain <start|stop>").color(NamedTextColor.RED)));
                return false;
            }

            if (args[1].equalsIgnoreCase("start")) {
                if (ClimateEvents.getInstance().isEventActive()) {
                    sender.sendMessage(prefix.append(Component.text("Ya hay un evento en curso").color(NamedTextColor.RED)));
                } else {
                    acidRainEvent.startAcidRain();
                    sender.sendMessage(prefix.append(Component.text("Lluvia ácida empezada").color(NamedTextColor.GREEN)));
                }
            } else if (args[1].equalsIgnoreCase("stop")) {
                if (!acidRainEvent.isAcidRainActive()) {
                    sender.sendMessage(prefix.append(Component.text("No hay lluvia ácida en curso").color(NamedTextColor.RED)));
                } else {
                    acidRainEvent.stopAcidRain();
                    sender.sendMessage(prefix.append(Component.text("Lluvia ácida terminada").color(NamedTextColor.GREEN)));
                }
            } else {
                sender.sendMessage(prefix.append(Component.text("Usa /climateevents acidrain <start|stop>").color(NamedTextColor.RED)));
            }
        } else if (args[0].equalsIgnoreCase("solarflare")) {
            if (!sender.hasPermission("climateevents.admin.events")) {
                sender.sendMessage(prefix.append(Component.text("No tienes permiso para usar este comando").color(NamedTextColor.RED)));
                return false;
            }

            if (args.length == 1) {
                sender.sendMessage(prefix.append(Component.text("Usa /climateevents solarflare <start|stop>").color(NamedTextColor.RED)));
                return false;
            }

            if (args[1].equalsIgnoreCase("start")) {
                if (ClimateEvents.getInstance().isEventActive()) {
                    sender.sendMessage(prefix.append(Component.text("Ya hay un evento en curso").color(NamedTextColor.RED)));
                } else {
                    solarFlareEvent.startSolarFlare();
                    sender.sendMessage(prefix.append(Component.text("Erupción solar empezada").color(NamedTextColor.GREEN)));
                }
            } else if (args[1].equalsIgnoreCase("stop")) {
                if (!solarFlareEvent.isSolarFlareActive()) {
                    sender.sendMessage(prefix.append(Component.text("No hay llamarada solar en curso").color(NamedTextColor.RED)));
                } else {
                    solarFlareEvent.stopSolarFlare();
                    sender.sendMessage(prefix.append(Component.text("Llamarada solar terminada").color(NamedTextColor.GREEN)));
                }
            } else {
                sender.sendMessage(prefix.append(Component.text("Usa /climateevents solarflare <start|stop>").color(NamedTextColor.RED)));
            }
        } else if (args[0].equalsIgnoreCase("electricstorm")) {
            if (!sender.hasPermission("climateevents.admin.events")) {
                sender.sendMessage(prefix.append(Component.text("No tienes permiso para usar este comando").color(NamedTextColor.RED)));
                return false;
            }

            if (args.length == 1) {
                sender.sendMessage(prefix.append(Component.text("Usa /climateevents electricstorm <start|stop>").color(NamedTextColor.RED)));
                return false;
            }

            if (args[1].equalsIgnoreCase("start")) {
                if (ClimateEvents.getInstance().isEventActive()) {
                    sender.sendMessage(prefix.append(Component.text("Ya hay un evento en curso").color(NamedTextColor.RED)));
                } else {
                    electricStormEvent.startElectricStorm();
                    sender.sendMessage(prefix.append(Component.text("Tormenta eléctrica empezada").color(NamedTextColor.GREEN)));
                }
            } else if (args[1].equalsIgnoreCase("stop")) {
                if (!electricStormEvent.isElectricStormActive()) {
                    sender.sendMessage(prefix.append(Component.text("No hay tormenta eléctrica en curso").color(NamedTextColor.RED)));
                } else {
                    electricStormEvent.stopElectricStorm();
                    sender.sendMessage(prefix.append(Component.text("Tormenta eléctrica terminada").color(NamedTextColor.GREEN)));
                }
            } else {
                sender.sendMessage(prefix.append(Component.text("Usa /climateevents electricstorm <start|stop>").color(NamedTextColor.RED)));
            }
        } else if (args[0].equalsIgnoreCase("sandstorm")) {
            if (!sender.hasPermission("climateevents.admin.events")) {
                sender.sendMessage(prefix.append(Component.text("No tienes permiso para usar este comando").color(NamedTextColor.RED)));
                return false;
            }

            if (args.length == 1) {
                sender.sendMessage(prefix.append(Component.text("Usa /climateevents sandstorm <start|stop>").color(NamedTextColor.RED)));
                return false;
            }

            if (args[1].equalsIgnoreCase("start")) {
                if (ClimateEvents.getInstance().isEventActive()) {
                    sender.sendMessage(prefix.append(Component.text("Ya hay un evento en curso").color(NamedTextColor.RED)));
                } else {
                    sandStormEvent.startSandStorm();
                    sender.sendMessage(prefix.append(Component.text("Tormenta de arena empezada").color(NamedTextColor.GREEN)));
                }
            } else if (args[1].equalsIgnoreCase("stop")) {
                if (!sandStormEvent.isSandStormActive()) {
                    sender.sendMessage(prefix.append(Component.text("No hay tormenta de arena en curso").color(NamedTextColor.RED)));
                } else {
                    sandStormEvent.stopSandStorm();
                    sender.sendMessage(prefix.append(Component.text("Tormenta de arena terminada").color(NamedTextColor.GREEN)));
                }
            } else {
                sender.sendMessage(prefix.append(Component.text("Usa /climateevents sandstorm <start|stop>").color(NamedTextColor.RED)));
            }
        } else {
            helpCommand((Player) sender);
        }
        return true;
    }
}
