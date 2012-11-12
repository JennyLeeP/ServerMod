package servermod.tweaks;

import net.minecraft.src.EntitySmallFireball;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemFireball extends net.minecraft.src.ItemFireball {
	public ItemFireball(int id) {
		super(id);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) return stack;
		
		if (!player.capabilities.isCreativeMode) stack.stackSize--;
		
		world.playAuxSFX(1009, (int)player.posX, (int)player.posY, (int)player.posZ, 0);
		
		double yaw = Math.toRadians(player.rotationYaw);
        double pitch = Math.toRadians(player.rotationPitch);
        
        double x = player.posX - Math.cos(yaw) * 0.16d;
        double z = player.posZ - Math.sin(yaw) * 0.16d;
        
        double startMotionX = -Math.sin(yaw) * Math.cos(pitch);
		double startMotionY = -Math.sin(pitch);
		double startMotionZ = Math.cos(yaw) * Math.cos(pitch);
		
		EntitySmallFireball firecharge = new EntitySmallFireball(world, player.posX, player.posY + player.getEyeHeight() - 0.1D, player.posZ, startMotionX, startMotionY, startMotionZ);
		firecharge.shootingEntity = player;
		firecharge.yOffset = player.yOffset;
        firecharge.setAngles(player.rotationYaw, player.rotationPitch);
        firecharge.setPosition(firecharge.posX, firecharge.posY, firecharge.posZ);
        world.spawnEntityInWorld(firecharge);
		
		return stack;
	}
}
