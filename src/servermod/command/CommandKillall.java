package servermod.command;

import java.util.List;
import java.util.Set;

import servermod.core.Command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.PlayerNotFoundException;
import net.minecraft.src.WrongUsageException;

public class CommandKillall extends Command {
	public CommandKillall(String commandName) {
		super(commandName);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
		
		String name = null;
		String pname = joinString(var1, var2, 0);
		for (String ename : (Set<String>)EntityList.stringToClassMapping.keySet()) {
			if (ename.equalsIgnoreCase(pname)) name = ename;
		}
		
		if (name == null) throw new PlayerNotFoundException("commands.generic.entityType.notFound", new Object[0]);
		
		int removed = 0;
		EntityPlayer player = (EntityPlayer)var1;
		for (Entity ent : (List<Entity>)player.worldObj.loadedEntityList) {
			String string = EntityList.getEntityString(ent);
			if (string != null && string.equalsIgnoreCase(name) && !(ent instanceof EntityPlayer)) {
				ent.setDead();
				removed++;
			}
		}
		
		notifyAdmins(var1, "commands.servermod_"+commandName+".success", removed, name, ((Class)EntityList.stringToClassMapping.get(name)).getName());
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
        return var2.length >= 1 ? getListOfStringsMatchingLastWord(var2, (String[])EntityList.stringToClassMapping.keySet().toArray(new String[0])) : null;
    }
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return var1 instanceof EntityPlayer && super.canCommandSenderUseCommand(var1);
	}
}
