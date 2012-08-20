package fr.rellynn.signshop;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.rellynn.signshop.type.SSObject;

public class UtilManager
{
	private SignShopPlugin plugin;
	
	public UtilManager(SignShopPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public boolean isInteger(String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}
	
	public boolean isDouble(String s)
	{
		try
		{
			Double.parseDouble(s);
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}
	
	public boolean isShop(Location location)
	{
		for (SSObject shop : plugin.shops)
		{
			if (shop.getLocation().equals(location))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public SSObject getShop(Location location)
	{
		for (SSObject shop : plugin.shops)
		{
			if (shop.getLocation().equals(location))
			{
				return shop;
			}
		}
		
		return null;
	}
	
	public boolean isTmpItem(Item item)
	{
		for (SSObject shop : plugin.shops)
		{
			if ((shop.getTmpItem() != null) && (shop.getTmpItem().equals(item)))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public SSObject getShopByItem(Item item)
	{
		for (SSObject shop : plugin.shops)
		{
			if ((shop.getTmpItem() != null) && (shop.getTmpItem().equals(item)))
			{
				return shop;
			}
		}
		
		return null;
	}
	
	public void update(SSObject shop, String message, Integer line)
	{
		Sign sign = (Sign) shop.getLocation().getBlock().getState();
		sign.setLine(line, message);
		sign.update();
	}
	
	public void setItemInHand(PlayerInventory playerinventory, Material material, Integer amount, Byte data)
	{
		if (amount > 0)
		{
			playerinventory.setItemInHand(new ItemStack(material, amount, data));
		}
		else
		{
			playerinventory.setItemInHand(new ItemStack(Material.AIR, 0));
		}
	}
}