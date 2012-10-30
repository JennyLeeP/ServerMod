package servermod.tweaks;

import java.util.logging.Level;

import net.minecraft.src.Block;
import net.minecraft.src.EntityList;
import net.minecraft.src.Item;

import servermod.core.ServerMod;

public class Tweaks {
	private final ServerMod sm;
	
	public Tweaks(ServerMod sm) {
		this.sm = sm;
		
		if (sm.settings.tweaks_throwable_firecharges) {
			Item.itemsList[Item.fireballCharge.shiftedIndex] = null;
			Item.fireballCharge = new ItemFireball(Item.fireballCharge.shiftedIndex - 256).setIconCoord(14, 2).setItemName("fireball");
		}
		
		if (sm.settings.tweaks_creeper_nogrief || sm.settings.tweaks_creeper_damagefactor != 1.0D) {
			if (sm.hasForge) {
				new CreeperOverride();
			
				EntityList.addMapping(EntityCreeper.class, "Creeper", 50, 894731, 0);
			} else sm.server.logger.log(Level.WARNING, "Tweaks: Cannot enable creeper tweaks without Forge");
		}
		
		if (sm.settings.tweaks_player_drophead) {
			new PlayerHeads();
		}
		
		if (sm.settings.tweaks_head_keepplayer) {
			Block.blocksList[Block.field_82512_cj.blockID] = null;
			new BlockSkull(Block.field_82512_cj.blockID).setHardness(1.0F).setStepSound(Block.soundStoneFootstep).setBlockName("skull").setRequiresSelfNotify(); // blocks are final :( but the ID will translate to our new skull
		}
		
		sm.server.logger.log(Level.INFO, "Tweaks: Initialized");
	}
}
