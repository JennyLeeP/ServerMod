package servermod.command;

import net.minecraft.src.Entity;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.World;
import net.minecraft.src.WrongUsageException;
import net.minecraftforge.common.DimensionManager;
import servermod.core.Command;
import servermod.util.Util;

public class CommandDifficulty extends Command {
	public CommandDifficulty(String commandName) {
		super(commandName);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage");
		
		World world = var1 instanceof Entity ? ((Entity)var1).worldObj : DimensionManager.getWorld(0);
		int difficulty = parseIntBounded(var1, var2[0], 0, 3);
		
		world.difficultySetting = difficulty;
		
		notifyAdmins(var1, "commands.servermod_"+commandName+".success", Util.getDifficulty(difficulty));
	}
}
