package servermod.worldedit;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.src.EnchantmentHelper;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityArrow;
import net.minecraft.src.EntityBoat;
import net.minecraft.src.EntityFallingSand;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityPainting;
import net.minecraft.src.EntityTNTPrimed;
import net.minecraft.src.EntityXPOrb;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;

import com.sk89q.worldedit.EntityType;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;

public class WorldEditUtils {
	public static BaseItemStack stackToBaseItemStack(ItemStack stack) {
		if (stack == null) return null;
		
		BaseItemStack is = new BaseItemStack(stack.itemID, stack.stackSize, (short)stack.getItemDamage());
		
		if (stack.isItemEnchanted()) {
			Map enchants = EnchantmentHelper.getEnchantments(stack);
			for (Object o : enchants.keySet()) {
				is.getEnchantments().put((Integer)o, (Integer)enchants.get(o));
			}
		}
		
		return is;
	}
	
	public static ItemStack baseItemStackToStack(BaseItem stack) {
		if (stack == null) return null;
		
		ItemStack is;
		if (stack instanceof BaseItemStack) {
			is = new ItemStack(stack.getType(), ((BaseItemStack)stack).getAmount(), stack.getData());
		} else {
			is = new ItemStack(stack.getType(), 1, stack.getData());
		}
		
		if (!stack.getEnchantments().isEmpty()) {
			EnchantmentHelper.setEnchantments(stack.getEnchantments(), is);
		}
		
		return is;
	}
	
	public static BaseItemStack[] inventoryToBaseItemStack(IInventory inv) {
		if (inv == null) return null;
		
		BaseItemStack[] ret = new BaseItemStack[inv.getSizeInventory()];
		
		for (int i = 0; i < ret.length; i++) {
			ret[i] = stackToBaseItemStack(inv.getStackInSlot(i));
		}
		
		return ret;
	}
	
	public static void baseItemStackToInventory(BaseItemStack[] stacks, IInventory inv) {
		if (inv == null || stacks == null) return;
		
		for (int i = 0; i < stacks.length; i++) {
			inv.setInventorySlotContents(i, baseItemStackToStack(stacks[i]));
		}
	}
	
	public static Class<? extends Entity> getEntityType(EntityType type) {
		switch (type) {
			case ARROWS: return EntityArrow.class;
			case BOATS: return EntityBoat.class;
			case FALLING_BLOCKS: return EntityFallingSand.class;
			case ITEMS: return EntityItem.class;
			case MINECARTS: return EntityMinecart.class;
			case PAINTINGS: return EntityPainting.class;
			case TNT: return EntityTNTPrimed.class;
			case XP_ORBS: return EntityXPOrb.class;
			default: return Entity.class;
		}
	}
}
