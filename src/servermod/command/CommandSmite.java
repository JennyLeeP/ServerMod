package servermod.command;

import servermod.core.ServerMod;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;

public class CommandSmite extends Command {
	public CommandSmite() {
		super("smite");
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		EntityPlayer player = var2.length < 1 ? getCommandSenderAsPlayer(var1) : ServerMod.server.getConfigurationManager().getPlayerForUsername(var2[0]);
		if (player == null) throw new PlayerNotFoundException();
		
		player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ));
		
		notifyAdmins(var1, "Smiting "+player.username);
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public boolean isUsernameIndex(int var1) {
		return var1 == 0;
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/"+name+" [player]";
	}
}
