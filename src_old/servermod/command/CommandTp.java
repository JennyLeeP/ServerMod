package servermod.command;

import java.util.List;

import servermod.core.Command;
import servermod.util.TeleporterCustom;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.PlayerNotFoundException;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.WrongUsageException;

public class CommandTp extends Command {
	public CommandTp(String commandName) {
		super(commandName);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();
		
		EntityPlayerMP tpfrom = (EntityPlayerMP)getCommandSenderAsPlayer(var1);
		EntityPlayerMP tpto = null;
		int tpdim = tpfrom.dimension;
		double tpx, tpy, tpz;
		float tpyaw = tpfrom.rotationYaw, tppitch = tpfrom.rotationPitch;
		
		switch (var2.length) {
			case 1:
				tpto = manager.getPlayerForUsername(var2[0]);
				if (tpto == null) throw new PlayerNotFoundException();
				tpx = tpto.posX;
				tpy = tpto.posY;
				tpz = tpto.posZ;
				tpyaw = tpto.rotationYaw;
				tppitch = tpto.rotationPitch;
				break;
			case 2:
				tpfrom = manager.getPlayerForUsername(var2[0]);
				tpto = manager.getPlayerForUsername(var2[1]);
				if (tpto == null) throw new PlayerNotFoundException();
				tpdim = tpto.dimension;
				tpx = tpto.posX;
				tpy = tpto.posY;
				tpz = tpto.posZ;
				tpyaw = tpto.rotationYaw;
				tppitch = tpto.rotationPitch;
				break;
			case 3:
				try {
					tpx = Integer.parseInt(var2[0])+0.5D;
					tpy = Integer.parseInt(var2[1]);
					if (tpy > 256) tpy = 256;
					else if (tpy < 0) tpy = 0;
					tpz = Integer.parseInt(var2[2])+0.5D;
				} catch (Throwable e) {
					throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
				}
				break;
			case 4:
				tpfrom = manager.getPlayerForUsername(var2[0]);
				if (tpfrom == null) throw new PlayerNotFoundException();
				tpdim = tpfrom.dimension;
				try {
					tpx = Integer.parseInt(var2[1])+0.5D;
					tpy = Integer.parseInt(var2[2]);
					if (tpy > 256) tpy = 256;
					else if (tpy < 0) tpy = 0;
					tpz = Integer.parseInt(var2[3])+0.5D;
				} catch (Throwable e) {
					throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
				}
				break;
			default:
				throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
		}
		
		if (tpfrom == null) throw new PlayerNotFoundException();
		
		if (tpfrom.dimension == tpdim) tpfrom.playerNetServerHandler.setPlayerLocation(tpx, tpy, tpz, tpyaw, tppitch);
		else manager.transferPlayerToDimension(tpfrom, tpdim, new TeleporterCustom(tpx, tpy, tpz, tpyaw, tppitch));
		
		if (tpto == null) notifyAdmins(var1, "commands.servermod_"+commandName+".coordinatesDim", new Object[] {tpfrom.getEntityName(), tpdim, (int)tpx, (int)tpy, (int)tpz});
		else notifyAdmins(var1, "commands.tp.success", new Object[] {tpfrom.getEntityName(), tpto.getEntityName()});
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
        return var2.length >= 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
