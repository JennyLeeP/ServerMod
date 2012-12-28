package servermod.command;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.world.World;

public class CommandSpawnMob extends Command {
	public CommandSpawnMob() {
		super("spawnmob");
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		EntityPlayer spawner = getCommandSenderAsPlayer(var1);
		
		if (var2.length < 1) throw showUsage(var1);
		
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
		if (clazz == null || !EntityLiving.class.isAssignableFrom(clazz)) throw new PlayerNotFoundException("That entity type is unknown");
		
		try {
			Constructor ctor = clazz.getConstructor(World.class);
			for (int i = 0; i < amount; i++) {
				Entity ent = (Entity)ctor.newInstance(spawner.worldObj);
				ent.setPosition(spawner.posX, spawner.posY, spawner.posZ);
				if (ent instanceof EntityLiving) ((EntityLiving)ent).initCreature();
				spawner.worldObj.spawnEntityInWorld(ent);
			}
		} catch (Throwable e) {
			throw new PlayerNotFoundException("That entity type is unknown");
		}
		
		notifyAdmins(var1, "Spawned "+amount+" "+type);
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/"+name+" mob [amount]";
	}

	@Override
	public List getCommandAliases() {
		return Arrays.asList("mob");
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
