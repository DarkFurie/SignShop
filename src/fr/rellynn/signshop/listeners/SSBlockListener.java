package fr.rellynn.signshop.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import fr.rellynn.signshop.SignShopPlugin;
import fr.rellynn.signshop.type.SSObject;

public class SSBlockListener implements Listener
{
	private SignShopPlugin plugin;
	
	public SSBlockListener(SignShopPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@SuppressWarnings("unused")
	@EventHandler
	public void onSignChange(SignChangeEvent e)
	{
		Player player = e.getPlayer();
		Block block = e.getBlock();
		
		String[] lines = e.getLines();
		
		if (((lines[0].equalsIgnoreCase("[commerce]")) || (lines[0].equalsIgnoreCase("[acommerce]"))) && (lines[0].equalsIgnoreCase("[commerce]")) ? (plugin.permission.has(player, "signshop.create")) : (plugin.permission.has(player, "signshop.adminshop.create")))
		{
			if (lines[1].contains(":"))
			{
				String[] args = lines[1].split(":");
				
				if ((args.length == 2) && (plugin.um.isDouble(args[0])) && (plugin.um.isDouble(args[1])))
				{
					if (lines[2].contains(":"))
					{
						String[] args2 = lines[2].split(":");
						
						if ((args2.length == 2) && (plugin.um.isInteger(args2[0])) && (plugin.um.isInteger(args2[1])))
						{
							for (SSObject shop : plugin.shops)
							{
								if (shop.getFixLocation().equals(block.getRelative(((org.bukkit.material.Sign) block.getState().getData()).getAttachedFace()).getLocation()))
								{
									player.sendMessage(ChatColor.RED + "Vous ne pouvez pas poser deux magasins sur un même bloc.");
									block.breakNaturally();
									return;
								}
							}
							
							String owner = player.getName();
							String type  = lines[0].replace("[", "").replace("]", "");
							
							Location location = block.getLocation();
							
							Material material = Material.AIR;
							Byte data = 0;
							
							Double purchase = Double.parseDouble(args[0]);
							Double sale = Double.parseDouble(args[1]);
							
							Integer amount = Integer.parseInt(args2[0]);
							Integer packs = 0;
							Integer maximum = Integer.parseInt(args2[1]);
							
							SSObject shop = new SSObject(owner, type, location, material, data, purchase, sale, amount, packs, maximum, false, plugin);
							
							e.setLine(0, ChatColor.DARK_GRAY + "A/V par " + amount);
							e.setLine(1, ChatColor.GOLD + "Aucun item");
							
							if ((purchase > 0) && (sale > 0))
							{
								e.setLine(2, ChatColor.GREEN + "A: " + plugin.economy.format(purchase));
								e.setLine(3, ChatColor.DARK_RED + "V: " + plugin.economy.format(sale));
							}
							else if ((purchase == 0) && (sale > 0))
							{
								e.setLine(2, ChatColor.GREEN + "Pas d'achat");
								e.setLine(3, ChatColor.DARK_RED + "V: " + plugin.economy.format(sale));
							}
							else if ((purchase > 0) && (sale == 0))
							{
								e.setLine(2, ChatColor.GREEN + "A: " + plugin.economy.format(purchase));
								e.setLine(3, ChatColor.DARK_RED + "Pas de vente");
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Player player = e.getPlayer();
		
		if (e.getBlock().getState() instanceof Sign)
		{			
			Sign sign = (Sign) e.getBlock().getState();
			Location location = sign.getLocation();
			
			if (plugin.um.isShop(location))
			{
				SSObject shop = plugin.um.getShop(location);
				
				if ((shop.getOwner().equals(player.getName())) && (plugin.permission.has(player, "signshop.break.me")))
				{
					if (shop.getPacks() > 0)
					{
						shop.getLocation().getWorld().dropItem(shop.getLocation(), new ItemStack(shop.getMaterial(), shop.getAmount() * shop.getPacks(), shop.getData()));
					}
					
					player.sendMessage(ChatColor.GREEN + "Vous avez cassé votre magasin.");
					plugin.removeShop(shop);
				}
				else if (plugin.permission.has(player, "signshop.break.all"))
				{
					if (shop.getPacks() > 0)
					{
						shop.getLocation().getWorld().dropItem(shop.getLocation(), new ItemStack(shop.getMaterial(), shop.getAmount() * shop.getPacks(), shop.getData()));
					}
					
					player.sendMessage(ChatColor.GREEN + "Vous avez cassé le magasin de " + ChatColor.AQUA + shop.getOwner() + ChatColor.GREEN + ".");
					plugin.removeShop(shop);
				}
				else
				{
					player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission de casser le magasin de " + ChatColor.DARK_RED + shop.getOwner() + ChatColor.RED + ".");
					e.setCancelled(true);
					sign.update();
				}
			}
		}
		else
		{
			for (SSObject shop : plugin.shops) // Optimisation possible
			{
				if (shop.getFixLocation().getBlock().equals(e.getBlock()))
				{
					if (shop.getOwner().equals(player.getName()))
					{
						if (shop.getPacks() > 0)
						{
							shop.getLocation().getWorld().dropItem(shop.getLocation(), new ItemStack(shop.getMaterial(), shop.getAmount() * shop.getPacks(), shop.getData()));
						}
						
						player.sendMessage(ChatColor.GREEN + "Vous avez cassé votre magasin.");
						plugin.removeShop(shop);
					}
					else if (plugin.permission.has(player, "signshop.break.all"))
					{
						if (shop.getPacks() > 0)
						{
							shop.getLocation().getWorld().dropItem(shop.getLocation(), new ItemStack(shop.getMaterial(), shop.getAmount() * shop.getPacks(), shop.getData()));
						}
						
						player.sendMessage(ChatColor.GREEN + "Vous avez cassé le magasin de " + ChatColor.AQUA + shop.getOwner() + ChatColor.GREEN + ".");
						plugin.removeShop(shop);
					}
					else
					{
						player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission de casser le magasin de " + ChatColor.DARK_RED + shop.getOwner() + ChatColor.RED + ".");
						e.setCancelled(true);
					}
					
					break;
				}
			}
		}
	}
}
