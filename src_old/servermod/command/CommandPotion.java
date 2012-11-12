package servermod.command;

import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.common.ObfuscationReflectionHelper;

import servermod.core.Command;
import servermod.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Enchantment;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.CommandBase;
import net.minecraft.src.PlayerNotFoundException;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.WrongUsageException;

public class CommandPotion extends Command {
	public CommandPotion(String commandName) {
		super(commandName);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
		
		int baseIndex = 2;
		EntityPlayer player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(var2[0]);
		if (player == null) {
			baseIndex = 1;
			player = getCommandSenderAsPlayer(var1);
		}
		
		if (var2[0].equalsIgnoreCase("add")) add(var1, player, Arrays.copyOfRange(var2, baseIndex, var2.length));
		else if (var2[0].equalsIgnoreCase("remove")) remove(var1, player, Arrays.copyOfRange(var2, baseIndex, var2.length));
		else if (var2[0].equalsIgnoreCase("clear")) clear(var1, player, Arrays.copyOfRange(var2, baseIndex, var2.length));
		else throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
        return var2.length >= 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
    }
	
	private void add(ICommandSender var1, EntityPlayer player, String[] var2) {
		if (var2.length < 2) throw new WrongUsageException("commands.servermod_"+commandName+".usage", new Object[0]);
		
		int potion = parseIntBounded(var1, var2[0], 0, Potion.potionTypes.length);
		if (Potion.potionTypes[potion] == null) throw new PlayerNotFoundException("commands.servermod_"+commandName+".badPotion", potion);
		
		int potency = 1;
	    if (var2.length > 2) potency = parseIntBounded(var1, var2[2], ObfuscationReflectionHelper.obfuscation ? 1 : -127, 127);
	    
	    int duration = parseIntBounded(var1, var2[1], 1, 3600);
	    
	    PotionEffect pe = new PotionEffect(potion, duration*20, potency-1);
	    player.addPotionEffect(pe);
	    
	    notifyAdmins(var1, "commands.servermod_"+commandName+".add.success", Util.getPotionEffectString(var1, pe), duration, player.username);
	}
	
	private void remove(ICommandSender var1, EntityPlayer player, String[] var2) {
		var1.sendChatToPlayer("TODO");
	}
	
	private void clear(ICommandSender var1, EntityPlayer player, String[] var2) {
		player.clearActivePotions();
		
		notifyAdmins(var1, "commands.servermod_"+commandName+".clear.success", player.username);
	}
}
