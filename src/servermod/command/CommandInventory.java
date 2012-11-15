package servermod.command;

import java.util.Arrays;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.PlayerNotFoundException;

public class CommandInventory extends Command {
	public CommandInventory() {
		super("inventory");
	}
	
	@Override
	public List getCommandAliases() {
		return Arrays.asList("inv");
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (!(var1 instanceof EntityPlayerMP)) throw new PlayerNotFoundException("This command must be used by a player");
		EntityPlayerMP player = (EntityPlayerMP)getCommandSenderAsPlayer(var1);
		EntityPlayerMP target = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(var2[0]);
		if (target == null) throw new PlayerNotFoundException();
		
		player.displayGUIChest(new InventoryPlayerWrapper(player, target));
		
		notifyAdmins(var1, "Viewing the inventory for "+target.username);
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/"+name+" player";
	}
	
	public static class InventoryPlayerWrapper implements IInventory {
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
		public void closeChest() {}
	}
}
