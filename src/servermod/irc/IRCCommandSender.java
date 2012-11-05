package servermod.irc;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.ICommandSender;

public class IRCCommandSender implements ICommandSender {
	private final IRC irc;
	private final String username;
	
	public IRCCommandSender(IRC irc, String username) {
		this.irc = irc;
		this.username = username;
	}
	
	@Override
	public String getCommandSenderName() {
		return username;
	}

	@Override
	public void sendChatToPlayer(String var1) {
		irc.bot.sendNotice(username, var1);
	}

	@Override
	public boolean canCommandSenderUseCommand(int var1, String var2) {
		return var1 <= 0; // only user commands
	}

	@Override
	public String translateString(String var1, Object... var2) {
		return null;
	}

	@Override
	public ChunkCoordinates func_82114_b() {
		return new ChunkCoordinates(0, 0, 0);
	}
}
