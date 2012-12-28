package servermod.command;

import java.text.DecimalFormat;

import servermod.core.ServerMod;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.world.World;
import net.minecraft.command.WrongUsageException;

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
			var1.sendChatToPlayer("TPS: "+floatfmt.format(tps)+" TPS of "+floatfmt.format(MAX_TPS)+" TPS ("+(int)((tps / MAX_TPS) * 100)+"%)");
			var1.sendChatToPlayer("Tick time: "+floatfmt.format(tickms)+" ms of "+floatfmt.format(MIN_TICKMS)+" ms");
		} else {
			int dim;
			try {
				dim = Integer.parseInt(var2[0]);
			} catch (Throwable e) {
				throw showUsage(var1);
			}
			
			World world = ServerMod.server.worldServerForDimension(dim);
			if (world == null) throw new PlayerNotFoundException("World not found");
			
			double tickms = getTickMs(world);
			double tps = getTps(world);
			
			var1.sendChatToPlayer("World "+world.provider.dimensionId+": "+world.provider.getDimensionName());
			var1.sendChatToPlayer("TPS: "+floatfmt.format(tps)+" TPS of "+floatfmt.format(MAX_TPS)+" TPS ("+(int)((tps / MAX_TPS) * 100)+"%)");
			var1.sendChatToPlayer("Tick time: "+floatfmt.format(tickms)+" ms of "+floatfmt.format(MIN_TICKMS)+" ms");
			var1.sendChatToPlayer("Loaded chunks: "+world.getChunkProvider().getLoadedChunkCount());
			var1.sendChatToPlayer("Entities: "+world.loadedEntityList.size());
			var1.sendChatToPlayer("Tile entities: "+world.loadedTileEntityList.size());
		}
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return ServerMod.instance.settings.getBoolean("require-op-tps") ? super.canCommandSenderUseCommand(var1) : true;
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/"+name+" [worldid|{o}]";
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
