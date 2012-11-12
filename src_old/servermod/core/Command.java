package servermod.core;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.StatCollector;

public abstract class Command extends CommandBase {
	protected String commandName;
	
	public Command(String commandName) {
		this.commandName = commandName;
	}
	
	@Override
	public String getCommandName() {
		return commandName;
	}
	
	@Override
	public String getCommandUsage(ICommandSender var1) {
        return var1.translateString("commands.servermod_"+commandName+".usage", new Object[0]);
    }
	
	/**
	 * Wrapper around the dedicated server not translating notifyAdmins when it should be
	 */
	public static void notifyAdmins(ICommandSender var1, String var2, Object... var3) {
		CommandBase.notifyAdmins(var1, StatCollector.translateToLocalFormatted(var2, var3), var3);
	}
	
	public static String joinString(ICommandSender sender, String[] s, int index) {
		return func_82361_a(sender, s, index, false);
	}
}
