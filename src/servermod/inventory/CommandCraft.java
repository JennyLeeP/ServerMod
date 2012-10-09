package servermod.inventory;

import java.util.Iterator;

import net.minecraft.src.Container;
import net.minecraft.src.ContainerWorkbench;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.IInventory;
import net.minecraft.src.IRecipe;
import net.minecraft.src.InventoryBasic;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.SlotCrafting;
import servermod.core.Command;

public class CommandCraft extends Command {
	private Inventory inventory;
	
	public CommandCraft(String commandName, Inventory inventory) {
		super(commandName);
		this.inventory = inventory;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		EntityPlayerMP player = (EntityPlayerMP)getCommandSenderAsPlayer(var1);
		
		if (inventory.sm.settings.inventory_crafting_use_inventory) {
			int times = var2.length < 1 ? 1 : parseIntBounded(var1, var2[0], 1, 64);
			
			InventoryCraftingWrapper craftMatrix = new InventoryCraftingWrapper(player);
			
			int crafted = 0;
			for (int i = 0; i < times; i++) {
				ItemStack stack = CraftingManager.getInstance().findMatchingRecipe(craftMatrix);
				if (stack != null) {
					SlotCrafting slot = new SlotCrafting(player, craftMatrix, new InventoryBasic("", 1), 0, 0, 0);
					slot.onPickupFromSlot(stack);
					player.inventory.addItemStackToInventory(stack.copy());
					crafted++;
				} else break;
			}
			
			var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".success", crafted));
		} else {
			player.displayGUIWorkbench((int)player.posX, (int)player.posY, (int)player.posZ);
		}
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return var1 instanceof EntityPlayerMP && inventory.sm.settings.inventory_crafting_enable && (!inventory.sm.settings.inventory_crafting_require_op || super.canCommandSenderUseCommand(var1));
	}
	
	private static class InventoryCraftingWrapper extends InventoryCrafting {
		private EntityPlayer player;
		
		public InventoryCraftingWrapper(EntityPlayer player) {
			super(null, 3, 3);
			this.player = player;
		}

		@Override
		public ItemStack getStackInSlot(int var1) {
			if (var1 < 3) {
				return player.inventory.mainInventory[9 + var1];
			} else if (var1 < 6) {
				return player.inventory.mainInventory[15 + var1];
			} else {
				return player.inventory.mainInventory[21 + var1];
			}
		}

		@Override
		public void setInventorySlotContents(int var1, ItemStack var2) {
			if (var1 < 3) {
				player.inventory.mainInventory[9 + var1] = var2;
			} else if (var1 < 6) {
				player.inventory.mainInventory[15 + var1] = var2;
			} else {
				player.inventory.mainInventory[21 + var1] = var2;
			}
			
			onInventoryChanged();
		}

		@Override
		public void onInventoryChanged() {
			player.inventory.onInventoryChanged();
		}
		
		@Override
		public ItemStack decrStackSize(int var1, int var2) {
			ItemStack stack = getStackInSlot(var1);
			if (stack != null) {
				if (stack.stackSize <= var2) {
					ItemStack ret = stack;
					setInventorySlotContents(var1, null);
					onInventoryChanged();
					return ret;
				} else {
					ItemStack ret = stack.splitStack(var2);
					if (stack.stackSize == 0) stack = null;
					onInventoryChanged();
					return ret;
				}
			} else return null;
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int var1) {
			ItemStack stack = getStackInSlot(var1);
			if (stack != null) {
				ItemStack ret = stack;
				setInventorySlotContents(var1, null);
				return ret;
			} else return null;
		}
	}
	
	private static class ContainerDummy extends Container {
		@Override
		public boolean canInteractWith(EntityPlayer var1) {
			return true;
		}
	}
}
