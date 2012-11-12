package servermod.tweaks;

import servermod.core.ServerMod;
import net.minecraft.src.Entity;
import net.minecraft.src.Explosion;
import net.minecraft.src.World;

public class EntityCreeper extends net.minecraft.src.EntityCreeper {
	private int lastActiveTimeX;
	private int timeSinceIgnitedX;
	private int state;
	
	public EntityCreeper(World world) {
		super(world);
	}
	
	public void onUpdate() {
        if (this.isEntityAlive()) {
            lastActiveTimeX = timeSinceIgnitedX;
            int var1 = state;

            if (var1 == 2 && timeSinceIgnitedX == 0) worldObj.playSoundAtEntity(this, "random.fuse", 1.0F, 0.5F);

            timeSinceIgnitedX += state == 2 ? 1 : state;

            if (timeSinceIgnitedX < 0) timeSinceIgnitedX = 0;

            if (timeSinceIgnitedX >= 30) {
                timeSinceIgnitedX = 30;

                if (!worldObj.isRemote) {
                    if (getPowered()) damageOnlyExplosion(worldObj, this, posX, posY, posZ, 6.0F * ServerMod.instance().settings.tweaks_creeper_damagefactor);
                    else damageOnlyExplosion(worldObj, this, posX, posY, posZ, 3.0F * ServerMod.instance().settings.tweaks_creeper_damagefactor);

                    setDead();
                }
            }
        }

        super.onUpdate();
    }
	
	@Override
	public void setCreeperState(int state) {
		this.state = state;
		if (state == 1) {
			super.setCreeperState(1);
			this.state = 2;
		}
		else super.setCreeperState(state);
	}
	
	@Override
	public int getCreeperState() {
		if (state == 2) return -1;
		else return super.getCreeperState();
	}
	
	@Override
	public boolean isExplosiveMob(Class par1Class) {
		return true;
	}
	
	private Explosion damageOnlyExplosion(World world, Entity exploder, double x, double y, double z, float power) {
		Explosion explosion = new Explosion(world, exploder, x, y, z, power);
		explosion.doExplosionA();
		if (ServerMod.instance().settings.tweaks_creeper_nogrief) explosion.field_77281_g.clear();
		explosion.doExplosionB(true);
		return explosion;
	}
}
