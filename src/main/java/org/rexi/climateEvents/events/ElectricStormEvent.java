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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.rexi.climateEvents.ClimateEvents;

import java.util.Random;

import static org.rexi.climateEvents.ClimateEvents.prefix;

public class ElectricStormEvent implements Listener {

    private final JavaPlugin plugin;
    private BukkitTask electricStormTask;
    private static final long event_duration = ClimateEvents.getInstance().getConfig().getLong("event_duration");
    private static final long ELECTRIC_STORM_DURATION = event_duration * 20L; // 5 minutos en ticks
    private static final double LIGHTNING_PROBABILITY = 0.1;
    private BossBar electricStormBossBar;
    private final Random random = new Random();

    Component titulo = Component.text("¡Alerta! Tormenta Eléctrica en curso").color(NamedTextColor.RED).decorate(TextDecoration.BOLD);
    String title = LegacyComponentSerializer.legacySection().serialize(titulo);

    String worldName = ClimateEvents.getInstance().getConfig().getString("world");
    World mundo = Bukkit.getWorld(worldName);

    public ElectricStormEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        electricStormBossBar = Bukkit.createBossBar(title, BarColor.RED, BarStyle.SEGMENTED_6);
    }

    public void startElectricStorm() {
        if (ClimateEvents.getInstance().isEventActive()) {
            return;
        }

        World world = mundo;
        if (world == null) {
            plugin.getLogger().warning(prefix + "El mundo " + mundo + " no existe.");
            return;
        }

        Bukkit.getConsoleSender().sendMessage(prefix.append(Component.text("La tormenta eléctrica ha comenzado").color(NamedTextColor.YELLOW)));
        ClimateEvents.getInstance().eventActive = true;

        for (Player player : world.getPlayers()) {
            electricStormBossBar.addPlayer(player);
            player.sendMessage(Component.text("¡La tormenta eléctrica ha comenzado!").color(NamedTextColor.RED));
        }
        electricStormBossBar.setVisible(true);

        world.setStorm(true);
        world.setThundering(true);

        long[] elapsedTicks = {0};

        electricStormTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            elapsedTicks[0] += 20;
            updateBossBarProgress(elapsedTicks[0]);

            for (Player player : world.getPlayers()) {
                if (!isUnderCover(player) && !isInCreativeOrEspectator(player)) {
                    if (random.nextDouble() < LIGHTNING_PROBABILITY) {
                        Location playerLocation = player.getLocation();
                        Location strikeLocation = playerLocation.add(random.nextInt(10) - 5, 0, random.nextInt(10) - 5);
                        world.strikeLightning(strikeLocation);
                    }
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
                }
            }
        }, 0L, 100L);

        Bukkit.getScheduler().runTaskLater(plugin, this::stopElectricStorm, ELECTRIC_STORM_DURATION);
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
        double progress = 1.0 - (double) elapsedTicks / ELECTRIC_STORM_DURATION;
        electricStormBossBar.setProgress(Math.max(0, Math.min(progress, 1.0)));
    }

    public void stopElectricStorm() {
        if (electricStormTask != null && !electricStormTask.isCancelled()) {
            electricStormTask.cancel();
            electricStormTask = null;
            World world = mundo;
            if (world != null) {
                Bukkit.getConsoleSender().sendMessage(prefix.append(Component.text("La tormenta eléctrica ha terminado").color(NamedTextColor.YELLOW)));
                ClimateEvents.getInstance().eventActive = false;
                world.setStorm(false);
                world.setThundering(false);
                for (Player player : world.getPlayers()) {
                    player.sendMessage(Component.text("¡La tormenta eléctrica ha terminado!").color(NamedTextColor.GREEN));
                    electricStormBossBar.removePlayer(player);
                }
            }
            electricStormBossBar.setVisible(false);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isElectricStormActive()) {
            electricStormBossBar.removePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isElectricStormActive()) {
            electricStormBossBar.addPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World destinationWorld = player.getWorld();
        if (isElectricStormActive()) {
            if (destinationWorld == mundo) {
                electricStormBossBar.addPlayer(player);
            } else {
                electricStormBossBar.removePlayer(player);
            }
        }
    }

    @EventHandler
    public boolean isElectricStormActive() {
        return electricStormTask != null && !electricStormTask.isCancelled();
    }
}
