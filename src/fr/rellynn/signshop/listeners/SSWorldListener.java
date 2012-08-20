package fr.rellynn.signshop.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import fr.rellynn.signshop.SignShopPlugin;
import fr.rellynn.signshop.type.SSObject;

public class SSWorldListener implements Listener
{
	private SignShopPlugin plugin;
	
	public SSWorldListener(SignShopPlugin plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler()
	public void onChunkLoad(ChunkLoadEvent e)
	{
		for (SSObject shop : plugin.shops)
		{
			Location l = shop.getLocation();
			
			if (e.getChunk() == l.getChunk())
			{
				if (shop.getMaterial() != Material.AIR)
				{
					shop.setChunkLoaded(true);
					
					if (shop.getTmpItem() != null)
					{
						shop.removeTmpItem();
					}
					
					shop.dropTmpItem();
				}
			}
		}
	}
	
	@EventHandler()
	public void onChunkUnload(ChunkUnloadEvent e)
	{
		for (SSObject shop : plugin.shops)
		{
			Location l = shop.getLocation();
			
			if (e.getChunk() == l.getChunk())
			{
				if (shop.getMaterial() != Material.AIR)
				{
					if (shop.getTmpItem() != null)
					{
						shop.removeTmpItem();
						shop.setChunkLoaded(false);
					}
				}
			}
		}
	}
}