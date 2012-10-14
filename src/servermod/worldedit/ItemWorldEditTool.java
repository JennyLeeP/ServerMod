package servermod.worldedit;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemWorldEditTool extends Item {
	private final WorldEdit we;
	
	public ItemWorldEditTool(int id, WorldEdit we) {
		super(id);
		this.we = we;
		
		setIconCoord(11, 6);
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public float getStrVsBlock(ItemStack stack, Block block, int meta) {
		return 9001.0F;
	}
	
	@Override
	public int getDamageVsEntity(Entity entity) {
		return 0;
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		we.command.sel(player, new String[] {""+x, ""+y, ""+z}, 2);
		
		return false;
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
		we.command.sel(player, new String[] {""+x, ""+y, ""+z}, 1);
		
		return true;
	}
}
