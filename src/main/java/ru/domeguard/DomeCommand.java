package ru.domeguard;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.domeguard.managers.DomeManager;

public class DomeCommand implements CommandExecutor {
    
    private final DomeManager domeManager;
    private final DomeMenu domeMenu;
    
    public DomeCommand(DomeManager domeManager, DomeMenu domeMenu) {
        this.domeManager = domeManager;
        this.domeMenu = domeMenu;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("dome.admin")) {
            player.sendMessage("§cУ вас нет прав на использование этой команды!");
            return true;
        }
        
        if (args.length == 0) {
            domeMenu.openMainMenu(player);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("reload")) {
            player.sendMessage("§aПлагин перезагружен!");
            return true;
        }
        
        return false;
    }
}
