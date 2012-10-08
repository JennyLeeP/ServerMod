package servermod.home;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.logging.Level;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

import net.minecraft.src.AnvilSaveHandler;
import net.minecraft.src.BlockBed;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.DedicatedServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.ServerCommandManager;
import net.minecraft.src.StatCollector;
import net.minecraft.src.World;
import net.minecraft.src.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.WorldEvent;
import servermod.command.CommandDisarm;
import servermod.core.ServerMod;
import servermod.util.DimensionChunkCoordinates;
import servermod.util.TeleporterCustom;

public class Home implements IPlayerTracker {
	protected ServerMod sm;
	protected HashMap<String,DimensionChunkCoordinates> homes = new HashMap<String,DimensionChunkCoordinates>();
	
	public Home(ServerMod sm) {
		this.sm = sm;
		
		ServerCommandManager commands = (ServerCommandManager)sm.server.getCommandManager();
		LanguageRegistry lang = LanguageRegistry.instance();
		
		lang.addStringLocalization("commands.generic.home.fail", "Your home position is not defined");
		
		lang.addStringLocalization("commands.servermod_home.usage", "/home");
		commands.registerCommand(new CommandHome("home", this));
		
		lang.addStringLocalization("commands.servermod_sethome.usage", "/sethome");
		lang.addStringLocalization("commands.servermod_sethome.success", "Set your home position to %d:%d,%d,%d");
		commands.registerCommand(new CommandSetHome("sethome", this));
		
		lang.addStringLocalization("commands.servermod_clearhome.usage", "/clearhome");
		lang.addStringLocalization("commands.servermod_clearhome.success", "Cleared your home position");
		commands.registerCommand(new CommandClearHome("clearhome", this));
		
		MinecraftForge.EVENT_BUS.register(this);
		GameRegistry.registerPlayerTracker(this);
		
		sm.server.logger.log(Level.INFO, "Home System: Initialized");
	}
	
	@ForgeSubscribe
	public void onWorldLoad(WorldEvent.Load event) {
		if (event.world.saveHandler instanceof AnvilSaveHandler) load(event.world);
	}
	
	@ForgeSubscribe
	public void onWorldSave(WorldEvent.Save event) {
		if (event.world.saveHandler instanceof AnvilSaveHandler) save(event.world);
	}
	
	@ForgeSubscribe
	public void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
		if (!sm.settings.home_use_bed || event.entityPlayer.worldObj.isRemote) return;
		
