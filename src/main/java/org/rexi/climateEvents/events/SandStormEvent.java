package org.rexi.climateEvents.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
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

public class SandStormEvent implements Listener {

    private final JavaPlugin plugin;
    private BukkitTask sandStormTask;
    private static final long event_duration = ClimateEvents.getInstance().getConfig().getLong("event_duration");
    private static final long SAND_STORM_DURATION = event_duration * 20L; // 5 minutos en ticks
    private BossBar sandStormBossBar;
    private Random random = new Random();

    Component titulo = Component.text("¡Cuidado! Tormenta de Arena en curso").color(NamedTextColor.RED).decorate(TextDecoration.BOLD);
    String title = LegacyComponentSerializer.legacySection().serialize(titulo);

    String worldName = ClimateEvents.getInstance().getConfig().getString("world");
    World mundo = Bukkit.getWorld(worldName);

    public SandStormEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        sandStormBossBar = Bukkit.createBossBar(title, BarColor.GREEN, BarStyle.SEGMENTED_6);
    }

    public void startSandStorm() {
        if (ClimateEvents.getInstance().isEventActive()) {
            return;
        }

        World world = mundo;
        if (world == null) {
            plugin.getLogger().warning(prefix + "El mundo" + mundo + "no existe");
        }
        world.setStorm(true);
        Bukkit.getConsoleSender().sendMessage(prefix.append(Component.text("La tormenta de arena ha comenzado").color(NamedTextColor.YELLOW)));
        ClimateEvents.getInstance().eventActive = true;

        for (Player player : world.getPlayers()) {
            sandStormBossBar.addPlayer(player);
            player.sendMessage(Component.text("¡La tormenta de arena ha empezado!").color(NamedTextColor.GREEN));
        }
        sandStormBossBar.setVisible(true);

        long[] elapsedTicks = {0};

        sandStormTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            elapsedTicks[0] += 20;
            updateBossBarProgress(elapsedTicks[0]);

            for (Player player : world.getPlayers()) {
                if (!isUnderCover(player) && !isInCreativeOrSpectator(player)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
                    generateSandParticles(player);
                    player.sendMessage(Component.text("¡La tormenta de arena te está afectando!").color(NamedTextColor.RED));
                }
            }
        }, 0L, 20L);
        Bukkit.getScheduler().runTaskLater(plugin, this::stopSandStorm, SAND_STORM_DURATION);
    }

    private boolean isUnderCover(Player player) {
        Location playerLocation = player.getLocation();
        int playerY = playerLocation.getBlockY();
        World world = player.getWorld();
        int highestY = world.getHighestBlockYAt(playerLocation.getBlockX(), playerLocation.getBlockZ());
        return highestY > playerY;
    }

    private boolean isInCreativeOrSpectator(Player player) {
        return player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR;
    }

    private void updateBossBarProgress(long elapsedTicks) {
        double progress = 1.0 - (double) elapsedTicks / SAND_STORM_DURATION;
        sandStormBossBar.setProgress(Math.max(0, Math.min(progress, 1.0)));
    }

    private void generateSandParticles(Player player) {
        Location playerLocation = player.getLocation();
        World world = player.getWorld();

        for (int i = 0; i < 20; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 2;
            double offsetY = random.nextDouble() * 2;
            double offsetZ = (random.nextDouble() - 0.5) * 2;

            Location particleLocation = playerLocation.clone().add(offsetX, offsetY, offsetZ);
            world.spawnParticle(Particle.BLOCK, particleLocation, 30, 0.5, 0.5, 0.5, 0, Material.SAND.createBlockData());
        }
    }

    public void stopSandStorm() {
        if (sandStormTask != null && !sandStormTask.isCancelled()) {
            sandStormTask.cancel();
            sandStormTask = null;
            World world = mundo;
            if (world != null) {
                ClimateEvents.getInstance().eventActive = false;
                world.setStorm(false);
                Bukkit.getConsoleSender().sendMessage(prefix.append(Component.text("La tormenta de arena ha terminado").color(NamedTextColor.YELLOW)));
                for (Player player : world.getPlayers()) {
                    player.sendMessage(prefix.append(Component.text("¡La tormenta de arena ha terminado!").color(NamedTextColor.GREEN)));
                    sandStormBossBar.removePlayer(player);
                }
            }
            sandStormBossBar.setVisible(false);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        sandStormBossBar.removePlayer(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isSandStormActive()) {
            sandStormBossBar.addPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World destinationWorld = player.getWorld();
        if (isSandStormActive()) {
            if (destinationWorld == mundo) {
                sandStormBossBar.addPlayer(player);
            } else {
                sandStormBossBar.removePlayer(player);
            }
        }
    }

    public boolean isSandStormActive() {
        return sandStormTask != null && !sandStormTask.isCancelled();
    }
}
