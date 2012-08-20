package fr.rellynn.signshop.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.rellynn.signshop.SignShopPlugin;
import fr.rellynn.signshop.type.SSObject;

public class SSPlayerListener implements Listener
{
	private SignShopPlugin plugin;
	
	public SSPlayerListener(SignShopPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if ((e.getClickedBlock() != null) && (e.getClickedBlock().getState() instanceof Sign))
		{
			Sign sign = (Sign) e.getClickedBlock().getState();
			Location location = sign.getLocation();
			
			if (plugin.um.isShop(location))
			{
				Player player = e.getPlayer();
				PlayerInventory playerinventory = player.getInventory();
				
				SSObject shop = plugin.um.getShop(location);
				
				if (e.getAction() == Action.LEFT_CLICK_BLOCK)
				{
					Material material = playerinventory.getItemInHand().getType();
					Byte data = playerinventory.getItemInHand().getData().getData();
					Integer amount = playerinventory.getItemInHand().getAmount();
					
					if (shop.getOwner().equals(player.getName()))
					{
						if (shop.getMaterial() == Material.AIR)
						{
							if (playerinventory.getItemInHand().getType() != Material.AIR)
							{
								shop.defineItem(material, data);
								player.sendMessage(ChatColor.GREEN + "Vous avez définis l'item " + ChatColor.AQUA + material.name() + ChatColor.GREEN + " en tant qu'objet de vente.");
							
								plugin.um.update(shop, ChatColor.GOLD + material.name(), 1);
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Vous ne pouvez pas définir l'item " + ChatColor.DARK_RED + "AIR" + ChatColor.RED + " en tant qu'objet de vente.");
							}
						}
						else
						{
							if ((material == shop.getMaterial()) && (data == shop.getData()) && (amount >= shop.getAmount()))
							{
								if (shop.getMaximum() >= shop.getAmount() * shop.getPacks())
								{
									shop.addItems();
									
									plugin.um.setItemInHand(playerinventory, material, amount - shop.getAmount(), data);
									player.updateInventory();
									
									player.sendMessage(ChatColor.GREEN + "Vous avez ajouté " + ChatColor.AQUA + shop.getAmount() + " " + material.name() + ChatColor.GREEN + " à votre magasin.");
									
									if ((shop.getPacks() > 0) && (sign.getLine(0).startsWith(ChatColor.DARK_GRAY.toString())))
									{
										plugin.um.update(shop, ChatColor.AQUA + "A/V par " + shop.getAmount(), 0);
									}
								}
								else
								{
									player.sendMessage(ChatColor.RED + "Votre magasin est plein.");
								}
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Vous devez avoir " + ChatColor.DARK_RED + shop.getAmount() + " " + shop.getMaterial().name() + ChatColor.RED + " dans votre main pour remplir votre magasin.");
							}
						}
					}
					else
					{
						if (shop.getSale() > 0)
						{
							if (plugin.permission.has(player, "signshop.transactions"))
							{
								if ((material == shop.getMaterial()) && (data == shop.getData()) && (amount >= shop.getAmount()))
								{
									if (shop.getMaximum() >= shop.getAmount() * shop.getPacks())
									{
										if (plugin.economy.has(shop.getOwner(), shop.getSale()))
										{
											World world = location.getWorld();
											Double x = location.getX();
											Double y = location.getY();
											Double z = location.getZ();
											
											if (plugin.confirm.contains("sale_" + player.getName() + "_" + world.getName() + "_" + x + "_" + y + "_" + z))
											{
												shop.addItems();
												
												plugin.economy.withdrawPlayer(shop.getOwner(), shop.getSale());
												plugin.economy.depositPlayer(player.getName(), shop.getSale());
												
												plugin.um.setItemInHand(playerinventory, material, amount - shop.getAmount(), data);
												player.updateInventory();
												
												player.sendMessage(ChatColor.GREEN + "Vous avez vendu " + ChatColor.AQUA + shop.getAmount() + " " + material.name() + ChatColor.GREEN + " contre " + ChatColor.AQUA + shop.getSale() + ChatColor.GREEN + " à ce magasin.");
												
												if ((shop.getPacks() > 0) && (sign.getLine(0).startsWith(ChatColor.DARK_GRAY.toString())))
												{
													plugin.um.update(shop, ChatColor.AQUA + "A/V par " + shop.getAmount(), 0);
												}
											}
											else
											{
												plugin.confirm.add("sale_" + player.getName() + "_" + world.getName() + "_" + x + "_" + y + "_" + z);
												player.sendMessage(ChatColor.GREEN + "Vous êtes sur le point de vendre " + ChatColor.AQUA + shop.getAmount() + " " + shop.getMaterial() + ChatColor.GREEN + " en échange de " + ChatColor.AQUA + plugin.economy.format(shop.getSale()) + ChatColor.GREEN + ". Pour confirmer cette vente, faites à nouveau un clic-droit sur le panneau.");
											}
										}
										else
										{
											player.sendMessage(ChatColor.RED + "Le créateur de ce magasin n'a pas assez de sous pour cette vente.");
										}
									}
									else
									{
										player.sendMessage(ChatColor.RED + "Ce magasin est plein.");
									}
								}
								else
								{
									player.sendMessage(ChatColor.RED + "Vous devez avoir " + ChatColor.DARK_RED + shop.getAmount() + " " + shop.getMaterial().name() + ChatColor.RED + " dans votre main pour les vendre à ce magasin.");
								}
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission de vendre.");
							}
						}
						else
						{
							player.sendMessage(ChatColor.RED + "Ce magasin n'achète pas de ressources.");
						}
					}
				}
				else if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
				{
					e.setCancelled(true);
					// --------------- -- //
					Material material = shop.getMaterial();
					Byte data = shop.getData();
					Integer amount = shop.getAmount();
					
					if (shop.getOwner().equals(player.getName()))
					{
						if ((material != Material.AIR) && (shop.getPacks() > 0))
						{
							shop.subtractItems();
							
							playerinventory.addItem(new ItemStack(material, amount, data));
							player.updateInventory();
							
							player.sendMessage(ChatColor.GREEN + "Vous avez retiré " + ChatColor.AQUA + amount + " " + material.name() + ChatColor.GREEN + " à votre magasin.");
							
							if ((shop.getPacks() == 0) && (sign.getLine(0).startsWith(ChatColor.AQUA.toString())))
							{
								plugin.um.update(shop, ChatColor.DARK_GRAY + "A/V par " + shop.getAmount(), 0);
							}
						}
						else
						{
							player.sendMessage(ChatColor.RED + "Votre magasin est vide.");
						}
					}
					else
					{
						if (shop.getPurchase() > 0)
						{
							if (plugin.permission.has(player, "signshop.transactions"))
							{
								if ((material != Material.AIR) && (shop.getPacks() > 0))
								{
									if (plugin.economy.has(player.getName(), shop.getPurchase()))
									{
										World world = location.getWorld();
										Double x = location.getX();
										Double y = location.getY();
										Double z = location.getZ();
										
										if (plugin.confirm.contains("purchase_" + player.getName() + "_" + world.getName() + "_" + x + "_" + y + "_" + z))
										{											
											shop.subtractItems();
											
											plugin.economy.depositPlayer(shop.getOwner(), shop.getPurchase());
											plugin.economy.withdrawPlayer(player.getName(), shop.getPurchase());
											
											playerinventory.addItem(new ItemStack(material, amount, data));
											player.updateInventory();
											
											player.sendMessage(ChatColor.GREEN + "Vous avez acheté " + ChatColor.AQUA + shop.getAmount() + " " + material.name() + ChatColor.GREEN + " contre " + ChatColor.AQUA + shop.getSale() + ChatColor.GREEN + " à ce magasin.");
											
											if ((shop.getPacks() == 0) && (sign.getLine(0).startsWith(ChatColor.AQUA.toString())))
											{
												plugin.um.update(shop, ChatColor.DARK_GRAY + "A/V par " + shop.getAmount(), 0);
											}
										}
										else
										{
											plugin.confirm.add("purchase_" + player.getName() + "_" + world.getName() + "_" + x + "_" + y + "_" + z);
											player.sendMessage(ChatColor.GREEN + "Vous êtes sur le point d'acheter " + ChatColor.AQUA + shop.getAmount() + " " + shop.getMaterial() + ChatColor.GREEN + " en échange de " + ChatColor.AQUA + plugin.economy.format(shop.getPurchase()) + ChatColor.GREEN + ". Pour confirmer cet achat, faites à nouveau un clic-droit sur le panneau.");
										}
									}
									else
									{
										player.sendMessage(ChatColor.RED + "Vous n'avez pas assez de sous pour cet achat.");
									}
								}
								else
								{
									player.sendMessage(ChatColor.RED + "Ce magasin est vide.");
								}
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'acheter.");
							}
						}
						else
						{
							player.sendMessage(ChatColor.RED + "Ce magasin ne vend pas de ressources.");
						}
					}
				}
			}
		}
	}
	
	@EventHandler()
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		for (SSObject shop : plugin.shops)
		{
			Player player = e.getPlayer();
			Location location = shop.getLocation();
			
			World world = location.getWorld();
			Double x = location.getX();
			Double y = location.getY();
			Double z = location.getZ();
			
			if (plugin.confirm.contains("sale_" + player.getName() + "_" + world.getName() + "_" + x + "_" + y + "_" + z))
			{
				plugin.confirm.remove("sale_" + player.getName() + "_" + world.getName() + "_" + x + "_" + y + "_" + z);
			}
			else if (plugin.confirm.contains("purchase_" + player.getName() + "_" + world.getName() + "_" + x + "_" + y + "_" + z))
			{
				plugin.confirm.remove("purchase_" + player.getName() + "_" + world.getName() + "_" + x + "_" + y + "_" + z);
			}
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e)
	{
		if (plugin.um.isTmpItem(e.getItem()))
		{
			e.setCancelled(true);
		}
	}
}