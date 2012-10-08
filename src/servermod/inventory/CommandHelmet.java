package servermod.inventory;

import java.util.Arrays;
import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import servermod.core.Command;

public class CommandHelmet extends Command {
	private Inventory inv;
	
	public CommandHelmet(String commandName, Inventory inv) {
		super(commandName);
		this.inv = inv;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		EntityPlayer player = getCommandSenderAsPlayer(var1);
		
		ItemStack temp = player.inventory.mainInventory[player.inventory.currentItem];
		if (temp != null && !(temp.getItem() instanceof ItemBlock)) {
			var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".fail"));
			return;
		}
		
		player.inventory.mainInventory[player.inventory.currentItem] = player.inventory.armorInventory[3];
		player.inventory.armorInventory[3] = temp;
		player.inventory.onInventoryChanged();
		
		var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".success"));
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return var1 instanceof EntityPlayer && (!inv.sm.settings.inventory_helmet_require_op || super.canCommandSenderUseCommand(var1));
	}
	
	@Override
	public List getCommandAliases() {
		return Arrays.asList("helm");
	}
}
