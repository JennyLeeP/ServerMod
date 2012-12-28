package servermod.rcon;

import java.io.PrintWriter;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.StatCollector;

public class RconCommandSender implements ICommandSender {
	private final String username;
	private final PrintWriter write;
	
	public RconCommandSender(String username, PrintWriter write) {
		this.username = username;
		this.write = write;
	}
	
	@Override
	public String getCommandSenderName() {
		return "RCON "+username;
	}

	@Override
	public void sendChatToPlayer(String var1) {
		write.println(var1);
	}

	@Override
	public boolean canCommandSenderUseCommand(int var1, String var2) {
		return true;
	}

	@Override
	public String translateString(String var1, Object... var2) {
		return StatCollector.translateToLocalFormatted(var1, var2);
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(0, 0, 0);
	}
}
