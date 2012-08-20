package fr.rellynn.signshop.listeners;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;

import fr.rellynn.signshop.SignShopPlugin;
import fr.rellynn.signshop.type.SSObject;

public class SSEntityListener implements Listener
{
	private SignShopPlugin plugin;
	
	public SSEntityListener(SignShopPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent e)
	{
		Item item = e.getEntity();
		
		if (plugin.um.isTmpItem(item))
		{
			SSObject shop = plugin.um.getShopByItem(item);
			shop.refreshTmpItem();
		}
	}
}
