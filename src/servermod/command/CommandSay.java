package servermod.command;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.WrongUsageException;

public class CommandSay extends Command {
	public CommandSay() {
		super("say");
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("/"+name+" message");
		
		PacketDispatcher.sendPacketToAllPlayers(new Packet3Chat("\u00a7d["+var1.getCommandSenderName()+"] "+func_82360_a(var1, var2, 0)));
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
}
