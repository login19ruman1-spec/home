package ru.domeguard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DomeMenu implements Listener {
    public static final String TITLE = "§8☠ §5Настройка купола v1.4";
    private static final String PLAYERS_TITLE = "§8👤 §5Иммунитет к урону";

    private final DomeGuardPlugin plugin;
    private final DomeManager dome;
    private final DamageManager damage;

    public DomeMenu(DomeGuardPlugin plugin, DomeManager dome, DamageManager damage) {
        this.plugin = plugin;
        this.dome = dome;
        this.damage = damage;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, TITLE);
        inv.setItem(10, item(Material.COMPASS, "§bЦентр", "§7Установить центр на вашей позиции", "§fX: " + fmt(dome.getCenterX()), "§fZ: " + fmt(dome.getCenterZ()), "§8Мир: " + dome.getWorldName()));
        inv.setItem(11, item(Material.ENDER_PEARL, "§dРадиус по X", "§7ЛКМ: +10", "§7ПКМ: -10", "§fСейчас: " + fmt(dome.getRadiusX())));
        inv.setItem(12, item(Material.ENDER_EYE, "§5Радиус по Z", "§7ЛКМ: +10", "§7ПКМ: -10", "§fСейчас: " + fmt(dome.getRadiusZ())));
        inv.setItem(13, item(Material.SPYGLASS, "§aВерхняя граница Y", "§7ЛКМ: +10", "§7ПКМ: -10", "§fСейчас: " + fmt(dome.getMaxY())));
        inv.setItem(14, item(Material.BEDROCK, "§cНижняя граница Y", "§7ЛКМ: +10", "§7ПКМ: -10", "§fСейчас: " + fmt(dome.getMinY())));
        inv.setItem(16, item(Material.PAPER, "§eТекущие настройки", "§7Мир: §f" + dome.getWorldName(), "§7Центр: §f" + fmt(dome.getCenterX()) + ", " + fmt(dome.getCenterZ()), "§7Радиус X: §f" + fmt(dome.getRadiusX()), "§7Радиус Z: §f" + fmt(dome.getRadiusZ()), "§7Y: §f" + fmt(dome.getMinY()) + " ... " + fmt(dome.getMaxY())));
        inv.setItem(21, item(Material.PLAYER_HEAD, "§bИммунитет к урону", "§7Выбрать игрока, который не получает", "§7урон от границы, но получает все эффекты.", "§fИммунных: " + damage.getDamageImmuneCount()));
        inv.setItem(22, item(Material.WARDEN_SPAWN_EGG, "§3Зона ужаса", "§7Вне границы эффекты усиливаются плавно.", "§f0–10: базовые эффекты", "§f10–50: тьма и Варден", "§f51: смерть + проклятие сна"));
        inv.setItem(23, item(Material.SUSPICIOUS_STEW, "§5Проклятие сна", "§7Вышедшие за смертельную границу", "§7не могут спать, пока не съедят", "§fзагадочное рагу (Suspicious Stew)."));
        inv.setItem(31, item(Material.BARRIER, "§cЗакрыть"));
        player.openInventory(inv);
    }

    private void openPlayers(Player admin) {
        Inventory inv = Bukkit.createInventory(null, 54, PLAYERS_TITLE);
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (int i = 0; i < Math.min(players.size(), 45); i++) {
            Player target = players.get(i);
            boolean immune = damage.isDamageImmune(target.getUniqueId());
            inv.setItem(i, head(target, immune));
        }
        inv.setItem(49, item(Material.ARROW, "§eНазад"));
        admin.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (!title.equals(TITLE) && !title.equals(PLAYERS_TITLE)) return;
        event.setCancelled(true);
        if (!player.hasPermission("dome.admin")) { player.closeInventory(); return; }

        if (title.equals(PLAYERS_TITLE)) {
            if (event.getRawSlot() == 49) { open(player); return; }
            if (event.getRawSlot() >= 0 && event.getRawSlot() < 45) {
                ItemStack clicked = event.getCurrentItem();
                if (clicked != null && clicked.getType() == Material.PLAYER_HEAD && clicked.getItemMeta() != null) {
                    String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                    Player target = Bukkit.getPlayerExact(name);
                    if (target != null) {
                        damage.toggleDamageImmunity(target.getUniqueId());
                        player.sendMessage("§aИммунитет к урону для §f" + target.getName() + "§a: " + (damage.isDamageImmune(target.getUniqueId()) ? "§2ВКЛ" : "§cВЫКЛ"));
                    }
                }
            }
            Bukkit.getScheduler().runTask(plugin, () -> openPlayers(player));
            return;
        }

        switch (event.getRawSlot()) {
            case 10 -> { dome.setFromLocation(player.getLocation()); player.sendMessage(ChatColor.GREEN + "Центр купола установлен."); }
            case 11 -> dome.setRadiusX(dome.getRadiusX() + (event.isLeftClick() ? 10 : -10));
            case 12 -> dome.setRadiusZ(dome.getRadiusZ() + (event.isLeftClick() ? 10 : -10));
            case 13 -> dome.setMaxY(dome.getMaxY() + (event.isLeftClick() ? 10 : -10));
            case 14 -> dome.setMinY(dome.getMinY() + (event.isLeftClick() ? 10 : -10));
            case 21 -> { openPlayers(player); return; }
            case 31 -> { player.closeInventory(); return; }
        }
        Bukkit.getScheduler().runTask(plugin, () -> open(player));
    }

    private ItemStack head(Player player, boolean immune) {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        var meta = (org.bukkit.inventory.meta.SkullMeta) stack.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName((immune ? "§a✓ " : "§c✗ ") + player.getName());
        meta.setLore(List.of(immune ? "§aУрон границы: НЕТ" : "§cУрон границы: ДА", "§7Нажми, чтобы переключить"));
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack item(Material material, String name, String... lore) {
        ItemStack stack = new ItemStack(material); ItemMeta meta = stack.getItemMeta(); meta.setDisplayName(name); meta.setLore(List.of(lore)); stack.setItemMeta(meta); return stack;
    }
    private String fmt(double value) { return String.format(java.util.Locale.US, "%.1f", value); }
}
