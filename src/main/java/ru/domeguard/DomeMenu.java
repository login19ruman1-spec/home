package ru.domeguard;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.domeguard.managers.DomeManager;
import ru.domeguard.managers.DamageManager;

import java.util.Arrays;

public class DomeMenu {
    
    private final DomeGuardPlugin plugin;
    private final DomeManager domeManager;
    private final DamageManager damageManager;
    
    public DomeMenu(DomeGuardPlugin plugin, DomeManager domeManager, DamageManager damageManager) {
        this.plugin = plugin;
        this.domeManager = domeManager;
        this.damageManager = damageManager;
    }
    
    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8Управление куполом");
        
        // Информация о куполе
        ItemStack info = new ItemStack(Material.COMPASS);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§aИнформация о куполе");
        infoMeta.setLore(Arrays.asList(
            "§7Радиус купола: §f50 блоков",
            "§7Смертельная зона: §c51+ блоков",
            "§7Игроков в куполе: §f" + getPlayersInDome()
        ));
        info.setItemMeta(infoMeta);
        inv.setItem(11, info);
        
        // Кнопка иммунитета
        ItemStack immunity = new ItemStack(Material.SHIELD);
        ItemMeta immunityMeta = immunity.getItemMeta();
        immunityMeta.setDisplayName("§6Иммунитет к урону");
        immunityMeta.setLore(Arrays.asList("§7Нажмите, чтобы выбрать игрока"));
        immunity.setItemMeta(immunityMeta);
        inv.setItem(13, immunity);
        
        // Кнопка выхода
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName("§cЗакрыть");
        close.setItemMeta(closeMeta);
        inv.setItem(26, close);
        
        player.openInventory(inv);
    }
    
    private int getPlayersInDome() {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (domeManager.isInsideDome(player)) {
                count++;
            }
        }
        return count;
    }
}
