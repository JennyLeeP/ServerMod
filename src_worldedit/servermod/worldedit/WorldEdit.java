package servermod.worldedit;

import java.util.WeakHashMap;

import com.sk89q.worldedit.WorldVector;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.World;
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

@Mod(modid = "ServerMod|WorldEdit", name = "ServerMod WorldEdit", version = "1.0", dependencies = "after:ServerMod")
public class WorldEdit implements IChatListener {
	@Instance("ServerMod|WorldEdit")
	public static WorldEdit instance;
	
	protected com.sk89q.worldedit.WorldEdit we;
	private Configuration config;
	
	private WeakHashMap<EntityPlayer, LocalPlayer> players = new WeakHashMap<EntityPlayer, LocalPlayer>();
	private WeakHashMap<World, LocalWorld> worlds = new WeakHashMap<World, LocalWorld>();
	private WeakHashMap<Entity, LocalEntity> entities = new WeakHashMap<Entity, LocalEntity>();
	
	@ServerStarting
	public void onServerStarting(FMLServerStartingEvent event) {
		config = new Configuration();
		config.load();
		we = new com.sk89q.worldedit.WorldEdit(new ServerInterface(), config);
		
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
