package servermod.worldedit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.src.EntityPlayerMP;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class WorldEditCUIUtils {
	public static void sendPointEvent(EntityPlayerMP player, int point, int x, int y, int z) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream os = new DataOutputStream(baos);
			
			os.write(("p|"+point+"|"+x+"|"+y+"|"+z).getBytes("UTF-8"));
			
			os.close();
			baos.close();
			byte[] data = baos.toByteArray();
			PacketDispatcher.sendPacketToPlayer(PacketDispatcher.getPacket("WECUI", data), (Player)player);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
