package servermod.command;

import servermod.core.ServerMod;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityDamageSource;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.PlayerNotFoundException;
import net.minecraft.src.World;

public class CommandKill extends Command {
	public CommandKill() {
		super("kill");
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {System.out.println(var1+" "+var2.length+" "+(var2.length > 0 ? var2[0] : "NONE"));
		EntityPlayer player = var2.length < 1 ? getCommandSenderAsPlayer(var1) : ServerMod.server.getConfigurationManager().getPlayerForUsername(var2[0]);
		if (player == null) throw new PlayerNotFoundException();
		
		if ((var1 != player || ServerMod.instance.settings.getBoolean("require-op-kill-self")) && !var1.canCommandSenderUseCommand(2, name)) { // hack!
			var1.sendChatToPlayer("\u00a7cYou do not have permission to use this command.");
			return;
		}
		
		if (var1 == player) player.attackEntityFrom(ForcedDamageSource.forced, 32767);
		else player.attackEntityFrom(ForcedDamageSource.causeEntityDamage(var1 instanceof Entity ? (Entity)var1 : new EntityDummy(var1.getCommandSenderName())), 32767);
		
		notifyAdmins(var1, "Killing "+player.username);
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return ServerMod.instance.settings.getBoolean("require-op-kill-self") ? super.canCommandSenderUseCommand(var1) : true;
	}
	
	@Override
	public boolean isUsernameIndex(int var1) {
		return var1 == 0;
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/"+name+" [player]";
	}
	
	public static class ForcedDamageSource extends DamageSource {
		public static final ForcedDamageSource forced = new ForcedDamageSource("generic");
		
		public ForcedDamageSource(String name) {
			super(name);
		}
		
		public static ForcedEntityDamageSource causeEntityDamage(Entity entity) {
			return new ForcedEntityDamageSource("indirectMagic", entity);
		}
		
		static {
			forced.setDamageBypassesArmor();
			forced.setDamageAllowedInCreativeMode();
		}
	}
	
	public static class ForcedEntityDamageSource extends EntityDamageSource {
		public ForcedEntityDamageSource(String name, Entity entity) {
			super(name, entity);
			
			setDamageBypassesArmor();
			setDamageAllowedInCreativeMode();
		}		
	}
	
	public static class EntityDummy extends Entity {
		private final String username;
		
		public EntityDummy(String username) {
			super(ServerMod.server.worldServers[0]);
			
			this.username = username;
		}

		@Override
		public void entityInit() {}

		@Override
		public void readEntityFromNBT(NBTTagCompound var1) {}

		@Override
		public void writeEntityToNBT(NBTTagCompound var1) {}
		
		@Override
		public String getEntityName() {
			return username;
		}
	}
}
