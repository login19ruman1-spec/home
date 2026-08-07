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
    public static final String TITLE = "§8☠ §5Настройка купола";

    private final DomeGuardPlugin plugin;
    private final DomeManager dome;

    public DomeMenu(DomeGuardPlugin plugin, DomeManager dome) {
        this.plugin = plugin;
        this.dome = dome;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        inv.setItem(10, item(Material.COMPASS, "§bЦентр",
                "§7Установить центр на вашей позиции",
                "§fX: " + fmt(dome.getCenterX()),
                "§fZ: " + fmt(dome.getCenterZ())));

        inv.setItem(11, item(Material.ENDER_EYE, "§dРадиус",
                "§7ЛКМ: +10",
                "§7ПКМ: -10",
                "§fСейчас: " + fmt(dome.getRadius())));

        inv.setItem(12, item(Material.SPYGLASS, "§aВерхняя граница",
                "§7ЛКМ: +10",
                "§7ПКМ: -10",
                "§fСейчас: " + fmt(dome.getMaxY())));

        inv.setItem(13, item(Material.BEDROCK, "§cНижняя граница",
                "§7ЛКМ: +10",
                "§7ПКМ: -10",
                "§fСейчас: " + fmt(dome.getMinY())));

        inv.setItem(15, item(Material.PAPER, "§eТекущие настройки",
                "§7Мир: §f" + dome.getWorldName(),
                "§7Центр: §f" + fmt(dome.getCenterX()) + ", " + fmt(dome.getCenterZ()),
                "§7Радиус: §f" + fmt(dome.getRadius()),
                "§7Y: §f" + fmt(dome.getMinY()) + " ... " + fmt(dome.getMaxY())));

        inv.setItem(22, item(Material.BARRIER, "§cЗакрыть"));
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
                dome.setRadius(dome.getRadius() + delta);
            }
            case 12 -> {
                double delta = event.isLeftClick() ? 10.0 : -10.0;
                dome.setMaxY(dome.getMaxY() + delta);
            }
            case 13 -> {
                double delta = event.isLeftClick() ? 10.0 : -10.0;
                dome.setMinY(dome.getMinY() + delta);
            }
            case 22 -> {
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
