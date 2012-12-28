package servermod.worldedit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import servermod.core.Settings;
import servermod.core.Util;

import com.sk89q.worldedit.WorldVector;

import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.IChatListener;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = "ServerMod|WorldEdit", name = "ServerMod WorldEdit", version = "1.0", dependencies = "required-after:ServerMod")
public class WorldEdit implements IChatListener {
	@Instance("ServerMod|WorldEdit")
	public static WorldEdit instance;
	public static MinecraftServer server;
	
	protected Logger log = Logger.getLogger("ServerMod");
	protected Settings settings = new Settings(new File(new File("servermod", "config"), "worldedit.cfg"), "ServerMod WorldEdit configuration file");
	
	protected com.sk89q.worldedit.WorldEdit we;
	private Configuration config;
	protected ServerInterface serverInterface;
	
	protected List<String> whitelist = new ArrayList<String>();
	private Map<EntityPlayer, LocalPlayer> players = new WeakHashMap<EntityPlayer, LocalPlayer>();
	private Map<World, LocalWorld> worlds = new WeakHashMap<World, LocalWorld>();
	private Map<Entity, LocalEntity> entities = new WeakHashMap<Entity, LocalEntity>();
	
	@ServerStarting
	public void onServerStarting(FMLServerStartingEvent event) {
		server = event.getServer();
		Loader.instance().getIndexedModList().get("ServerMod|WorldEdit").getMetadata().parent = "ServerMod";
		
		try {
			config = new Configuration();
			config.load();
			we = new com.sk89q.worldedit.WorldEdit(new ServerInterface(), config);
		} catch (Throwable e) {
			log.log(Level.SEVERE, "Failed to initialize WorldEdit. Ensure WorldEdit.jar is in the lib folder");
			return;
		}
		
		settings.addSetting("enable-whitelist", false, "Use servermod/worldedit-whitelist.txt as a list of users capable of using WorldEdit, instead of all ops");
		
		try {
			settings.load();
		} catch (Throwable e) {
			log.log(Level.WARNING, "Failed to load the configuration file", e);
		}
		try {
			settings.save();
		} catch (Throwable e) {
			log.log(Level.WARNING, "Failed to save the configuration file", e);
		}
		
		if (settings.getBoolean("enable-whitelist")) {
			try {
				for (String user : Util.readFileToString(new File("servermod", "worldedit-whitelist.txt")).split("\n")) {
					whitelist.add(user.toLowerCase());
				}
			} catch (Throwable e) {
				log.log(Level.WARNING, "Failed to load the WorldEdit whitelist", e);
			}
		}
		
		MinecraftForge.EVENT_BUS.register(this);
		
		if (Loader.isModLoaded("Forge")) new ForgeHelper();
		else NetworkRegistry.instance().registerChatListener(this);
	}

	@Override
	public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
		if (message.message.startsWith("//")) {
			we.handleCommand(getPlayer(handler.getPlayer()), message.message.split(" "));
			return new Packet3Chat("");
		}
		
		return message;
	}

	@Override
	public Packet3Chat clientChat(NetHandler handler, Packet3Chat message) {
		return message;
	}
	
	protected LocalPlayer getPlayer(EntityPlayer player) {
		if (players.containsKey(player)) {
			return players.get(player);
		} else {
			LocalPlayer ret = new LocalPlayer(player);
			players.put(player, ret);
			return ret;
		}
	}
	
	protected LocalWorld getWorld(World world) {
		if (worlds.containsKey(world)) {
			return worlds.get(world);
		} else {
			LocalWorld ret = new LocalWorld(world);
			worlds.put(world, ret);
			return ret;
		}
	}
	
	protected LocalEntity getEntity(Entity entity) {
		if (entities.containsKey(entity)) {
			return entities.get(entity);
		} else {
			LocalEntity ret = new LocalEntity(entity);
			entities.put(entity, ret);
			return ret;
		}
	}
}
