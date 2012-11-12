package servermod.command;

import java.lang.reflect.Field;
import java.util.HashMap;

import cpw.mods.fml.common.ObfuscationReflectionHelper;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.PlayerNotFoundException;
import net.minecraft.src.WrongUsageException;
import servermod.core.Command;
import servermod.util.Util;

public class CommandConfig extends Command {
	private final HashMap<String,Setting> settings = new HashMap<String,Setting>();
	
	public CommandConfig(String commandName) {
		super(commandName);
		
		MinecraftServer server = MinecraftServer.getServer();
		settings.put("online-mode", new Setting(server, ObfuscationReflectionHelper.obfuscation ? "x" : "onlineMode"));
		settings.put("spawn-animals", new Setting(server, ObfuscationReflectionHelper.obfuscation ? "y" : "canSpawnAnimals"));
		settings.put("spawn-npcs", new Setting(server,  ObfuscationReflectionHelper.obfuscation ? "z" : "canSpawnNPCs"));
		settings.put("pvp", new Setting(server, ObfuscationReflectionHelper.obfuscation ? "A" : "pvpEnabled"));
		settings.put("allow-flight", new Setting(server, ObfuscationReflectionHelper.obfuscation ? "B" : "allowFlight"));
		settings.put("texture-pack", new Setting(server, ObfuscationReflectionHelper.obfuscation ? "P" : "texturePack"));
		settings.put("motd", new Setting(server, ObfuscationReflectionHelper.obfuscation ? "C" : "motd"));
	
		if (server.isDedicatedServer()) {
			settings.put("generate-structures", new Setting(server, ObfuscationReflectionHelper.obfuscation ? "p" : "canSpawnStructures"));
			settings.put("gamemode", new Setting(server, ObfuscationReflectionHelper.obfuscation ? "q" : "gameType"));
		}
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 2) throw new WrongUsageException("commands.servermod_"+commandName+".usage");
		
		if (!settings.containsKey(var2[0])) throw new PlayerNotFoundException("commands.servermod_"+commandName+".notFound");
		
		Setting setting = settings.get(var2[0]);
		if (setting.target.getType() == String.class) {
			setting.set(joinString(var1, var2, 1));
		} else if (setting.target.getType() == Boolean.TYPE) {
			try {
				setting.set(Util.parseBoolean(var2[1]));
			} catch (IllegalArgumentException e) {
				throw new WrongUsageException("commands.servermod_"+commandName+".usage");
			}
		} else if (setting.target.getType() == Integer.TYPE) {
			try {
				setting.set(Integer.parseInt(var2[1]));
			} catch (NumberFormatException e) {
				throw new WrongUsageException("commands.servermod_"+commandName+".usage");
			}
		} else if (setting.target.getType() == EnumGameType.class) {
			try {
				setting.set(EnumGameType.getByID(Integer.parseInt(var2[1])));
			} catch (NumberFormatException e) {
				throw new WrongUsageException("commands.servermod_"+commandName+".usage");
			}
		} else {
			throw new RuntimeException("Unknown type "+setting.target.getType().getName());
		}
		
		notifyAdmins(var1, "commands.servermod_"+commandName+".success", var2[0]);
	}
	
	private class Setting {
		public final Object obj;
		public final Field target;
		
		public Setting(Object obj, String field) {
			this.obj = obj;
			try {
				target = Util.getDeclaredFieldTraverse(obj.getClass(), field);
				target.setAccessible(true);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		
		public void set(Object value) {
			try {
				target.set(obj, value);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}
}
