package org.rexi.climateEvents;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.rexi.climateEvents.events.AcidRainEvent;
import org.rexi.climateEvents.events.ElectricStormEvent;
import org.rexi.climateEvents.events.SandStormEvent;
import org.rexi.climateEvents.events.SolarFlareEvent;
import org.rexi.climateEvents.utils.CommandHandler;
import org.rexi.climateEvents.utils.TabCommandCompleter;

import java.util.Random;

public final class ClimateEvents extends JavaPlugin {

    public static final Component prefix = Component.empty()
            .append(Component.text("[").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD)
            .append(Component.text("ClimateEvents").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, false))
            .append(Component.text("] ").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD)));
    private static ClimateEvents instance;

    private final Random random = new Random();
    public boolean eventActive = false;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        instance = this;

        getCommand("climateevents").setTabCompleter(new TabCommandCompleter());

        AcidRainEvent acidRainEvent = new AcidRainEvent(this);
        SolarFlareEvent solarFlareEvent = new SolarFlareEvent(this);
        ElectricStormEvent electricStormEvent = new ElectricStormEvent(this);
        SandStormEvent sandStormEvent = new SandStormEvent(this);
        getCommand("climateevents").setExecutor(new CommandHandler(acidRainEvent, solarFlareEvent, electricStormEvent, sandStormEvent));
        Bukkit.getPluginManager().registerEvents(acidRainEvent, this);
        Bukkit.getPluginManager().registerEvents(solarFlareEvent, this);
        Bukkit.getPluginManager().registerEvents(electricStormEvent, this);
        Bukkit.getPluginManager().registerEvents(sandStormEvent, this);

        Bukkit.getConsoleSender().sendMessage(prefix
                .append(Component.text("El plugin ha sido activado").color(NamedTextColor.GREEN)));
        Bukkit.getConsoleSender().sendMessage(prefix
                .append(Component.text("Gracias por usar plugins de Rexi666 :D").color(NamedTextColor.DARK_BLUE)));

        Bukkit.getScheduler().runTaskTimer(this, this::triggerRandomEvent, 240000L, 240000L);
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

    public boolean isEventActive() {
        return eventActive;
    }

    private void triggerRandomEvent() {
        if (!isEventActive()) {
            int eventIndex = random.nextInt(4); // Genera un número aleatorio entre 0 y 2
            switch (eventIndex) {
                case 0:
                    new AcidRainEvent(this).startAcidRain();
                    break;
                case 1:
                    new SolarFlareEvent(this).startSolarFlare();
                    break;
                case 2:
                    new ElectricStormEvent(this).startElectricStorm();
                    break;
                case 3:
                    new SandStormEvent(this).startSandStorm();
                    break;
            }
            eventActive = true;
        } else {
            Bukkit.getConsoleSender().sendMessage(prefix.append(Component.text("Se ha intentado iniciar un evento pero ya había uno activo").color(NamedTextColor.RED)));
        }
    }

}
