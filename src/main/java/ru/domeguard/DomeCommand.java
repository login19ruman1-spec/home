package ru.domeguard;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class DomeCommand implements CommandExecutor {
    private final DomeManager dome;
    private final DomeMenu menu;

    public DomeCommand(DomeManager dome, DomeMenu menu) {
        this.dome = dome;
        this.menu = menu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Команду может использовать только игрок.");
            return true;
        }
        if (!player.hasPermission("dome.admin")) {
            player.sendMessage("§cУ вас нет прав.");
            return true;
        }
        menu.open(player);
        return true;
    }
}
