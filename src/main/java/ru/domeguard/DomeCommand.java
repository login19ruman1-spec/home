package ru.domeguard;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class DomeCommand implements CommandExecutor {
    private final DomeManager dome;

    public DomeCommand(DomeManager dome) {
        this.dome = dome;
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

        new DomeMenu(dome.getPlugin(), dome).open(player);
        return true;
    }
}
