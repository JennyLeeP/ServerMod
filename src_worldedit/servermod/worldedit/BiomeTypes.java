package servermod.worldedit;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.BiomeGenBase;

import com.sk89q.worldedit.UnknownBiomeTypeException;

public class BiomeTypes implements com.sk89q.worldedit.BiomeTypes {
	@Override
	public List<com.sk89q.worldedit.BiomeType> all() {
		List<com.sk89q.worldedit.BiomeType> ret = new ArrayList<com.sk89q.worldedit.BiomeType>();
		
		for (BiomeGenBase biome : BiomeGenBase.biomeList) {
			if (biome != null) ret.add(new BiomeType(biome));
		}
		
		return ret;
	}

	@Override
	public com.sk89q.worldedit.BiomeType get(String arg0) throws UnknownBiomeTypeException {
		for (com.sk89q.worldedit.BiomeType biome : all()) {
			if (biome.getName().equalsIgnoreCase(arg0)) return biome;
		}
		
		throw new UnknownBiomeTypeException(arg0);
	}

	@Override
	public boolean has(String arg0) {
		for (com.sk89q.worldedit.BiomeType biome : all()) {
			if (biome.getName().equalsIgnoreCase(arg0)) return true;
		}
		
		return false;
	}
}
