package servermod.worldedit;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.TileEntity;

public class PlayerData {
	public final String username;
	public int[] selA = new int[3];
	public int[] selB = new int[3];
	public int[] clipboardSize = new int[3];
	public short[] clipboard;
	public byte[] clipboardMeta;
	public List<TileEntity> clipboardTiles = new ArrayList<TileEntity>();
	
	public PlayerData(String username) {
		this.username = username;
	}
}
