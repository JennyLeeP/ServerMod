package servermod.command;

import java.text.DecimalFormat;

import servermod.core.ServerMod;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.World;
import net.minecraft.src.WrongUsageException;

public class CommandTps extends Command {
	private static DecimalFormat floatfmt = new DecimalFormat("##0.00");
	private static final int MAX_TPS = 20;
	private static final int MIN_TICKMS = 1000 / MAX_TPS;
	
	public CommandTps() {
		super("tps");
	}
	
	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 1) {
			double tps = getTps(null);
			var1.sendChatToPlayer("Overall: "+floatfmt.format(tps)+" TPS ("+(int)((tps / MAX_TPS) * 100)+"%)");
			
			for (World world : ServerMod.server.worldServers) {
				tps = getTps(world);
				var1.sendChatToPlayer("World "+world.provider.dimensionId+": "+floatfmt.format(tps)+" TPS ("+(int)((tps / MAX_TPS) * 100)+"%) ["+world.provider.getDimensionName()+"]");
			}
		} else if (var2[0].toLowerCase().charAt(0) == 'o') {
			double tickms = getTickMs(null);
			double tps = getTps(null);
			
			var1.sendChatToPlayer("Overall server tick");
			var1.sendChatToPlayer("TPS: "+floatfmt.format(tps)+" TPS of "+MAX_TPS+" TPS ("+(int)((tps / MAX_TPS) * 100)+")");
			var1.sendChatToPlayer("Tick time: "+floatfmt.format(tickms)+" ms of "+MIN_TICKMS+" ms");
		} else {
			int dim;
			try {
				dim = Integer.parseInt(var2[0]);
			} catch (Throwable e) {
				throw new WrongUsageException("/"+name+" [worldid|{o}]");
			}
			
			World world = ServerMod.server.worldServerForDimension(dim);
			double tickms = getTickMs(world);
			double tps = getTps(world);
			
			var1.sendChatToPlayer("World "+world.provider.dimensionId+": "+world.provider.getDimensionName());
			var1.sendChatToPlayer("TPS: "+floatfmt.format(tps)+" TPS of "+MAX_TPS+" TPS ("+(int)((tps / MAX_TPS) * 100)+")");
			var1.sendChatToPlayer("Tick time: "+floatfmt.format(tickms)+" ms of "+MIN_TICKMS+" ms");
		}
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return ServerMod.instance.settings.getBoolean("require-op-tps") ? 2 : 0;
	}
	
	private double getTickTimeSum(long[] times) {
        long timesum = 0L;
        if (times == null) return 0;
        for (int i = 0; i < times.length; i++) {
        	timesum += times[i];
        }

        return (double)(timesum / times.length);
    }
	
	private double getTickMs(World world) {
		return getTickTimeSum(world == null ? ServerMod.server.tickTimeArray : ServerMod.server.worldTickTimes.get(world.provider.dimensionId)) * 1.0E-6D;
	}
	
	private double getTps(World world) {
		double tps = 1000 / getTickMs(world);
		return tps > MAX_TPS ? MAX_TPS : tps;
	}
}
