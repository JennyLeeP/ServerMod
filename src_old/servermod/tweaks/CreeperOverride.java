package servermod.tweaks;

import net.minecraft.src.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class CreeperOverride {
	public CreeperOverride() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@ForgeSubscribe
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (event.entity instanceof net.minecraft.src.EntityCreeper && !(event.entity instanceof EntityCreeper)) {
			World world = event.entity.worldObj;
			
			EntityCreeper creeper = new EntityCreeper(world);
			creeper.setPositionAndRotation(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, event.entity.rotationPitch);
			if (event.entity.getDataWatcher().getWatchableObjectByte(17) == 1) { // powered creeper
				creeper.getDataWatcher().updateObject(17, (byte)1);
			}
			
			event.entity.setDead();
			world.spawnEntityInWorld(creeper);
		}
	}
}
