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

import java.util.List;

public final class DomeMenu implements Listener {
    public static final String TITLE = "§8☠ §5Настройка купола v1.3";

    private final DomeGuardPlugin plugin;
    private final DomeManager dome;

    public DomeMenu(DomeGuardPlugin plugin, DomeManager dome) {
        this.plugin = plugin;
        this.dome = dome;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, TITLE);

        inv.setItem(10, item(Material.COMPASS, "§bЦентр",
                "§7Установить центр на вашей позиции",
                "§fX: " + fmt(dome.getCenterX()),
                "§fZ: " + fmt(dome.getCenterZ()),
                "§8Мир: " + dome.getWorldName()));

        inv.setItem(11, item(Material.ENDER_PEARL, "§dРадиус по X",
                "§7ЛКМ: +10 блоков",
                "§7ПКМ: -10 блоков",
                "§fСейчас: " + fmt(dome.getRadiusX())));

        inv.setItem(12, item(Material.ENDER_EYE, "§5Радиус по Z",
                "§7ЛКМ: +10 блоков",
                "§7ПКМ: -10 блоков",
                "§fСейчас: " + fmt(dome.getRadiusZ())));

        inv.setItem(13, item(Material.SPYGLASS, "§aВерхняя граница Y",
                "§7ЛКМ: +10",
                "§7ПКМ: -10",
                "§fСейчас: " + fmt(dome.getMaxY())));

        inv.setItem(14, item(Material.BEDROCK, "§cНижняя граница Y",
                "§7ЛКМ: +10",
                "§7ПКМ: -10",
                "§fСейчас: " + fmt(dome.getMinY())));

        inv.setItem(16, item(Material.PAPER, "§eТекущие настройки",
                "§7Мир: §f" + dome.getWorldName(),
                "§7Центр: §f" + fmt(dome.getCenterX()) + ", " + fmt(dome.getCenterZ()),
                "§7Радиус X: §f" + fmt(dome.getRadiusX()),
                "§7Радиус Z: §f" + fmt(dome.getRadiusZ()),
                "§7Y: §f" + fmt(dome.getMinY()) + " ... " + fmt(dome.getMaxY())));

        inv.setItem(22, item(Material.WARDEN_SPAWN_EGG, "§3Эффект Вардена",
                "§7За границей эффекты нарастают постепенно:",
                "§f• 0–10: тошнота, замедление, слабость",
                "§f• 0–10: усталость от добычи",
                "§f• 10–30: Darkness + Blindness",
                "§f• звук сердца Вардена усиливается",
                "§f• 31 блок: смерть"));

        inv.setItem(31, item(Material.BARRIER, "§cЗакрыть"));
        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(TITLE)) return;

        event.setCancelled(true);

        if (!player.hasPermission("dome.admin")) {
            player.closeInventory();
            return;
        }

        switch (event.getRawSlot()) {
            case 10 -> {
                dome.setFromLocation(player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Центр купола установлен.");
            }
            case 11 -> {
                double delta = event.isLeftClick() ? 10.0 : -10.0;
                dome.setRadiusX(dome.getRadiusX() + delta);
            }
            case 12 -> {
                double delta = event.isLeftClick() ? 10.0 : -10.0;
                dome.setRadiusZ(dome.getRadiusZ() + delta);
            }
            case 13 -> {
                double delta = event.isLeftClick() ? 10.0 : -10.0;
                dome.setMaxY(dome.getMaxY() + delta);
            }
            case 14 -> {
                double delta = event.isLeftClick() ? 10.0 : -10.0;
                dome.setMinY(dome.getMinY() + delta);
            }
            case 31 -> {
                player.closeInventory();
                return;
            }
        }

        Bukkit.getScheduler().runTask(plugin, () -> open(player));
    }

    private ItemStack item(Material material, String name, String... lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    private String fmt(double value) {
        return String.format(java.util.Locale.US, "%.1f", value);
    }
}
