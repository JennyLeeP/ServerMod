package servermod.worldedit;

import net.minecraft.src.Block;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntityLiving;

import com.sk89q.worldedit.BiomeTypes;

import cpw.mods.fml.common.registry.TickRegistry;

public class ServerInterface extends com.sk89q.worldedit.ServerInterface {
	private final servermod.worldedit.BiomeTypes biomeTypes;
	
	public ServerInterface() {
		this.biomeTypes = new servermod.worldedit.BiomeTypes();
	}
	
	@Override
	public BiomeTypes getBiomes() {
		return biomeTypes;
	}

	@Override
	public boolean isValidMobType(String arg0) {
		return EntityList.stringToClassMapping.containsKey(arg0) && EntityLiving.class.isAssignableFrom((Class)EntityList.stringToClassMapping.get(arg0));
	}

	@Override
	public void reload() {
		
	}

	@Override
	public int resolveItem(String arg0) {
		return 0;
	}
}
