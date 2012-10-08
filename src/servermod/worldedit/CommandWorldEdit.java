package servermod.worldedit;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.CommandException;
import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagDouble;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.PlayerManager;
import net.minecraft.src.PlayerNotFoundException;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.WorldServer;
import net.minecraft.src.WrongUsageException;
import net.minecraftforge.common.DimensionManager;
import servermod.core.Command;

public class CommandWorldEdit extends Command {
	private WorldEdit we;
	private HashMap<String, Method> methodCache = new HashMap<String, Method>();
	
	public CommandWorldEdit(String commandName, WorldEdit we) {
		super(commandName);
		this.we = we;
	}
	
	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage");
		
		try {
			String methodName = var2[0].toLowerCase();
			Method method;
			if (methodCache.containsKey(methodName)) {
				method = methodCache.get(methodName);
			} else {
				try {
					method = getClass().getDeclaredMethod(methodName, ICommandSender.class, String[].class);
				} catch (NoSuchMethodException e) {
					System.out.println(methodName+" "+e);
					throw new WrongUsageException("commands.servermod_"+commandName+".usage");
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
				
				methodCache.put(methodName, method);
			}
			
			method.invoke(this, var1, Arrays.copyOfRange(var2, 1, var2.length));
		} catch (CommandException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List getCommandAliases() {
		return Arrays.asList("we");
	}
	
	private void tool(ICommandSender var1, String[] var2) {
		EntityPlayer player = getCommandSenderAsPlayer(var1);
		
		player.inventory.addItemStackToInventory(we.tool.copy());
		
		var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".tool.success", var1.translateString(we.tool.getItemName()+".name")));
	}
	
	private void sel1(ICommandSender var1, String[] var2) {
		sel(var1, var2, 1);
	}
	
	private void sel2(ICommandSender var1, String[] var2) {
		sel(var1, var2, 2);
	}
	
