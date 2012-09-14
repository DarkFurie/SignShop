package fr.rellynn.signshop.type;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.material.Sign;

import fr.rellynn.signshop.SignShopPlugin;

public class SSObject
{
	// FIELDS
	protected String owner;
	protected String type;
	
	protected Location location;
	protected Location fixLocation;
	
	protected Material material;
	protected Byte data;
	
	protected Double purchase;
	protected Double sale;
	
	protected Integer amount;
	protected Integer packs;
	protected Integer maximum;
	
	protected Item tmpItem;
	
	protected Boolean infinite;
	protected Boolean chunkLoaded;
	
	// CONSTRUCTOR
	public SSObject(String owner, String type, Location location, Material material, Byte data, Double purchase, Double sale, Integer amount, Integer packs, Integer maximum, Boolean infinite, SignShopPlugin plugin)
	{
		try
		{
			this.owner = owner;
			this.type = type;
			this.location = location;
			this.fixLocation = location.getBlock().getRelative(((Sign) location.getBlock().getState().getData()).getAttachedFace()).getLocation();
			this.material = material;
			this.data = data;
			this.purchase = purchase;
			this.sale = sale;
			this.amount = amount;
			this.packs = packs;
			this.maximum = maximum;
			this.infinite = infinite;
			plugin.shops.add(this);
			
			initShop();
		}
		catch (Exception ex)
		{
		}
	}
	
	// METHODS
	private void initShop()
	{
		chunkLoaded = fixLocation.getWorld().isChunkLoaded(fixLocation.getChunk());
		
		if (chunkLoaded)
		{
			dropTmpItem();
		}
	}
	
	public void dropTmpItem()
	{
		if ((material != Material.AIR) && (tmpItem == null) && (fixLocation.clone().add(0.0D, 1.0D, 0.0D).getBlock().getType() == Material.AIR))
		{
			tmpItem = fixLocation.getWorld().dropItem(fixLocation.clone().add(0.5D, 1.6D, 0.5D), new ItemStack(material, 1, data));
			tmpItem.setVelocity(new Vector(0.0D, 0.1D, 0.0D));
		}
	}
	
	public void removeTmpItem()
	{
		removeOtherEntities();
		
		if (tmpItem != null)
		{
			tmpItem.remove();
			tmpItem = null;
		}
	}
	
	private void removeOtherEntities()
	{
		Chunk c = fixLocation.getChunk();
		
		for (Entity e : c.getEntities())
		{
			if ((!(e instanceof Item)) || (e.equals(tmpItem)) || (!e.getLocation().getBlock().equals(fixLocation.getBlock().getRelative(BlockFace.UP))))
			{
				continue;
			}
			
			e.remove();
		}
	}
	
	public void refreshTmpItem()
	{
		removeTmpItem();
		dropTmpItem();
	}
	
	public void defineItem(Material material, Byte data)
	{
		this.material = material;
		this.data = data;
		
		dropTmpItem();
	}
	
	public void addItems()
	{
		packs++;
	}
	
	public void subtractItems()
	{
		packs--;
	}
	
	// GETTERS
	public String getOwner()
	{
		return owner;
	}
	
	public String getType()
	{
		return type;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public Location getFixLocation()
	{
		return fixLocation;
	}
	
	public Material getMaterial()
	{
		return material;
	}
	
	public Byte getData()
	{
		return data;
	}
	
	public Integer getAmount()
	{
		return amount;
	}
	
	public Integer getPacks()
	{
		return packs;
	}
	
	public Double getPurchase()
	{
		return purchase;
	}
	
	public Double getSale()
	{
		return sale;
	}
	
	public Integer getMaximum()
	{
		return maximum;
	}
	
	public Item getTmpItem()
	{
		return tmpItem;
	}
	
	public Boolean getInfinite()
	{
		return infinite;
	}
	
	// SETTERS
	public void setType(String type)
	{
		this.type = type;
	}
	
	public void setInfinite(Boolean infinite)
	{
		this.infinite = infinite;
	}
	
	public void setChunkLoaded(Boolean chunkLoaded)
	{
		this.chunkLoaded = chunkLoaded;
	}
}