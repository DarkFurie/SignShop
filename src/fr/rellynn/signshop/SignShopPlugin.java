package fr.rellynn.signshop;

import java.util.ArrayList;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.rellynn.signshop.command.InfiniteCommand;
import fr.rellynn.signshop.listeners.SSBlockListener;
import fr.rellynn.signshop.listeners.SSEntityListener;
import fr.rellynn.signshop.listeners.SSPlayerListener;
import fr.rellynn.signshop.listeners.SSWorldListener;
import fr.rellynn.signshop.type.SSObject;

public class SignShopPlugin extends JavaPlugin
{
	public Economy economy = null;
	public Permission permission = null;
	
	public InfiniteCommand infiniteCommand = null;
	
	public ArrayList<SSObject> shops = null;
	public ArrayList<String> confirm = null;
	
	public ArrayList<Player> infinitePlayers = null;
	
	public UtilManager um = null;
	
	private SSBlockListener bListener = null;
	private SSEntityListener eListener = null;
	private SSPlayerListener pListener = null;
	private SSWorldListener wListener = null;
	
	@Override
	public void onEnable()
	{
		if (!setupDepends())
		{
			setEnabled(false);
		}
		
		setupCommands();
		
		shops = new ArrayList<SSObject>();
		confirm = new ArrayList<String>();
		
		infinitePlayers = new ArrayList<Player>();
		
		um = new UtilManager(this);
		
		bListener = new SSBlockListener(this);
		eListener = new SSEntityListener(this);
		pListener = new SSPlayerListener(this);
		wListener = new SSWorldListener(this);
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(bListener, this);
		pm.registerEvents(eListener, this);
		pm.registerEvents(pListener, this);
		pm.registerEvents(wListener, this);
		
		loadAll();
	}
	
	@Override
	public void onDisable()
	{
		for (SSObject shop : shops) shop.removeTmpItem();
		saveAll();
	}
	
	private boolean setupDepends()
	{
		if (!setupEconomy())
		{
			getServer().getLogger().info("Le plugin d'economie est introuvable, etes-vous sur d'en avoir un ?");
			return false;
		}
		else if (!setupPermission())
		{
			getServer().getLogger().info("Le plugin de permissions est introuvable, etes-vous sur d'en avoir un ?");
			return false;
		}
		
		return true;
	}
	
	public void setupCommands()
	{
		infiniteCommand = new InfiniteCommand(this);
		getCommand("shop").setExecutor(infiniteCommand);
	}
	
	private Boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		
		if (economyProvider != null)
		{
			economy = (Economy) economyProvider.getProvider();
			return true;
		}
		
		return false;
	}
	
	private Boolean setupPermission()
	{
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
		
		if (permissionProvider != null)
		{
			permission = (Permission) permissionProvider.getProvider();
			return true;
		}
		
		return false;
	}

	private void saveAll()
	{
		for (SSObject shop : shops)
		{
			Location location = shop.getLocation();
			
			FileConfiguration config = getConfig();
			String node = "shops." + location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
			
			config.set(node + ".owner", shop.getOwner());
			config.set(node + ".type", shop.getType());
			config.set(node + ".location.world", location.getWorld().getName());
			config.set(node + ".location.x", location.getBlockX());
			config.set(node + ".location.y", location.getBlockY());
			config.set(node + ".location.z", location.getBlockZ());
			config.set(node + ".material", shop.getMaterial().name());
			config.set(node + ".data", shop.getData());
			config.set(node + ".purchase", shop.getPurchase());
			config.set(node + ".sale", shop.getSale());
			config.set(node + ".amount", shop.getAmount());
			config.set(node + ".packs", shop.getPacks());
			config.set(node + ".maximum", shop.getMaximum());
			config.set(node + ".infinite", shop.getInfinite());
			saveConfig();
		}
	}
	
	@SuppressWarnings("unused")
	private void loadAll()
	{
		FileConfiguration config = getConfig();
		
		if (config.getConfigurationSection("shops") != null)
		{
			Set<String> keys = config.getConfigurationSection("shops").getKeys(false);
			
			for (String key : keys)
			{
				ConfigurationSection cs = config.getConfigurationSection("shops." + key);
				World w = getServer().getWorld(cs.getString("location.world"));
				
				if (w != null)
				{
					String owner = cs.getString("owner");
					String type = cs.getString("type");
					Double x = cs.getDouble("location.x");
					Double y = cs.getDouble("location.y");
					Double z = cs.getDouble("location.z");
					Material material = Material.getMaterial(cs.getString("material"));
					Byte data = (byte) cs.getInt("data");
					Double purchase = cs.getDouble("purchase");
					Double sale = cs.getDouble("sale");
					Integer amount = cs.getInt("amount");
					Integer packs = cs.getInt("packs");
					Integer maximum = cs.getInt("maximum");
					Boolean infinite = cs.getBoolean("infinite");
					
					Location location = new Location(w, x, y, z);
					SSObject shop = new SSObject(owner, type, location, material, data, purchase, sale, amount, packs, maximum, infinite, this);
				}
			}
		}
	}
	
	public void removeShop(SSObject shop)
	{
		Location location = shop.getLocation();
		
		FileConfiguration config = getConfig();
		String node = "shops." + location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
		
		if (config.getConfigurationSection(node) != null)
		{
			config.set(node, null);
			saveConfig();
		}
		
		shop.removeTmpItem();
		shops.remove(shop);
	}
}