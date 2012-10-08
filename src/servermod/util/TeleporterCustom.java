package servermod.util;

import net.minecraft.src.Entity;
import net.minecraft.src.Teleporter;
import net.minecraft.src.World;

public class TeleporterCustom extends Teleporter {
	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;
	
	public TeleporterCustom(double x, double y, double z, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	@Override
	public void placeInPortal(World var1, Entity var2) {
		var2.setLocationAndAngles(x, y, z, yaw, pitch);
	}
}