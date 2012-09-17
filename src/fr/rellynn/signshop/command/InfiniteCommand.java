package fr.rellynn.signshop.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.rellynn.signshop.SignShopPlugin;

public class InfiniteCommand implements CommandExecutor
{
	private SignShopPlugin plugin;
	
	public InfiniteCommand(SignShopPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (isPlayer(sender))
		{
			Player player = (Player) sender;
			
			if (label.equalsIgnoreCase("shop"))
			{
				if ((args.length >= 1) && (args[0].equalsIgnoreCase("infinite")) && ((player.hasPermission("signshop.infinite")) || (player.hasPermission("signshop.commands.*"))))
				{
					if (!plugin.infinitePlayers.contains(player))
					{
						plugin.infinitePlayers.add(player);
						player.sendMessage(ChatColor.GREEN + "Vous venez de passer en mode " + ChatColor.AQUA + "\"infinite\"" + ChatColor.GREEN + ".");
					}
					else
					{
						plugin.infinitePlayers.remove(player);
						player.sendMessage(ChatColor.RED + "Vous n'êtes plus en mode " + ChatColor.DARK_RED + "\"infinite\"" + ChatColor.RED + ".");
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isPlayer(CommandSender sender)
	{
		return sender instanceof Player;
	}
}