package servermod.irc;

import java.util.Arrays;

import net.minecraft.src.ICommandSender;
import net.minecraft.src.PlayerNotFoundException;
import net.minecraft.src.WrongUsageException;
import servermod.core.Command;

public class CommandIRC extends Command {
	private IRC irc;
	
	public CommandIRC(String commandName, IRC irc) {
		super(commandName);
		this.irc = irc;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
		
		if (var2[0].equalsIgnoreCase("connect")) connect(var1, Arrays.copyOfRange(var2, 1, var2.length));
		else if (var2[0].equalsIgnoreCase("disconnect")) disconnect(var1, Arrays.copyOfRange(var2, 1, var2.length));
		else if (var2[0].equalsIgnoreCase("say")) say(var1, Arrays.copyOfRange(var2, 1, var2.length));
		else if (var2[0].equalsIgnoreCase("nick")) nick(var1, Arrays.copyOfRange(var2, 1, var2.length));
		else throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
	}
	
	private void connect(ICommandSender var1, String[] var2) {
		if (irc.bot.isConnected()) throw new PlayerNotFoundException("commands.servermod_"+commandName+".connect.alreadyConnected");
		
		notifyAdmins(var1, "commands.servermod_"+commandName+".connect.success");
		
		if (irc.bot.isConnected()) var1.sendChatToPlayer("Connect successful");
		else var1.sendChatToPlayer(var1.translateString(""));
	}
	
	private void disconnect(ICommandSender var1, String[] var2) {
		if (!irc.bot.isConnected()) throw new PlayerNotFoundException("commands.servermod_"+commandName+".notConnected");
		
		irc.bot.quitServer(var2.length < 1 ? "Help! "+var1.getCommandSenderName()+" is quitting me!" : var2[0]);
	}
	
	private void say(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
		if (!irc.bot.isConnected()) throw new PlayerNotFoundException("commands.servermod_"+commandName+".notConnected");
		
		irc.bot.sendMessage(irc.sm.settings.irc_channel, "["+var1.getCommandSenderName()+"] "+joinString(var1, var2, 0));
	}
	
	private void nick(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
		if (!irc.bot.isConnected()) throw new PlayerNotFoundException("commands.servermod_"+commandName+".notConnected");
		
		irc.bot.changeNick(var2[0]);
		
		notifyAdmins(var1, "commands.servermod_"+commandName+".nick.success", var2[0]);
	}
}
