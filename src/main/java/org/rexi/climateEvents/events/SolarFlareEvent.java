package org.rexi.climateEvents.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.rexi.climateEvents.ClimateEvents;

import java.util.Random;
import static org.rexi.climateEvents.ClimateEvents.prefix;

public class SolarFlareEvent implements Listener {

    private final JavaPlugin plugin;
    private BukkitTask solarFlareTask;
    private static final long SOLAR_FLARE_DURATION = 6000L;
    private BossBar solarFlareBossBar;
    private final Random random = new Random();

    Component titulo = Component.text("¡Alerta! Erupción Solar en curso").color(NamedTextColor.RED).decorate(TextDecoration.BOLD);
    String title = LegacyComponentSerializer.legacySection().serialize(titulo);

    String worldName = ClimateEvents.getInstance().getConfig().getString("world");
    World mundo = Bukkit.getWorld(worldName);

    public SolarFlareEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        solarFlareBossBar = Bukkit.createBossBar(
                title,
                BarColor.RED,
                BarStyle.SEGMENTED_6
        );
    }

    public void startSolarFlare() {
        if (ClimateEvents.getInstance().isEventActive()) {
            return;
        }

        World world = mundo;
        if (world == null) {
            plugin.getLogger().warning(prefix + "El mundo " + mundo + " no existe.");
            return;
        }

        Bukkit.getConsoleSender().sendMessage(prefix.append(Component.text("La erupción solar ha comenzado").color(NamedTextColor.YELLOW)));
        ClimateEvents.getInstance().eventActive = true;

        for (Player player : world.getPlayers()) {
            solarFlareBossBar.addPlayer(player);
            player.sendMessage(Component.text("¡La erupción solar ha comenzado!").color(NamedTextColor.RED));
        }
        solarFlareBossBar.setVisible(true);

        long[] elapsedTicks = {0};

        solarFlareTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            elapsedTicks[0] += 20;
            updateBossBarProgress(elapsedTicks[0]);

            for (Player player : world.getPlayers()) {
                if (!isUnderCover(player) && !isInCreativeOrEspectator(player)) {
                    player.damage(1.5);
                    if (random.nextDouble() < 0.5) {
                        player.setFireTicks(20);
                        player.sendMessage(Component.text("¡Te estás quemando debido a la erupción solar!").color(NamedTextColor.RED));
                    }
                }
            }
        }, 0L, 20L);

        Bukkit.getScheduler().runTaskLater(plugin, this::stopSolarFlare, SOLAR_FLARE_DURATION);
    }

    private boolean isUnderCover(Player player) {
        Location playerLocation = player.getLocation();
        int playerY = playerLocation.getBlockY();
        World world = player.getWorld();
        int highestY = world.getHighestBlockYAt(playerLocation.getBlockX(), playerLocation.getBlockZ());
        return highestY > playerY;
    }

    private boolean isInCreativeOrEspectator(Player player) {
        return player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR;
    }

    private void updateBossBarProgress(long elapsedTicks) {
        double progress = 1.0 - (double) elapsedTicks / SOLAR_FLARE_DURATION;
        solarFlareBossBar.setProgress(Math.max(0, Math.min(progress, 1.0)));
    }

    public void stopSolarFlare() {
        if (solarFlareTask != null && !solarFlareTask.isCancelled()) {
            solarFlareTask.cancel();
            solarFlareTask = null;
            World world = mundo;
            if (world != null) {
                Bukkit.getConsoleSender().sendMessage(prefix.append(Component.text("La erupción solar ha terminado").color(NamedTextColor.YELLOW)));
                ClimateEvents.getInstance().eventActive = false;
                for (Player player : world.getPlayers()) {
                    player.sendMessage(Component.text("¡La erupción solar ha terminado!").color(NamedTextColor.GREEN));
                    solarFlareBossBar.removePlayer(player);
                }
            }
            solarFlareBossBar.setVisible(false);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isSolarFlareActive()) {
            solarFlareBossBar.removePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isSolarFlareActive()) {
            solarFlareBossBar.addPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World destinationWorld = player.getWorld();
        if (isSolarFlareActive()) {
            if (destinationWorld == mundo) {
                solarFlareBossBar.addPlayer(player);
            } else {
                solarFlareBossBar.removePlayer(player);
            }
        }
    }

    @EventHandler
    public boolean isSolarFlareActive() {
        return solarFlareTask != null && !solarFlareTask.isCancelled();
    }
}
