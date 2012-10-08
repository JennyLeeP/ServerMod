package servermod.command;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.WrongUsageException;
import servermod.core.Command;
import servermod.core.ServerMod;

public class CommandSay extends Command {
	public CommandSay(String commandName) {
		super(commandName);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
		
		PacketDispatcher.sendPacketToAllPlayers(new Packet3Chat("\u00a7d["+var1.getCommandSenderName()+"] "+joinString(var2, 0)));
		
		ServerMod sm = ServerMod.instance();
		if (sm.irc != null && (sm.settings.enable_chat_relaying || var1 instanceof MinecraftServer) && sm.irc.bot.isConnected()) {
			sm.irc.messageQueue.add("["+var1.getCommandSenderName()+"] "+joinString(var2, 0));
		}
	}
	
	@Override
	public String getCommandUsage(ICommandSender var1) {
        return var1.translateString("commands.say.usage", new Object[0]);
    }
}
