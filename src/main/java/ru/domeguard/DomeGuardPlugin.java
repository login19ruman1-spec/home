package ru.domeguard;

import org.bukkit.plugin.java.JavaPlugin;
import ru.domeguard.listeners.BoundaryListener;
import ru.domeguard.listeners.RespawnListener;
import ru.domeguard.listeners.BorderPortalListener; // НОВЫЙ импорт
import ru.domeguard.managers.Cursemanager;
import ru.domeguard.managers.DamageManager;
import ru.domeguard.managers.DomeManager;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import ru.domeguard.world.VoidWorldGenerator; // НОВЫЙ импорт

public class DomeGuardPlugin extends JavaPlugin {
    
    private Cursemanager curseManager;
    private DamageManager damageManager;
    private DomeManager domeManager;
    
    @Override
    public void onEnable() {
        // --- 1. Инициализация менеджеров ---
        curseManager = new Cursemanager(this);
        damageManager = new DamageManager(this);
        domeManager = new DomeManager(this);
        
        // --- 2. Регистрация команд ---
        getCommand("dome").setExecutor(new DomeCommand(this));
        
        // --- 3. РЕГИСТРАЦИЯ СЛУШАТЕЛЕЙ (ВАЖНО!) ---
        // Существующие слушатели
        getServer().getPluginManager().registerEvents(new BoundaryListener(this), this);
        getServer().getPluginManager().registerEvents(new RespawnListener(curseManager), this);
        
        // НОВЫЙ слушатель для портала на границе ада
        getServer().getPluginManager().registerEvents(new BorderPortalListener(), this);
        
        // --- 4. Создание мира Void (если его нет) ---
        createVoidWorld();
        
        getLogger().info("DomeGuard плагин успешно загружен!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("DomeGuard плагин выгружен.");
    }
    
    // Метод для создания мира Void
    private void createVoidWorld() {
        World voidWorld = getServer().getWorld("void_world");
        if (voidWorld == null) {
            getLogger().info("Создание мира Void...");
            WorldCreator creator = new WorldCreator("void_world");
            creator.environment(World.Environment.NORMAL);
            creator.generator(new VoidWorldGenerator());
            creator.createWorld();
            getLogger().info("Мир Void создан!");
        } else {
            getLogger().info("Мир Void уже существует.");
        }
    }
}
