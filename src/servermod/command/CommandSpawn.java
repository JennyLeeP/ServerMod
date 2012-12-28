package servermod.command;

import servermod.core.ServerMod;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.entity.Entity;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;

public class CommandSpawn extends Command {
	public CommandSpawn() {
		super("spawn");
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (!(var1 instanceof Entity)) throw new PlayerNotFoundException("This command must be used by a player");
		
		Entity ent = (Entity)var1;
		ChunkCoordinates point = ent.worldObj.getSpawnPoint();
		ent.setPosition(point.posX + 0.5D, point.posY + 0.5D, point.posZ + 0.5D);
		
		var1.sendChatToPlayer("Teleported to the spawn point");
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/"+name;
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return ServerMod.instance.settings.getBoolean("require-op-spawn") ? 4 : 0;
	}
}
