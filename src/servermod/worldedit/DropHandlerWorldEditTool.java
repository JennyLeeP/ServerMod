package servermod.worldedit;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.item.ItemTossEvent;

public class DropHandlerWorldEditTool {
	private WorldEdit we;
	
	public DropHandlerWorldEditTool(WorldEdit we) {
		this.we = we;
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@ForgeSubscribe
	public void onItemToss(ItemTossEvent event) {
		if (event.entityItem.item.itemID == we.tool.itemID) {
			event.setCanceled(true);
		}
	}
}
