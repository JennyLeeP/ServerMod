package servermod.tweaks;

import java.util.ArrayList;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySkull;
import net.minecraft.src.World;

public class BlockSkull extends net.minecraft.src.BlockSkull {
	public BlockSkull(int par1) {
		super(par1);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int metadata) {
		if (world.isRemote || (metadata & 8) == 0) return;
		
		ItemStack head = new ItemStack(Item.field_82799_bQ, 1, getDamageValue(world, x, y, z));
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntitySkull) {
			TileEntitySkull skull = (TileEntitySkull)te;
			NBTTagCompound workaround = new NBTTagCompound();
			skull.writeToNBT(workaround);
			
			String owner = workaround.getString("ExtraType");
			if (owner != null && !owner.isEmpty()) {
				head.stackTagCompound = new NBTTagCompound();
				head.stackTagCompound.setString("SkullOwner", owner);
			}
		}
		
		dropBlockAsItem_do(world, x, y, z, head);
		world.removeBlockTileEntity(x, y, z);
	}
}
