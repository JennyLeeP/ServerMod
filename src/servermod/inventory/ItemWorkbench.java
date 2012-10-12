package servermod.inventory;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemWorkbench extends ItemBlock {
	private Inventory inv;
	
	public ItemWorkbench(int id, Inventory inv) {
		super(id);
		this.inv = inv;
	}

	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) return stack;
		
		inv.commandCraft.processCommand(player, new String[] {"1"});
		
		return stack;
	}
}
