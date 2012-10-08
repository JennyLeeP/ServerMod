package servermod.inventory;

import java.util.Arrays;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.PlayerNotFoundException;
import net.minecraft.src.WrongUsageException;
import servermod.core.Command;

public class CommandInventory extends Command {
	private Inventory inv;
	
	public CommandInventory(String commandName, Inventory inv) {
		super(commandName);
		this.inv = inv;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage");
		
		EntityPlayerMP player = (EntityPlayerMP)getCommandSenderAsPlayer(var1);
		EntityPlayerMP target = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(var2[0]);
		if (target == null) throw new PlayerNotFoundException();
		
		player.displayGUIChest(new InventoryPlayerWrapper(player, target));
		
		notifyAdmins(var1, "commands.servermod_"+commandName+".success", target.username);
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return var1 instanceof EntityPlayer && super.canCommandSenderUseCommand(var1);
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
        return var2.length >= 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
    }
	
	@Override
	public List getCommandAliases() {
		return Arrays.asList("inv");
	}
	
	private static class InventoryPlayerWrapper implements IInventory {
		private EntityPlayerMP viewer;
		private EntityPlayer player;
		
		public InventoryPlayerWrapper(EntityPlayerMP viewer, EntityPlayer player) {
			this.viewer = viewer;
			this.player = player;
		}
		
		@Override
		public int getSizeInventory() {
			if (player == null || player.isDead) {
				viewer.closeScreen();
			}
			
			return 45;
		}

		@Override
		public ItemStack getStackInSlot(int var1) {
			if (player == null || player.isDead) {
				viewer.closeScreen();
				return null;
			}
			
			if (var1 >= 0 && var1 < 27) {
				return player.inventory.mainInventory[var1 + 9];
			} else if (var1 >= 27 && var1 < 36) {
				return player.inventory.mainInventory[var1 - 27];
			} else if (var1 >= 36 && var1 < 40) {
				return player.inventory.armorInventory[39 - var1];
			} else return null;
		}

		@Override
		public ItemStack decrStackSize(int var1, int var2) {
			if (player == null || player.isDead) {
				viewer.closeScreen();
				return null;
			}
			
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
			if (player == null || player.isDead) {
				viewer.closeScreen();
				return null;
			}
			
			ItemStack stack = getStackInSlot(var1);
			if (stack != null) {
				ItemStack ret = stack;
				setInventorySlotContents(var1, null);
				return ret;
			} else return null;
		}

		@Override
		public void setInventorySlotContents(int var1, ItemStack var2) {
			if (player == null || player.isDead) {
				viewer.dropPlayerItem(var2);
				viewer.closeScreen();
				return;
			}
			
			if (var1 >= 0 && var1 < 27) {
				player.inventory.mainInventory[var1 + 9] = var2;
			} else if (var1 >= 27 && var1 < 36) {
				player.inventory.mainInventory[var1 - 27] = var2;
			} else if (var1 >= 36 && var1 < 40) {
				player.inventory.armorInventory[39 - var1] = var2;
			} else {
				viewer.dropPlayerItem(var2);
			}
		}

		@Override
		public String getInvName() {
			if (player == null || player.isDead) {
				viewer.closeScreen();
				return "Unknown";
			}
			
			return player.username;
		}

		@Override
		public int getInventoryStackLimit() {
			if (player == null || player.isDead) {
				viewer.closeScreen();
				return 64;
			}
			
			return player.inventory.getInventoryStackLimit();
		}

		@Override
		public void onInventoryChanged() {
			if (player == null || player.isDead) {
				viewer.closeScreen();
			}
			
			player.inventory.onInventoryChanged();
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer var1) {
			if (player == null || player.isDead) {
				viewer.closeScreen();
				return false;
			}
			
			return true;
		}

		@Override
		public void openChest() {
			if (player == null || player.isDead) {
				viewer.closeScreen();
			}			
		}

		@Override
		public void closeChest() {
			// putting a check here causes a stack overflow
		}
	}
}
