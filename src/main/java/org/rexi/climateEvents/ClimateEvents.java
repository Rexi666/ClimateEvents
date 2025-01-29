package org.rexi.climateEvents;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.rexi.climateEvents.events.AcidRainEvent;
import org.rexi.climateEvents.events.ElectricStormEvent;
import org.rexi.climateEvents.events.SandStormEvent;
import org.rexi.climateEvents.events.SolarFlareEvent;
import org.rexi.climateEvents.utils.CommandHandler;
import org.rexi.climateEvents.utils.TabCommandCompleter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public final class ClimateEvents extends JavaPlugin {

    public static final Component prefix = Component.empty()
            .append(Component.text("[").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD)
            .append(Component.text("ClimateEvents").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, false))
            .append(Component.text("] ").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD)));
    private static ClimateEvents instance;

    private final Random random = new Random();
    public boolean eventActive = false;
    private int DIAS_ENTRE_EVENTOS;
    private long diasHastaProximoEvento;

    @Override
    public void onEnable() {
        loadConfig();

        instance = this;

        DIAS_ENTRE_EVENTOS = getConfig().getInt("event_days");
        diasHastaProximoEvento = DIAS_ENTRE_EVENTOS;

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

        Bukkit.getScheduler().runTaskTimer(this, this::notificarDiasRestantes, 0L, 24000L);
    }

    private void notificarDiasRestantes() {
        if (diasHastaProximoEvento > 0) {
            diasHastaProximoEvento--;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(Component.text("------------------------------------").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD));
                player.sendMessage(Component.text("Faltan" + diasHastaProximoEvento + "días para el próximo evento climático").color(NamedTextColor.DARK_PURPLE));
                player.sendMessage(Component.text("------------------------------------").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD));
            }
        } else {
            // Iniciar evento y reiniciar contador
            triggerRandomEvent();
            diasHastaProximoEvento = DIAS_ENTRE_EVENTOS;
        }
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

    public void loadConfig() {
        saveDefaultConfig();
        int configVersion = getConfig().getInt("configVersion", 1);
        if (configVersion < 2) {
            // Realiza las actualizaciones necesarias
            getConfig().set("event_duration", 300);
            getConfig().set("event_days", 10);
            saveConfig();
            getConfig().set("configVersion", 2);
            saveConfig();
        }
    }

    private void triggerRandomEvent() {
        if (!isEventActive()) {
            int eventIndex = random.nextInt(4);
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
