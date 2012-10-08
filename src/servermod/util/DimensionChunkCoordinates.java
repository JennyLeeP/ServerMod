package servermod.util;

import net.minecraft.src.ChunkCoordinates;

public class DimensionChunkCoordinates extends ChunkCoordinates {
	public int dimension = 0;
	
	public DimensionChunkCoordinates(int x, int y, int z, int dimension) {
		super(x, y, z);
		this.dimension = dimension;
	}
	
	public DimensionChunkCoordinates(DimensionChunkCoordinates dcc) {
		super(dcc.posX, dcc.posY, dcc.posZ);
		dimension = dcc.dimension;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof DimensionChunkCoordinates)) return false;
        else {
           	DimensionChunkCoordinates dcc = (DimensionChunkCoordinates)object;
            return posX == dcc.posX && posY == dcc.posY && this.posZ == dcc.posZ && this.dimension == dcc.dimension;
        }
	}
	
	@Override
	public int hashCode() {
        return super.hashCode() + dimension << 32;
    }
	
	public void set(int x, int y, int z, int dimension) {
		posX = x;
		posY = y;
		posZ = z;
		this.dimension = dimension;
	}
}
