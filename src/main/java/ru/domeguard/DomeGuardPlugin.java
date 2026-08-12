
package ru.domeguard;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import ru.domeguard.managers.CurseManager;
import ru.domeguard.managers.DamageManager;
import ru.domeguard.managers.DomeManager;
import ru.domeguard.VoidWorldGenerator;

public class DomeGuardPlugin extends JavaPlugin {
    
    private CurseManager curseManager;
    private DamageManager damageManager;
    private DomeManager domeManager;
    
    @Override
    public void onEnable() {
        // --- 1. Инициализация менеджеров ---
        curseManager = new CurseManager(this);
        domeManager = new DomeManager(this);
        damageManager = new DamageManager(this, domeManager, curseManager);
        
        // --- 2. Регистрация команд ---
        getCommand("dome").setExecutor(new DomeCommand(domeManager, new DomeMenu(this)));
        
        // --- 3. Регистрация слушателей ---
        getServer().getPluginManager().registerEvents(new BoundaryListener(this, curseManager), this);
        getServer().getPluginManager().registerEvents(new RespawnListener(curseManager), this);
        getServer().getPluginManager().registerEvents(new BorderPortalListener(), this);
        
        // --- 4. Создание мира Void ---
        createVoidWorld();
        
        getLogger().info("DomeGuard плагин успешно загружен!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("DomeGuard плагин выгружен.");
    }
    
    private void createVoidWorld() {
        World voidWorld = getServer().getWorld("void_world");
        if (voidWorld == null) {
            getLogger().info("Создание мира Void...");
            WorldCreator creator = new WorldCreator("void_world");
            creator.environment(World.Environment.NORMAL);
            creator.generator(new VoidWorldGenerator());
            creator.createWorld();
            getLogger().info("Мир Void создан!");
        }
    }
}
