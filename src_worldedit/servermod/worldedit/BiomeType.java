package servermod.worldedit;

import net.minecraft.src.BiomeGenBase;

public class BiomeType implements com.sk89q.worldedit.BiomeType {
	protected BiomeGenBase biome;
	
	public BiomeType(BiomeGenBase biome) {
		this.biome = biome;
	}

	@Override
	public String getName() {
		return biome.biomeName;
	}
}
