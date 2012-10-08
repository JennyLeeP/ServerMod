package servermod.core;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityPlayer;

public class DamageSourceForce extends DamageSource {
	public static DamageSourceForce genericForce = new DamageSourceForce("generic");
	
	public DamageSourceForce(String name) {
		super(name);
		setDamageBypassesArmor();
		setDamageAllowedInCreativeMode();
	}
	
	public static EntityDamageSourceForce causePlayerDamageForce(EntityPlayer player) {
		return new EntityDamageSourceForce("indirectMagic", player);
	}
}
