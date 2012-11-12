package servermod.command;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityLightningBolt;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.World;
import net.minecraft.src.WrongUsageException;
import net.minecraftforge.common.DimensionManager;
import servermod.core.Command;

public class CommandWeather extends Command {
	public CommandWeather(String commandName) {
		super(commandName);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage");
		
		World world = var1 instanceof Entity ? ((Entity)var1).worldObj : DimensionManager.getWorld(0);
		char c = var2[0].charAt(0);
		
		switch (c) {
			case 'c':
			case 's':
			case 'o':
			case 'n': {
				world.getWorldInfo().setRaining(false);
				world.getWorldInfo().setThundering(false);
				
				notifyAdmins(var1, "commands.servermod_"+commandName+".success", var1.translateString("commands.servermod_weather.weather.none"));
				break;
			}
			case 'r': {
				world.getWorldInfo().setRaining(true);
				world.getWorldInfo().setThundering(false);
				
				notifyAdmins(var1, "commands.servermod_"+commandName+".success", var1.translateString("commands.servermod_weather.weather.rain"));
				break;
			}
			case 't': {
				world.getWorldInfo().setRaining(true);
				world.getWorldInfo().setThundering(true);
				
				notifyAdmins(var1, "commands.servermod_"+commandName+".success", var1.translateString("commands.servermod_weather.weather.thunder"));
				break;
			}
			case 'l': {
				MovingObjectPosition mop = getCommandSenderAsPlayer(var1).rayTrace(256.0D, 0F);
				if (mop == null) return;
				world.addWeatherEffect(new EntityLightningBolt(world, mop.blockX, mop.blockY, mop.blockZ));
				
				notifyAdmins(var1, "commands.servermod_"+commandName+".lightning");
				break;
			}
			default: {
				throw new WrongUsageException("commands.servermod_"+commandName+".usage");
			}
		}
	}
}
