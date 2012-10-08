package servermod.worldedit;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class PlayerData {
	public final String username;
	public int[] selA = new int[3];
	public int[] selB = new int[3];
	public int[] clipboardSize = new int[3];
	public short[] clipboard;
	public byte[] clipboardMeta;
	public List<NBTTagCompound> clipboardTiles = new ArrayList<NBTTagCompound>();
	public List<NBTTagCompound> clipboardEntities = new ArrayList<NBTTagCompound>();
	
	public PlayerData(String username) {
		this.username = username;
	}
}
