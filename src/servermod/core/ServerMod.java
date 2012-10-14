package servermod.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;

import servermod.command.CommandConfig;
import servermod.command.CommandDifficulty;
import servermod.command.CommandDisarm;
import servermod.command.CommandEnchant;
import servermod.command.CommandHeal;
import servermod.command.CommandKill;
import servermod.command.CommandKillall;
import servermod.command.CommandPotion;
import servermod.command.CommandSay;
import servermod.command.CommandSmite;
import servermod.command.CommandSpawnMob;
import servermod.command.CommandTp;
import servermod.command.CommandTps;
import servermod.command.CommandWeather;
import servermod.command.CommandXP;
import servermod.crashreporter.CrashReporter;
import servermod.home.Home;
import servermod.inventory.CommandInventory;
import servermod.inventory.Inventory;
import servermod.irc.IRC;
import servermod.motd.MOTD;
import servermod.tweaks.Tweaks;
import servermod.worldedit.WorldEdit;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.CommandBase;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ServerCommandManager;
import net.minecraft.src.WorldChunkManager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "ServerMod", name = "ServerMod", version = ServerMod.VERSION)
public class ServerMod {
	public static final String VERSION = "0.1";
	private static ServerMod instance;
	public MinecraftServer server;
	public Settings settings;
	public boolean hasForge = false;
	
	public IRC irc;
	public CrashReporter crashReporter;
	public Home home;
	public Inventory inventory;
	public MOTD motd;
	public WorldEdit we;
	public Tweaks tweaks;
	
	public ServerMod() {
		instance = this;
	}
	
