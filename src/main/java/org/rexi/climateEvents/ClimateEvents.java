package org.rexi.climateEvents;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.rexi.climateEvents.events.AcidRainEvent;
import org.rexi.climateEvents.utils.CommandHandler;
import org.rexi.climateEvents.utils.TabCommandCompleter;

public final class ClimateEvents extends JavaPlugin {

public static final Component prefix = Component.empty()
        .append(Component.text("[").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD)
        .append(Component.text("ClimateEvents").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, false))
        .append(Component.text("] ").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD)));
private static ClimateEvents instance;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        instance = this;

        getCommand("climateevents").setTabCompleter(new TabCommandCompleter());

        AcidRainEvent acidRainEvent = new AcidRainEvent(this);
        getCommand("climateevents").setExecutor(new CommandHandler(acidRainEvent));
        Bukkit.getPluginManager().registerEvents(acidRainEvent, this);

        Bukkit.getConsoleSender().sendMessage(prefix
                .append(Component.text("El plugin ha sido activado").color(NamedTextColor.GREEN)));
        Bukkit.getConsoleSender().sendMessage(prefix
                .append(Component.text("Gracias por usar plugins de Rexi666 :D").color(NamedTextColor.DARK_BLUE)));

        Bukkit.getScheduler().runTaskTimer(this, acidRainEvent::startAcidRain, 240000L, 240000L);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(prefix
                .append(Component.text("El plugin ha sido desactivado").color(NamedTextColor.RED)));
        Bukkit.getConsoleSender().sendMessage(prefix
                .append(Component.text("Gracias por usar plugins de Rexi666 :D").color(NamedTextColor.DARK_BLUE)));
    }

    public static ClimateEvents getInstance() {
        return instance;
    }
}
