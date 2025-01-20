package org.rexi.climateEvents.events;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
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
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.rexi.climateEvents.ClimateEvents;

import static org.rexi.climateEvents.ClimateEvents.prefix;

public class AcidRainEvent implements Listener{

    private final JavaPlugin plugin;
    private BukkitTask acidRainTask;
    private static final long ACID_RAIN_DURATION = 6000L; // 5 minutos en ticks
    private BossBar acidRainBossBar;

    Component titulo = Component.text("¡Cuidado! Lluvia Ácida en curso").color(NamedTextColor.RED).decorate(TextDecoration.BOLD);
    String title = LegacyComponentSerializer.legacySection().serialize(titulo);

    String worldName = ClimateEvents.getInstance().getConfig().getString("world");
    World mundo = Bukkit.getWorld(worldName);

    public AcidRainEvent(JavaPlugin plugin) {
        this.plugin = plugin;
        acidRainBossBar = Bukkit.createBossBar(title, BarColor.GREEN, BarStyle.SEGMENTED_6);
    }

    public void startAcidRain() {
        if (ClimateEvents.getInstance().isEventActive()) {
            return; // La lluvia ácida ya está activa
        }

        // Iniciar la lluvia ácida
        World world = mundo;
        if (world == null) {
            plugin.getLogger().warning(prefix + "El mundo " + mundo + " no existe.");
            return;
        }
        world.setStorm(true);
        Bukkit.getConsoleSender().sendMessage(prefix.append(Component.text("La lluvia ácida ha comenzado.").color(NamedTextColor.YELLOW)));
        ClimateEvents.getInstance().eventActive = true;

        // Añadir jugadores a la BossBar
        for (Player player : world.getPlayers()) {
            acidRainBossBar.addPlayer(player);
            player.sendMessage(Component.text("¡La lluvia ácida ha empezado!").color(NamedTextColor.GREEN));
        }
        acidRainBossBar.setVisible(true);

        // Declarar una variable para llevar la cuenta del tiempo transcurrido
        long[] elapsedTicks = {0};

        // Tarea que aplica daño y efectos a los jugadores
        acidRainTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            elapsedTicks[0] += 20; // Incrementar por cada iteración
            updateBossBarProgress(elapsedTicks[0]);

            for (Player player : world.getPlayers()) {
                if (!isUnderCover(player) && !isInCreativeOrSpectator(player)) {
                    player.damage(1);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 30, 1));
                    player.sendMessage(Component.text("¡La lluvia ácida te está dañando!").color(NamedTextColor.RED));
                }
            }
        }, 0L, 20L);
        // Ejecutar cada 1 segundos (20 ticks)

        // Programar la detención de la lluvia ácida después de la duración especificada
        Bukkit.getScheduler().runTaskLater(plugin, this::stopAcidRain, ACID_RAIN_DURATION);
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
        double progress = 1.0 - (double) elapsedTicks / ACID_RAIN_DURATION;
        acidRainBossBar.setProgress(Math.max(0, Math.min(progress, 1.0)));
    }

    public void stopAcidRain() {
        if (acidRainTask != null && !acidRainTask.isCancelled()) {
            acidRainTask.cancel();
            acidRainTask = null;
            World world = mundo;
            if (world != null) {
                world.setStorm(false);
                ClimateEvents.getInstance().eventActive = false;
                Bukkit.getConsoleSender().sendMessage(prefix.append(Component.text("La lluvia ácida ha terminado.").color(NamedTextColor.YELLOW)));
                for (Player player : world.getPlayers()) {
                    player.sendMessage(prefix.append(Component.text("¡La lluvia ácida ha terminado!").color(NamedTextColor.GREEN)));
                    acidRainBossBar.removePlayer(player);
                }
            }
            acidRainBossBar.setVisible(false);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        acidRainBossBar.removePlayer(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isAcidRainActive()) {
            acidRainBossBar.addPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World destinationWorld = player.getWorld();
        if (isAcidRainActive()) {
            if (destinationWorld == mundo) {
                acidRainBossBar.addPlayer(player);
            } else {
                acidRainBossBar.removePlayer(player);
            }
        }
    }

    public boolean isAcidRainActive() {
        return acidRainTask != null && !acidRainTask.isCancelled();
    }
}