		homes.put(event.entityPlayer.username, new DimensionChunkCoordinates(event.x, event.y, event.z, event.entityPlayer.dimension));
	}
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) respawnPlayer((EntityPlayerMP)player);
	}
	
	private void load(World world) {
		File homeFile = new File(sm.createWorldFolder(getSaveDirectory((AnvilSaveHandler)world.getSaveHandler())), "home.dat");
		NBTTagCompound tag;
		
		try {
			tag = CompressedStreamTools.readCompressed(new FileInputStream(homeFile));
		} catch (Throwable e) {
			sm.server.logger.log(Level.WARNING, "Home System: Could not load home data", e);
			return;
		}
		
		NBTTagList homeList = tag.getTagList("Homes");
		for (int i = 0; i < homeList.tagCount(); i++) {
			NBTTagCompound home = (NBTTagCompound)homeList.tagAt(i);
			homes.put(home.getString("Username"), new DimensionChunkCoordinates(home.getInteger("x"), home.getInteger("y"), home.getInteger("z"), home.getInteger("Dimension")));
		}
	}
	
	private void save(World world) {
		File homeFile = new File(sm.createWorldFolder(getSaveDirectory((AnvilSaveHandler)world.getSaveHandler())), "home.dat");
		NBTTagCompound tag = new NBTTagCompound();
		
		NBTTagList homeList = tag.getTagList("Homes");
		for (String username : homes.keySet()) {
			DimensionChunkCoordinates dcc = homes.get(username);
			
			NBTTagCompound homeTag = new NBTTagCompound();
			homeTag.setString("Username", username);
			homeTag.setInteger("x", dcc.posX);
			homeTag.setInteger("y", dcc.posY);
			homeTag.setInteger("z", dcc.posZ);
			homeTag.setInteger("Dimension", dcc.dimension);
			
			homeList.appendTag(homeTag);
		}
		
		tag.setTag("Homes", homeList);
		
		try {
			CompressedStreamTools.writeCompressed(tag, new FileOutputStream(homeFile));
		} catch (Throwable e) {
			sm.server.logger.log(Level.WARNING, "Home System: Could not save home data", e);
			return;
		}
	}
	
	private File getSaveDirectory(AnvilSaveHandler saveHandler) {
		return new File(sm.server instanceof DedicatedServer ? "." : "saves", saveHandler.getSaveDirectoryName());
	}
	
	protected void respawnPlayer(EntityPlayerMP player) {
		if (homes.containsKey(player.username)) {
			DimensionChunkCoordinates dcc = homes.get(player.username);
			
			WorldServer world = DimensionManager.getWorld(dcc.dimension);
			if (world == null) {
				player.sendChatToPlayer("Cannot go to your home position: World missing");
				return;
			}
			
			DimensionChunkCoordinates newdcc = null;
			
			for (int x = 0; x < 3; x++) {
				boolean brk = false;
				for (int z = 0; z < 3; z++) {
					System.out.println(world.doesBlockHaveSolidTopSurface(dcc.posX + x, dcc.posY - 1, dcc.posZ + z));
					System.out.println(world.getBlockId(dcc.posX + x, dcc.posY, dcc.posZ + z) == 0);
					System.out.println(world.getBlockId(dcc.posX + x, dcc.posY + 1, dcc.posZ + z) == 0);
					
					System.out.println(world.doesBlockHaveSolidTopSurface(dcc.posX - x, dcc.posY - 1, dcc.posZ - z));
					System.out.println(world.getBlockId(dcc.posX - x, dcc.posY, dcc.posZ - z) == 0);
					System.out.println(world.getBlockId(dcc.posX - x, dcc.posY + 1, dcc.posZ - z) == 0);
					
					if (world.doesBlockHaveSolidTopSurface(dcc.posX + x, dcc.posY - 1, dcc.posZ + z) && world.getBlockId(dcc.posX + x, dcc.posY, dcc.posZ + z) == 0 && world.getBlockId(dcc.posX + x, dcc.posY + 1, dcc.posZ + z) == 0) {
						newdcc = new DimensionChunkCoordinates(dcc.posX + x, dcc.posY, dcc.posZ + z, dcc.dimension);
					} else if (world.doesBlockHaveSolidTopSurface(dcc.posX - x, dcc.posY - 1, dcc.posZ - z) && world.getBlockId(dcc.posX - x, dcc.posY, dcc.posZ - z) == 0 && world.getBlockId(dcc.posX - x, dcc.posY + 1, dcc.posZ - z) == 0) {
						newdcc = new DimensionChunkCoordinates(dcc.posX - x, dcc.posY, dcc.posZ - z, dcc.dimension);
					}
				}
				if (newdcc != null) break;
			}
			
			if (newdcc == null) {
				player.sendChatToPlayer("Cannot go to your home position: Obstructed");
				return;
			}
			
			if (player.dimension == dcc.dimension) ((EntityPlayerMP)player).serverForThisPlayer.setPlayerLocation(newdcc.posX + 0.5D, newdcc.posY + 0.5D, newdcc.posZ + 0.5D, 0F, 0F);
			else sm.server.getConfigurationManager().transferPlayerToDimension(player, dcc.dimension, new TeleporterCustom(newdcc.posX + 0.5D, newdcc.posY + 0.5D, newdcc.posZ + 0.5D, 0F, 0F));
		}
	}
}
