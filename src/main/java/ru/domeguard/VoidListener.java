package ru.domeguard;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class VoidListener implements Listener {

    private final VoidManager voidManager;
    private final DomeGuardPlugin plugin;

    public VoidListener(
            DomeGuardPlugin plugin,
            VoidManager voidManager
    ) {
        this.plugin = plugin;
        this.voidManager = voidManager;
    }

    /*
     * Основной перехват портала.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPortal(PlayerPortalEvent event) {

        Player player = event.getPlayer();

        if (!voidManager.enabled()) {
            return;
        }

        Location from = event.getFrom();

        if (from == null || from.getWorld() == null) {
            return;
        }

        World world = from.getWorld();

        /*
         * ВЫХОД ЗА ВЕРХНЮЮ ГРАНИЦУ НЕЗЕРА
         */
        if (world.getEnvironment() == World.Environment.NETHER
                && isAboveNetherBoundary(from)) {

            event.setCancelled(true);

            voidManager.enter(
                    player,
                    from
            );

            return;
        }

        /*
         * Если игрок уже находится в DomeVoid,
         * стандартный портал не должен отправлять
         * его в обычный мир.
         */
        if (voidManager.isVoidWorld(world)) {

            event.setCancelled(true);

            voidManager.returnPlayer(player);
        }
    }

    /*
     * Дополнительный перехват.
     *
     * Именно этот обработчик нужен для случая,
     * когда Minecraft уже пытается выполнить
     * Nether -> Overworld teleport.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event) {

        if (event.getCause()
                != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            return;
        }

        Player player = event.getPlayer();

        Location from = event.getFrom();

        if (from == null || from.getWorld() == null) {
            return;
        }

        World world = from.getWorld();

        /*
         * Незер выше границы.
         */
        if (world.getEnvironment() == World.Environment.NETHER
                && isAboveNetherBoundary(from)) {

            /*
             * Полностью отменяем стандартный
             * Nether -> Overworld.
             */
            event.setCancelled(true);

            /*
             * Отправляем именно в нашу вселенную.
             */
            voidManager.enter(
                    player,
                    from
            );

            return;
        }

        /*
         * Если каким-то образом Minecraft пытается
         * обработать настоящий Nether Portal внутри DomeVoid,
         * не разрешаем ему телепортировать игрока.
         */
        if (voidManager.isVoidWorld(world)) {

            event.setCancelled(true);
        }
    }

    /*
     * Проверка верхней границы Незера.
     */
    private boolean isAboveNetherBoundary(
            Location location
    ) {

        double triggerY = plugin.getConfig()
                .getDouble(
                        "nether-void.trigger-y",
                        127.0
                );

        return location.getY() >= triggerY;
    }

    /*
     * Игрок находится в DomeVoid.
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (!voidManager.isVoidWorld(
                player.getWorld()
        )) {
            return;
        }

        voidManager.checkReturn(player);
    }

    /*
     * В DomeVoid игрок НИКОГДА не должен
     * получать урон от настоящего Void.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVoidDamage(
            EntityDamageEvent event
    ) {

        if (!(event.getEntity()
                instanceof Player player)) {
            return;
        }

        if (!voidManager.isVoidWorld(
                player.getWorld()
        )) {
            return;
        }

        if (event.getCause()
                == EntityDamageEvent.DamageCause.VOID) {

            event.setCancelled(true);

            player.setFallDistance(0);
        }
    }
}
