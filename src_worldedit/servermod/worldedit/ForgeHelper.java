package servermod.worldedit;

import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.sk89q.worldedit.WorldVector;

import cpw.mods.fml.common.network.IChatListener;
import cpw.mods.fml.common.network.NetworkRegistry;

public class ForgeHelper implements IChatListener {
	public ForgeHelper() {
		NetworkRegistry.instance().registerChatListener(this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@ForgeSubscribe
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.entity.worldObj.isRemote) return;
		
		switch (event.action) {
			case LEFT_CLICK_BLOCK: {
				WorldEdit.instance.we.handleBlockLeftClick(WorldEdit.instance.getPlayer(event.entityPlayer), new WorldVector(WorldEdit.instance.getWorld(event.entityPlayer.worldObj), event.x, event.y, event.z));
				break;
			}
			case RIGHT_CLICK_AIR: {
				WorldEdit.instance.we.handleRightClick(WorldEdit.instance.getPlayer(event.entityPlayer));
				break;
			}
			case RIGHT_CLICK_BLOCK: {
				WorldEdit.instance.we.handleBlockRightClick(WorldEdit.instance.getPlayer(event.entityPlayer), new WorldVector(WorldEdit.instance.getWorld(event.entityPlayer.worldObj), event.x, event.y, event.z));
				break;
			}
		}
	}
	
	@ForgeSubscribe
	public void onServerChat(ServerChatEvent event) {
		if (event.message.startsWith("\u00bcworldedit\u00bc")) {
			event.setCanceled(true);
			WorldEdit.instance.we.handleCommand(WorldEdit.instance.getPlayer(event.player), event.message.substring(11).split(" "));
		}
	}

	@Override
	public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
		if (message.message.startsWith("//")) message.message = "\u00bcworldedit\u00bc"+message.message; // unique code
		return message;
	}

	@Override
	public Packet3Chat clientChat(NetHandler handler, Packet3Chat message) {
		return message;
	}
}
