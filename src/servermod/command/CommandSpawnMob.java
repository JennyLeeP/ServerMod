package servermod.command;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.PlayerNotFoundException;
import net.minecraft.src.World;
import net.minecraft.src.WrongUsageException;
import servermod.core.Command;

public class CommandSpawnMob extends Command {
	public CommandSpawnMob(String commandName) {
		super(commandName);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		EntityPlayer spawner = getCommandSenderAsPlayer(var1);
		
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage");
		
		int amount = 1;
		if (var2.length > 1) amount = parseIntBounded(var1, var2[1], 1, 100);
		
		Class clazz = null;
		String type = "Unknown";
		for (String name : ((Map<String,Class>)EntityList.stringToClassMapping).keySet()) {
			if (name.equalsIgnoreCase(var2[0])) {
				clazz = (Class)EntityList.stringToClassMapping.get(name);
				type = name;
				break;
			}
		}
		if (clazz == null || !EntityLiving.class.isAssignableFrom(clazz)) throw new PlayerNotFoundException("commands.generic.entityType.notFound");
		
		try {
			Constructor ctor = clazz.getConstructor(World.class);
			for (int i = 0; i < amount; i++) {
				Entity ent = (Entity)ctor.newInstance(spawner.worldObj);
				ent.setPosition(spawner.posX, spawner.posY, spawner.posZ);
				if (ent instanceof EntityLiving) ((EntityLiving)ent).func_82163_bD();
				spawner.worldObj.spawnEntityInWorld(ent);
			}
		} catch (Throwable e) {
			throw new PlayerNotFoundException("commands.servermod_"+commandName+".badEntity");
		}
		
		notifyAdmins(var1, "commands.servermod_"+commandName+".success", amount, type);
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
        return var2.length >= 1 ? getListOfStringsMatchingLastWord(var2, getValidEntities().toArray(new String[0])) : null;
    }
	
	private List<String> getValidEntities() {
		List<String> ret = new ArrayList<String>();
		for (String name : ((Map<String,Class>)EntityList.stringToClassMapping).keySet()) {
			Class clazz = (Class)EntityList.stringToClassMapping.get(name);
			if (EntityLiving.class.isAssignableFrom(clazz)) ret.add(name);
		}
		return ret;
	}
}
