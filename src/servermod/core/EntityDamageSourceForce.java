package servermod.core;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityDamageSource;

public class EntityDamageSourceForce extends EntityDamageSource {
	public EntityDamageSourceForce(String par1Str, Entity par2Entity) {
		super(par1Str, par2Entity);
		setDamageAllowedInCreativeMode();
		setDamageBypassesArmor();
	}
}