	public void sel(ICommandSender var1, String[] var2, int index) {
		if (var2.length == 0) {
			EntityPlayer player = getCommandSenderAsPlayer(var1);
			PlayerData data = we.getPlayerData(var1.getCommandSenderName());
			
			if (index == 1) {
				data.selA = new int[3];
				data.selA[0] = (int)Math.floor(player.posX);
				data.selA[1] = (int)Math.floor(player.posY);
				data.selA[2] = (int)Math.floor(player.posZ);
				
				var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".sel.success", index, data.selA[0], data.selA[1], data.selA[2]));
			} else {
				data.selB = new int[3];
				data.selB[0] = (int)Math.floor(player.posX);
				data.selB[1] = (int)Math.floor(player.posY);
				data.selB[2] = (int)Math.floor(player.posZ);
				
				var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".sel.success", index, data.selB[0], data.selB[1], data.selB[2]));
			}
			
		} else if (var2.length == 3) {
			PlayerData data = we.getPlayerData(var1.getCommandSenderName());
			
			if (index == 1) {
				data.selA = new int[3];
				data.selA[0] = parseInt(var1, var2[0]);
				data.selA[1] = parseIntBounded(var1, var2[1], 0, 255);
				data.selA[2] = parseInt(var1, var2[2]);
				
				var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".sel.success", index, data.selA[0], data.selA[1], data.selA[2]));
			} else {
				data.selB = new int[3];
				data.selB[0] = parseInt(var1, var2[0]);
				data.selB[1] = parseIntBounded(var1, var2[1], 0, 255);
				data.selB[2] = parseInt(var1, var2[2]);
				
				var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".sel.success", index, data.selB[0], data.selB[1], data.selB[2]));
			}
		} else throw new WrongUsageException("commands.servermod_"+commandName+".sel.usage");
	}
	
	private void clearsel(ICommandSender var1, String[] var2) {
		we.getPlayerData(var1.getCommandSenderName()).selA = null;
		we.getPlayerData(var1.getCommandSenderName()).selB = null;
		
		var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".clearsel.success"));
	}
	
	private void set(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".set.usage");
		
		int id = 0;
		int meta = 0;
		if (var2[0].indexOf(":") != -1) {
			id = parseIntBounded(var1, var2[0].substring(0, var2[0].indexOf(":")), 0, Block.blocksList.length);
			meta = parseIntBounded(var1, var2[0].substring(var2[0].indexOf(":") + 1), 0, 15);
		} else {
			id = parseIntBounded(var1, var2[0], 0, Block.blocksList.length);
		}
		
		WorldServer world = var1 instanceof Entity ? (WorldServer)((Entity)var1).worldObj : DimensionManager.getWorld(0);
		
		if (Block.blocksList[id] == null || Block.blocksList[id].blockID == 0 || Block.blocksList[id].isAirBlock(world, 0, 0, 0)) {
			throw new PlayerNotFoundException("commands.servermod_"+commandName+".badBlock", id, meta);
		}
		
		PlayerData data = we.getPlayerData(var1.getCommandSenderName());
		
		if (data.selA == null || data.selB == null) throw new PlayerNotFoundException("commands.servermod_"+commandName+".noSelection");
		
		int minX = Math.min(data.selA[0], data.selB[0]);
		int minY = Math.min(data.selA[1], data.selB[1]);
		int minZ = Math.min(data.selA[2], data.selB[2]);
		int maxX = Math.max(data.selA[0], data.selB[0]);
		int maxY = Math.max(data.selA[1], data.selB[1]);
		int maxZ = Math.max(data.selA[2], data.selB[2]);
		
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					world.setBlockAndMetadata(x, y, z, id, meta); // TODO more efficient solution at chunk level?
				}
			}
		}
		
		for (int x = (int)Math.floor(minX / 16); x <= (int)Math.floor(maxX / 16); x++) {
			for (int z = (int)Math.floor(minZ / 16); z <= (int)Math.floor(maxZ / 16); z++) {
				world.getPlayerManager().flagChunkForUpdate(x, 0, z);
			}
		}
		
		var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".set.success", (maxX-minX+1)*(maxY-minY+1)*(maxZ-minZ+1), var1.translateString(new ItemStack(id, 1, meta).getItemName()+".name")));
	}
	
	private void copy(ICommandSender var1, String[] var2) {
		WorldServer world = var1 instanceof Entity ? (WorldServer)((Entity)var1).worldObj : DimensionManager.getWorld(0);
		PlayerData data = we.getPlayerData(var1.getCommandSenderName());
		
		if (data.selA == null || data.selB == null) throw new PlayerNotFoundException("commands.servermod_"+commandName+".noSelection");
		
		int minX = Math.min(data.selA[0], data.selB[0]);
		int minY = Math.min(data.selA[1], data.selB[1]);
		int minZ = Math.min(data.selA[2], data.selB[2]);
		int maxX = Math.max(data.selA[0], data.selB[0]);
		int maxY = Math.max(data.selA[1], data.selB[1]);
		int maxZ = Math.max(data.selA[2], data.selB[2]);
		
		data.clipboardSize[0] = maxX - minX + 1;
		data.clipboardSize[1] = maxY - minY + 1;
		data.clipboardSize[2] = maxZ - minZ + 1;
		data.clipboard = new short[data.clipboardSize[0] * data.clipboardSize[1] * data.clipboardSize[2]];
		data.clipboardMeta = new byte[data.clipboard.length];
		data.clipboardTiles.clear();
		data.clipboardEntities.clear();
		
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					short id = (short)world.getBlockId(x, y, z);
					byte meta = (byte)world.getBlockMetadata(x, y, z);
					
					int index = (y-minY) * data.clipboardSize[0] * data.clipboardSize[2] + (z-minZ) * data.clipboardSize[0] + (x-minX);
					data.clipboard[index] = id;
					data.clipboardMeta[index] = meta;
					
					if (Block.blocksList[id] != null && Block.blocksList[id].hasTileEntity(meta)) {
						TileEntity te = world.getBlockTileEntity(x, y, z);
						
						if (te != null) {
							NBTTagCompound tileNBT = new NBTTagCompound();
							te.writeToNBT(tileNBT);
							tileNBT.setInteger("x", tileNBT.getInteger("x") - minX);
							tileNBT.setInteger("y", tileNBT.getInteger("y") - minY);
							tileNBT.setInteger("z", tileNBT.getInteger("z") - minZ);
							data.clipboardTiles.add(tileNBT);
						}
					}
				}
			}
		}
		
		if (we.sm.settings.worldedit_copy_entities) {
			for (Entity ent : (List<Entity>)world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ))) {
				if (ent instanceof EntityPlayer) continue; // entity blacklist
				
				NBTTagCompound entityNBT = new NBTTagCompound();
				ent.writeToNBT(entityNBT);
				NBTTagList newEntityPos = new NBTTagList();
				NBTTagList entityPos = entityNBT.getTagList("Pos");
				newEntityPos.appendTag(new NBTTagDouble((String)null, ((NBTTagDouble)entityPos.tagAt(0)).data - minX));
				newEntityPos.appendTag(new NBTTagDouble((String)null, ((NBTTagDouble)entityPos.tagAt(1)).data - minY));
				newEntityPos.appendTag(new NBTTagDouble((String)null, ((NBTTagDouble)entityPos.tagAt(2)).data - minZ));
				entityNBT.setTag("Pos", newEntityPos);
			}
		}
		
		var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".copy.success", data.clipboard.length));
	}
	
	private void load(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".load.usage");
		
		File schematicFile = new File("schematics", joinString(var2, 0).replace(".", "") + ".schematic");
		if (!schematicFile.exists()) throw new PlayerNotFoundException("commands.servermod_"+commandName+".load.fileNotFound");
		
		WorldServer world = var1 instanceof Entity ? (WorldServer)((Entity)var1).worldObj : DimensionManager.getWorld(0);
		PlayerData data = we.getPlayerData(var1.getCommandSenderName());
		
		try {
			NBTTagCompound schematicTag = CompressedStreamTools.read(schematicFile);
			if (!schematicTag.getName().equals("Schematic")) throw new Exception();
			if (!schematicTag.getString("Materials").equalsIgnoreCase("Alpha")) throw new Exception();
			
			data.clipboardSize[0] = schematicTag.getShort("Width");
			data.clipboardSize[1] = schematicTag.getShort("Height");
			data.clipboardSize[2] = schematicTag.getShort("Length");
			data.clipboard = new short[data.clipboardSize[0] * data.clipboardSize[1] * data.clipboardSize[2]];
			data.clipboardMeta = new byte[data.clipboard.length];
			data.clipboardTiles.clear();
			data.clipboardEntities.clear();
			
			byte[] rawBlocks = schematicTag.getByteArray("Blocks");
			byte[] meta = schematicTag.getByteArray("Data");
			
			if (schematicTag.hasKey("AddBlocks")) {
				byte[] addBlocks = schematicTag.getByteArray("AddBlocks");
				for (int i = 0, abi = 0; i < data.clipboard.length && abi < addBlocks.length; ++abi) {
					data.clipboard[i] = (short)(((addBlocks[abi] >> 4) << 8) + (rawBlocks[i++] & 0xFF));
					if (i < data.clipboard.length) {
						data.clipboard[i] = (short)(((addBlocks[abi] & 0xF) << 8) + (rawBlocks[i++] & 0xFF));
					}
				}
			} else {
				for (int i = 0; i < rawBlocks.length; i++) {
					data.clipboard[i] = (short)(rawBlocks[i] & 0xFF);
					data.clipboardMeta[i] = meta[i];
				}
			}
			
			if (schematicTag.hasKey("TileEntities")) {
				NBTTagList tiles = schematicTag.getTagList("TileEntities");
				
				for (int i = 0; i < tiles.tagCount(); i++) {
					data.clipboardTiles.add((NBTTagCompound)tiles.tagAt(i));
				}
			}
			
			if (schematicTag.hasKey("Entities")) {
				NBTTagList entities = schematicTag.getTagList("Entities");
				
				for (int i = 0; i < entities.tagCount(); i++) {
					data.clipboardEntities.add((NBTTagCompound)entities.tagAt(i));
				}
			}
		} catch (Throwable e) {
			throw new PlayerNotFoundException("commands.servermod_"+commandName+".load.readFail");
		}
		
		var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".load.success", data.clipboard.length));
	}
	
	private void save(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".save.usage");
		
		File schematicFile = new File("schematics", joinString(var2, 0).replace(".", "") + ".schematic");
		if (!schematicFile.canWrite()) throw new PlayerNotFoundException("commands.servermod_"+commandName+".save.noWrite");
		
		PlayerData data = we.getPlayerData(var1.getCommandSenderName());
		
		if (data.clipboard == null) throw new PlayerNotFoundException("commands.servermod_"+commandName+".noClipboard");
		
		try {
			NBTTagCompound schematicTag = new NBTTagCompound("Schematic");
			
			schematicTag.setString("Materials", "Alpha");
			schematicTag.setShort("Width", (short)data.clipboardSize[0]);
			schematicTag.setShort("Height", (short)data.clipboardSize[1]);
			schematicTag.setShort("Length", (short)data.clipboardSize[2]);
			
			byte[] blocks = new byte[data.clipboard.length];
			byte[] addBlocks = null;
			byte[] meta = new byte[data.clipboardMeta.length];
			
			int extblocks = 0;
			
			for (int i = 0; i < blocks.length && i < meta.length; i++) {
				if (data.clipboard[i] > 255) {
					extblocks++;
					if (addBlocks == null) addBlocks = new byte[blocks.length >> 1];
					addBlocks[i >> 1] = (byte)(((i & 1) == 0) ?
							addBlocks[i >> 1] & 0xF0 | (data.clipboard[i] >> 8) & 0xF
							: addBlocks[i >> 1] & 0xF | ((data.clipboard[i] >> 8) & 0xF) << 4);
				}
				
				blocks[i] = (byte)data.clipboard[i];
				meta[i] = data.clipboardMeta[i];
			}
			
			if (extblocks >= 0) {
				var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".4kWarning", extblocks));
			}
			
			NBTTagList tiles = new NBTTagList();
			
			for (NBTTagCompound tile : data.clipboardTiles) {
				tiles.appendTag(tile);
			}
			
			schematicTag.setTag("TileEntities", tiles);
			
			NBTTagList entities = new NBTTagList();
				
			for (NBTTagCompound ent : data.clipboardEntities) {
				entities.appendTag(ent);
			}
			
			schematicTag.setTag("Entities", entities);
			
			CompressedStreamTools.write(schematicTag, schematicFile);
		} catch (Throwable e) {
			throw new WrongUsageException("commands.servermod_"+commandName+".save.writeFail");
		}
		
		var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".save.success"));
	}
	
	private void paste(ICommandSender var1, String[] var2) {
		int baseX, baseY, baseZ;
		
		if (var2.length == 0) {
			EntityPlayer player = getCommandSenderAsPlayer(var1);
			baseX = (int)Math.ceil(player.posX);
			baseY = (int)Math.floor(player.posY);
			baseZ = (int)Math.floor(player.posZ);
		} else if (var2.length == 3) {
			baseX = parseInt(var1, var2[0]);
			baseY = parseIntBounded(var1, var2[0], 0, 255);
			baseZ = parseInt(var1, var2[1]);
		} else {
			throw new WrongUsageException("commands.servermod_"+commandName+".paste.usage");
		}
		
		WorldServer world = var1 instanceof Entity ? (WorldServer)((Entity)var1).worldObj : DimensionManager.getWorld(0);
		PlayerData data = we.getPlayerData(var1.getCommandSenderName());
		
		if (data.clipboard == null) throw new PlayerNotFoundException("commands.servermod_"+commandName+".noClipboard");
		
		int i = 0;
		
		for (int x = baseX; x <= baseX + data.clipboardSize[0]; x++) {
			for (int y = baseY; y <= baseY + data.clipboardSize[1]; y++) {
				for (int z = baseZ; z <= baseZ + data.clipboardSize[2]; z++) {
					world.setBlockAndMetadata(x, y, z, data.clipboard[i], data.clipboardMeta[i++]); // TODO more efficient solution at chunk level?
				}
			}
		}
		
		for (NBTTagCompound tileNBT : data.clipboardTiles) {
			NBTTagCompound tileNBTNew = (NBTTagCompound)tileNBT.copy();
			tileNBTNew.setInteger("x", tileNBTNew.getInteger("x"));
			tileNBTNew.setInteger("y", tileNBTNew.getInteger("y"));
			tileNBTNew.setInteger("z", tileNBTNew.getInteger("z"));
			TileEntity tile = TileEntity.createAndLoadEntity(tileNBTNew);
			world.setBlockTileEntity(tile.xCoord, tile.yCoord, tile.zCoord, tile);
		}
		
		for (NBTTagCompound entityNBT : data.clipboardEntities) {
			NBTTagCompound entityNBTNew = (NBTTagCompound)entityNBT.copy();
			NBTTagList newEntityPos = new NBTTagList();
			NBTTagList entityPos = entityNBTNew.getTagList("Pos");
			newEntityPos.appendTag(new NBTTagDouble((String)null, baseX + ((NBTTagDouble)entityPos.tagAt(0)).data));
			newEntityPos.appendTag(new NBTTagDouble((String)null, baseY + ((NBTTagDouble)entityPos.tagAt(1)).data));
			newEntityPos.appendTag(new NBTTagDouble((String)null, baseZ + ((NBTTagDouble)entityPos.tagAt(2)).data));
			entityNBTNew.setTag("Pos", newEntityPos);
			Entity ent = EntityList.createEntityFromNBT(entityNBTNew, world);
			world.spawnEntityInWorld(ent);
		}
		
		for (int x = (int)Math.floor(baseX / 16); x <= (int)Math.floor(baseX + data.clipboardSize[0] / 16); x++) {
			for (int z = (int)Math.floor(baseZ / 16); z <= (int)Math.floor(baseX + data.clipboardSize[2] / 16); z++) {
				world.getPlayerManager().flagChunkForUpdate(x, 0, z);
			}
		}
		
		var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".paste.success"));
	}
}
