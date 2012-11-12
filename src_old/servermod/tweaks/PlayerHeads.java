package servermod.tweaks;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

public class PlayerHeads {
	public PlayerHeads() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@ForgeSubscribe
	public void onPlayerDrops(PlayerDropsEvent event) {
		ItemStack head = new ItemStack(Item.field_82799_bQ, 1, 3);
		head.stackTagCompound = new NBTTagCompound();
		head.stackTagCompound.setString("SkullOwner", event.entityPlayer.username);
		event.entityPlayer.dropPlayerItem(head);
	}
}
