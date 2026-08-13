package io.github.alex123.domeguard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DomeGuard extends JavaPlugin {

    private VoidWorld voidWorld;

    @Override
    public void onEnable() {

        getLogger().info(
                "DomeGuard запускается..."
        );

        /*
         * Создаём отдельную вселенную Void.
         */

        voidWorld = new VoidWorld(this);

        voidWorld.create();

        /*
         * Создаём портал внутри Void.
         */

        voidWorld.createPortal();

        /*
         * Включаем обработчики событий.
         */

        Bukkit.getPluginManager().registerEvents(
                new VoidPortalListener(
                        this,
                        voidWorld
                ),
                this
        );

        getLogger().info(
                "DomeGuard успешно запущен!"
        );
    }

    @Override
    public void onDisable() {

        getLogger().info(
                "DomeGuard выключен."
        );
    }

    public VoidWorld getVoidWorld() {
        return voidWorld;
    }
}
