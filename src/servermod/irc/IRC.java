package servermod.irc;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.ServerCommandManager;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;

import servermod.core.ServerMod;
import servermod.crashreporter.CrashReporter;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.IChatListener;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;

public class IRC extends ListenerAdapter implements IChatListener, IPlayerTracker, ITickHandler {
	public final PircBotX bot;
	public boolean serverCrashed = false;
	private final MinecraftServer server;
	protected final ServerMod sm;
	public Queue<String> messageQueue = new ArrayDeque<String>();
	
	public IRC(ServerMod sm) {
		this.server = MinecraftServer.getServer();
		this.sm = sm;
		
		NetworkRegistry.instance().registerChatListener(this);
		GameRegistry.registerPlayerTracker(this);
		TickRegistry.registerTickHandler(this, Side.SERVER);
		
		ServerCommandManager commands = (ServerCommandManager)sm.server.getCommandManager();
		LanguageRegistry lang = LanguageRegistry.instance();
		
		lang.addStringLocalization("commands.servermod_irc.usage", "/irc {connect}|{disconnect [message]}|{say <text>}|{nick <nick>}");
		lang.addStringLocalization("commands.servermod_irc.notConnected", "Not connected");
		lang.addStringLocalization("commands.servermod_irc.connect.alreadyConnected", "Already connected");
		lang.addStringLocalization("commands.servermod_irc.connect.success", "Connecting IRC");
		lang.addStringLocalization("commands.servermod_irc.disconnect.success", "Disconnecting IRC");
		lang.addStringLocalization("commands.servermod_irc.nick.success", "Changing IRC nick to %s");
		commands.registerCommand(new CommandIRC("irc", this));
		
		bot = new PircBotX();
		bot.getListenerManager().addListener(this);
		bot.setLogin("ServerMod");
		Loader loader = Loader.instance();
		bot.setVersion("ServerMod v"+sm.VERSION+" [FML "+loader.getFMLVersionString()+" "+loader.getMCVersionString()+"]");
		String flags = "" +
				(sm.server.isDedicatedServer() ? "D" : "I") +
				(sm.hasForge ? "F" : "");
		bot.setFinger("Flags: "+flags);
		bot.setName(sm.settings.irc_nick);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				connect();
			}
		}, "ServerMod: IRC connecting").start();
		
		sm.server.logger.log(Level.INFO, "IRC: Initialized");
	}
	
	public boolean connect() {
		if (bot.isConnected()) return false;
		
		try {
			bot.connect(sm.settings.irc_server);
		} catch (NickAlreadyInUseException e) {
			String name = bot.getName();
			server.logger.log(Level.WARNING, "IRC: Nick "+name+" already in use, trying another");
			bot.setName(name+"_");
			connect();
		} catch (Throwable e) {
			server.logger.log(Level.WARNING, "IRC: Could not connect: "+e);
			return false;
		}
		
		sm.server.logger.log(Level.INFO, "IRC: Connected");
		if (!sm.settings.irc_auth_nick.isEmpty()) {
			bot.sendMessage(sm.settings.irc_auth_nick, sm.settings.irc_auth_message);
			try { Thread.sleep(2000); } catch (Throwable e) {}
		}
		bot.joinChannel(sm.settings.irc_channel, sm.settings.irc_channel_key);
		return true;
	}
	
	

	@Override
	public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
		if (sm.settings.enable_chat_relaying && (!message.message.startsWith("/") || message.message.startsWith("/me ")) && bot.isConnected()) {
			if (message.message.startsWith("/me")) {
				messageQueue.add("* "+handler.getPlayer().username+" "+message.message.substring(4).replaceAll("\r|\n", ""));
			} else {
				messageQueue.add("<"+handler.getPlayer().username+"> "+message.message.replaceAll("\r|\n", ""));
			}
		}
		
		return message;
	}

	@Override
	public Packet3Chat clientChat(NetHandler handler, Packet3Chat message) {
		return message;
	}
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		if (sm.settings.enable_chat_relaying && bot.isConnected()) {
			bot.sendMessage(sm.settings.irc_channel, player.username+" joined the game.");
		}
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		if (sm.settings.enable_chat_relaying && bot.isConnected() && !serverCrashed) {
			bot.sendMessage(sm.settings.irc_channel, player.username+" left the game.");
		}
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		
	}
	
	@Override
	public void onPrivateMessage(PrivateMessageEvent event) {
		sm.server.logger.log(Level.INFO, "[IRC "+event.getUser().getNick()+"] "+event.getMessage());
	}
	
	@Override
	public void onMessage(MessageEvent event) {
		if (sm.settings.irc_command_threaddump && event.getMessage().equalsIgnoreCase("!threaddump")) {
			String s = "Current active threads: "+Thread.activeCount();
			
			Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
			for (Thread thread : stacks.keySet()) {
				s += "\n\n"+thread.toString();
				
				for (StackTraceElement element : stacks.get(thread)) {
					s += "\n\t"+element.toString();
				}
			}
			
			bot.sendMessage(sm.settings.irc_channel, "Thread dump: "+CrashReporter.paste("ServerMod Thread Dump", s));
		}
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (bot.isConnected()) {
			while (!messageQueue.isEmpty()) {
				bot.sendMessage(sm.settings.irc_channel, messageQueue.poll());
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return "ServerMod IRC";
	}
	
	@ForgeSubscribe
	public void onLivingDeath(LivingDeathEvent event) {
		if (sm.settings.enable_chat_relaying && event.entityLiving instanceof EntityPlayerMP) {
			bot.sendMessage(sm.settings.irc_channel, event.source.getDeathMessage((EntityPlayer)event.entityLiving));
		}
	}
}