	@ServerStarting
	public void onServerStarting(FMLServerStartingEvent event) {
		server = MinecraftServer.getServer();
		
		server.logger.log(Level.INFO, "ServerMod starting");
		
		ServerCommandManager commands = (ServerCommandManager)server.getCommandManager();
		LanguageRegistry lang = LanguageRegistry.instance();
		
		lang.addStringLocalization("commands.generic.entityType.notFound", "That entity type cannot be recognized");
		
		lang.addStringLocalization("commands.servermod_kill.usage", "/kill [player]");
		lang.addStringLocalization("commands.servermod_kill.success", "Killing %s");
		commands.registerCommand(new CommandKill("kill"));
		
		lang.addStringLocalization("commands.servermod_killall.usage", "/killall <entitytype>");
		lang.addStringLocalization("commands.servermod_killall.success", "Removing %d entities of type %s [%s]");
		commands.registerCommand(new CommandKillall("killall"));
		
		lang.addStringLocalization("commands.servermod_smite.usage", "/smite [player]");
		lang.addStringLocalization("commands.servermod_smite.success", "Smiting %s");
		commands.registerCommand(new CommandSmite("smite"));
		
		lang.addStringLocalization("commands.servermod_disarm.usage", "/disarm [player]");
		lang.addStringLocalization("commands.servermod_disarm.success", "Disarming %s");
		commands.registerCommand(new CommandDisarm("disarm"));
		
		lang.addStringLocalization("commands.servermod_enchant.usage", "/enchant {level <levelcount>}|{add <id> [potency]}|{remove <id>}|{clear}");
		lang.addStringLocalization("commands.servermod_enchant.badItem", "Currently held item is invalid");
		lang.addStringLocalization("commands.servermod_enchant.level.success", "Enchanting %s with %d levels");
		lang.addStringLocalization("commands.servermod_enchant.add.badEnchant", "Bad enchantment %d");
		lang.addStringLocalization("commands.servermod_enchant.add.success", "Enchanting %s with %s");
		lang.addStringLocalization("commands.servermod_enchant.remove.success", "Removing %s from %s");
		lang.addStringLocalization("commands.servermod_enchant.clear.success", "Clearing enchantments for %s");
		commands.registerCommand(new CommandEnchant("enchant"));
		
		lang.addStringLocalization("commands.servermod_tps.usage", "/tps [worldid]");
		lang.addStringLocalization("commands.servermod_tps.world", "World %1$d");
		lang.addStringLocalization("commands.servermod_tps.overall", "Overall");
		lang.addStringLocalization("commands.servermod_tps.short", "%1$s: %4$s TPS (%3$d%%) [%2$s]");
		lang.addStringLocalization("commands.servermod_tps.long", "%1$s: %2$s\nTPS: %4$s TPS out of %5$d TPS (%3$d%%)\nTick: %6$s ms out of %7$d ms");
		commands.registerCommand(new CommandTps("tps"));
		
		commands.registerCommand(new CommandXP("xp"));
		
		lang.addStringLocalization("commands.servermod_tp.usage", "/tp [player] {<player>}|{<[worldid] <x> <y> <z>}");
		lang.addStringLocalization("commands.servermod_tp.coordinatesDim", "Teleported %s to %d:%d,%d,%d");
		commands.registerCommand(new CommandTp("tp"));
		
		lang.addStringLocalization("commands.servermod_potion.usage", "/potion [player] {add <id> <seconds> [potency]}|{remove <id>}|{clear}");
		lang.addStringLocalization("commands.servermod_potion.badPotion", "Bad potion %d");
		lang.addStringLocalization("commands.servermod_potion.add.success", "Adding %s for %ss to %s");
		lang.addStringLocalization("commands.servermod_potion.remove.success", "Removing %s from %s");
		lang.addStringLocalization("commands.servermod_potion.clear.success", "Clearing potions for %s");
		commands.registerCommand(new CommandPotion("potion"));
		
		lang.addStringLocalization("commands.servermod_weather.weather.none", "None");
		lang.addStringLocalization("commands.servermod_weather.weather.rain", "Rain");
		lang.addStringLocalization("commands.servermod_weather.weather.thunder", "Thunderstorm");
		lang.addStringLocalization("commands.servermod_weather.success", "Changing weather to %s");
		lang.addStringLocalization("commands.servermod_weather.lightning", "Triggering lightning");
		commands.registerCommand(new CommandWeather("weather"));
		
		lang.addStringLocalization("commands.servermod_spawnmob.usage", "/spawnmob <entitytype>");
		lang.addStringLocalization("commands.servermod_spawnmob.badEntity", "That entity type cannot be spawned");
		lang.addStringLocalization("commands.servermod_spawnmob.success", "Spawned %d %s");
		commands.registerCommand(new CommandSpawnMob("spawnmob"));
		
		lang.addStringLocalization("commands.servermod_heal.usage", "/heal [player] [amount]");
		lang.addStringLocalization("commands.servermod_heal.success", "Healed %s by %d");
		commands.registerCommand(new CommandHeal("heal"));
		
		lang.addStringLocalization("commands.servermod_difficulty.usage", "/difficulty <difficulty>");
		lang.addStringLocalization("commands.servermod_difficulty.success", "Changed difficulty to %s");
		commands.registerCommand(new CommandDifficulty("difficulty"));
		
		lang.addStringLocalization("commands.servermod_config.usage", "/config <key> <value>");
		lang.addStringLocalization("commands.servermod_config.notFound", "Configuration entry not found");
		lang.addStringLocalization("commands.servermod_config.success", "Changed configuration entry %s");
		commands.registerCommand(new CommandConfig("config"));
		
		commands.registerCommand(new CommandSay("say"));
		
		lang.addStringLocalization("commands.message.display.outgoing", "[-> %s] %s");
		lang.addStringLocalization("commands.message.display.incoming", "[%s] %s");
		
		File folder = new File("servermod");
		if (!folder.exists()) folder.mkdirs();
		
		settings = new Settings(this, "servermod/servermod.properties");
		settings.load();
		settings.save();
		
		try {
			MinecraftForge.class.getName();
			hasForge = true;
		} catch (Throwable e) {}

		if (settings.enable_irc) {
			irc = new IRC(this);
		} else if (settings.enable_chat_relaying) {
			server.logger.log(Level.WARNING, "Cannot enable chat relaying without IRC");
		}
		
		if (settings.enable_crash_reporter) {
			if (settings.enable_irc) crashReporter = new CrashReporter(this);
			else server.logger.log(Level.WARNING, "Cannot enable crash reporter without IRC");
		}
		
		if (settings.enable_home) {
			if (hasForge) home = new Home(this);
			else server.logger.log(Level.WARNING, "Cannot enable home system without Forge");
		}
		
		if (settings.enable_inventory) {
			inventory = new Inventory(this);
		}
		
		if (settings.enable_motd) {
			motd = new MOTD(this);
		}
		
		if (settings.enable_worldedit) {
			we = new WorldEdit(this);
		}
		
		tweaks = new Tweaks(this);
	}
	
	@ServerStopping
	public void onServerStopping(FMLServerStoppingEvent event) {
		if (irc != null && irc.bot.isConnected()) {
			irc.bot.quitServer("Server shutting down");
			try { Thread.sleep(1000); } catch (Throwable e) {}
		}
	}
	
	public static ServerMod instance() {
		return instance;
	}
	
	public File createWorldFolder(File folder) {
		File f = new File(folder, "servermod");
		if (!f.exists()) f.mkdirs();
		return f;
	}
}
