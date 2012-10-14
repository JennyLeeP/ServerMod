package servermod.command;

import java.text.DecimalFormat;

import servermod.core.Command;
import servermod.core.ServerMod;
import servermod.util.Util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.World;
import net.minecraft.src.WorldServer;
import net.minecraft.src.WrongUsageException;
import net.minecraftforge.common.DimensionManager;

public class CommandTps extends Command {
	private static DecimalFormat floatfmt = new DecimalFormat("##0.00");
	private static final int MAX_TPS = 20;
	private static final int MAX_TICKMS = 1000 / MAX_TPS;
	
	public CommandTps(String commandName) {
		super(commandName);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		MinecraftServer server = MinecraftServer.getServer();
		
		if (var2.length == 0) {
			var1.sendChatToPlayer(getOverallTps(server, var1, false));
			for (WorldServer world : DimensionManager.getWorlds()) {
				var1.sendChatToPlayer(getTpsInfo(server, world, var1, false));
			}
		} else if (var2[0].toLowerCase().startsWith("o")) {
			for (String split : getOverallTps(server, var1, true).split("\n")) var1.sendChatToPlayer(split);
		} else {
			World world;
			try {
				int dim = Integer.parseInt(var2.length == 1 ? var2[0] : var2[1]);
				world = DimensionManager.getWorld(dim);
				world.getClass();
			} catch (Throwable e) {
				throw new WrongUsageException("commands.tps.usage", new Object[0]);
			}
			
			for (String split : getTpsInfo(server, world, var1, true).split("\n")) var1.sendChatToPlayer(split);
		}
	}
	
	private String getTpsInfo(MinecraftServer server, World world, ICommandSender sender, boolean full) {
		if (world == null) return "";
		double tickms = getTickTimeSum(server.worldTickTimes.get(world.provider.dimensionId)) * 1.0E-6D;
		double tps = 1000 / tickms;
		if (tps > MAX_TPS) tps = MAX_TPS;
		return sender.translateString("commands.servermod_"+commandName+"."+(full ? "long" : "short"), sender.translateString("commands.servermod_"+commandName+".world", world.provider.dimensionId), Util.getWorldName(world), Math.round((tps / MAX_TPS) * 100), floatfmt.format(tps), MAX_TPS, floatfmt.format(tickms), MAX_TICKMS);
	}

	private String getOverallTps(MinecraftServer server, ICommandSender sender, boolean full) {
		double tickms = getTickTimeSum(server.tickTimeArray) * 1.0E-6D;
		double tps = 1000 / tickms;
		if (tps > MAX_TPS) tps = MAX_TPS;
		return sender.translateString("commands.servermod_"+commandName+"."+(full ? "long" : "short"), sender.translateString("commands.servermod_"+commandName+".overall"), "Server", Math.round((tps / MAX_TPS) * 100), floatfmt.format(tps), MAX_TPS, floatfmt.format(tickms), MAX_TICKMS);
	}
	
	private double getTickTimeSum(long[] times) {
        long timesum = 0L;
        if (times == null) return 0;
        for (int i = 0; i < times.length; i++) {
        	timesum += times[i];
        }

        return (double)(timesum / times.length);
    }
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return !ServerMod.instance().settings.require_op_tps || super.canCommandSenderUseCommand(var1);
	}
}
