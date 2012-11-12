package servermod.inventory;

import net.minecraft.src.ContainerChest;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import servermod.core.Command;

public class CommandEnderChest extends Command {
	private Inventory inventory;
	
	public CommandEnderChest(String commandName, Inventory inventory) {
		super(commandName);
		this.inventory = inventory;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		EntityPlayerMP player = (EntityPlayerMP)getCommandSenderAsPlayer(var1);
		player.displayGUIChest(player.getInventoryEnderChest());
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return var1 instanceof EntityPlayerMP && inventory.sm.settings.inventory_enderchest_enable && (!inventory.sm.settings.inventory_enderchest_require_op || super.canCommandSenderUseCommand(var1));
	}
}
