package servermod.command;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import servermod.core.Command;
import servermod.util.Util;

import net.minecraft.src.CommandBase;
import net.minecraft.src.CommandException;
import net.minecraft.src.Enchantment;
import net.minecraft.src.EnchantmentHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.PlayerNotFoundException;
import net.minecraft.src.WrongUsageException;

public class CommandEnchant extends Command {
	private static Field tagMap = null;
	
	public CommandEnchant(String commandName) {
		super(commandName);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
		
		if (var2[0].equalsIgnoreCase("level")) level(var1, Arrays.copyOfRange(var2, 1, var2.length));
		else if (var2[0].equalsIgnoreCase("add")) add(var1, Arrays.copyOfRange(var2, 1, var2.length));
		else if (var2[0].equalsIgnoreCase("remove")) remove(var1, Arrays.copyOfRange(var2, 1, var2.length));
		else if (var2[0].equalsIgnoreCase("clear")) clear(var1, Arrays.copyOfRange(var2, 1, var2.length));
		else throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
	}
	
	private void level(ICommandSender var1, String[] var2) {
		EntityPlayer player = getCommandSenderAsPlayer(var1);
		ItemStack stack = player.inventory.mainInventory[player.inventory.currentItem];
		
		if (stack == null || !stack.isItemEnchantable()) throw new PlayerNotFoundException("commands.servermod_"+commandName+".badItem", new Object[0]);
		
		int levels = player.experienceLevel;
		if (var2.length > 0) levels = parseIntWithMin(var1, var2[0], 1);
		if (levels < 1) levels = 1;
		
		EnchantmentHelper.addRandomEnchantment(player.getRNG(), stack, levels);
		notifyAdmins(var1, "commands.servermod_"+commandName+".level.success", var1.translateString(stack.getItemName()+".name", new Object[0]), levels);
	}
	
	private void add(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);

		EntityPlayer player = getCommandSenderAsPlayer(var1);
		ItemStack stack = player.inventory.mainInventory[player.inventory.currentItem];
		
		if (stack == null) throw new PlayerNotFoundException("commands.servermod_"+commandName+".badItem", new Object[0]);
		
		int enchant = parseIntBounded(var1, var2[0], 0, Enchantment.enchantmentsList.length);
		if (Enchantment.enchantmentsList[enchant] == null) throw new PlayerNotFoundException("commands.servermod_"+commandName+".badEnchant", enchant);
		
		int potency = 1;
	    if (var2.length > 1) potency = parseIntBounded(var1, var2[1], 1, 127);
	    
	    stack.addEnchantment(Enchantment.enchantmentsList[enchant], potency);
		
		notifyAdmins(var1, "commands.servermod_"+commandName+".add.success", var1.translateString(stack.getItemName()+".name", new Object[0]), Util.getEnchantmentName(enchant, potency));
	}
	
	private void remove(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
		
		EntityPlayer player = getCommandSenderAsPlayer(var1);
		ItemStack stack = player.inventory.mainInventory[player.inventory.currentItem];
		
		if (stack == null || stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("ench")) throw new PlayerNotFoundException("commands.servermod_"+commandName+".badItem", new Object[0]);
		
		int enchant = parseIntBounded(var1, var2[0], 0, Enchantment.enchantmentsList.length);
		if (Enchantment.enchantmentsList[enchant] == null) throw new PlayerNotFoundException("commands.servermod_"+commandName+".badEnchant", enchant);
		
		NBTTagList ench;
		try {
			ench = (NBTTagList)((Map)tagMap.get(stack.stackTagCompound)).get("ench");
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		
		int potency = 1;
		NBTTagList newEnch = new NBTTagList();
		for (int i = 0; i < ench.tagCount(); i++) {
			NBTTagCompound enchEntry = (NBTTagCompound)ench.tagAt(i);
			if (enchEntry.getShort("ench") != enchant) {
				newEnch.appendTag(enchEntry);
			} else {
				potency = enchEntry.getByte("lvl");
			}
		}
		
		stack.stackTagCompound.setTag("ench", newEnch);
		
		notifyAdmins(var1, "commands.servermod_"+commandName+".remove.success", Util.getEnchantmentName(enchant, potency), var1.translateString(stack.getItemName()+".name", new Object[0]));
	}
	
	private void clear(ICommandSender var1, String[] var2) {
		EntityPlayer player = getCommandSenderAsPlayer(var1);
		ItemStack stack = player.inventory.mainInventory[player.inventory.currentItem];
		
		if (stack == null || stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("ench")) throw new PlayerNotFoundException("commands.servermod_"+commandName+".badItem", new Object[0]);
		
		try {
			((Map)tagMap.get(stack.stackTagCompound)).remove("ench");
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		
		notifyAdmins(var1, "commands.servermod_"+commandName+".clear.success", var1.translateString(stack.getItemName()+".name", new Object[0]));
	}
	
	static {
		for (Field f : NBTTagCompound.class.getDeclaredFields()) {
			if (f.getType() == Map.class) {
				tagMap = f;
				tagMap.setAccessible(true);
				break;
			}
		}
	}
}
