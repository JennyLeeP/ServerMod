package servermod.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import net.minecraft.server.MinecraftServer;

public class Settings extends Properties {
	private ServerMod sm;
	private File file;
	private Class<? extends Settings> classCache = getClass();
	private Map<String,Field> fieldCache = new HashMap<String,Field>();
	
	public boolean require_op_kill_self = false;
	public boolean require_op_tps = false;
	
	public boolean enable_irc = false;
	public String irc_server = "";
	public String irc_nick = "";
	public String irc_channel = "";
	public String irc_channel_key = "";
	public String irc_auth_nick = "NickServ";
	public String irc_auth_message = "";
	public boolean irc_command_threaddump = false;
	
	public boolean enable_chat_relaying = false;
	public boolean chat_relaying_bidirectional = false;
	public boolean chat_relaying_commands = false;
	
	public boolean enable_crash_reporter = false;
	public boolean crash_reporter_disconnect = true;
	public boolean crash_reporter_vote_restart = false;
	public int crash_reporter_vote_restart_votes = 3;
	
	public boolean enable_home = false;
	public boolean home_require_op = false;
	public boolean home_use_bed = true;
	
	public boolean enable_inventory = true;
	public boolean inventory_crafting_enable = true;
	public boolean inventory_crafting_require_op = true;
	public boolean inventory_crafting_use_inventory = true;
	public boolean inventory_crafting_workbench = false;
	public boolean inventory_enderchest_enable = true;
	public boolean inventory_enderchest_require_op = true;
	public boolean inventory_user_enable = true;
	public boolean inventory_helmet_enable = true;
	public boolean inventory_helmet_require_op = false;
	
	public boolean enable_motd = true;
	
	public boolean enable_worldedit = true;
	
	public boolean worldedit_copy_entities = false;
	
	public boolean tweaks_throwable_firecharges = false;
	public boolean tweaks_creeper_nogrief = false;
	public float tweaks_creeper_damagefactor = 1.0F;
	public boolean tweaks_player_drophead = false;
	public boolean tweaks_head_keepplayer = true;
	
	public Settings(ServerMod sm, String file) {
		this.sm = sm;
		this.file = new File(file);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized Enumeration keys() {
		Enumeration keysEnum = super.keys();
		Vector keyList = new Vector();
		while (keysEnum.hasMoreElements()){
			keyList.add(keysEnum.nextElement());
		}
		Collections.sort(keyList);
		return keyList.elements();
	}
	
	public void load() {
		try {
			load(new FileReader(file));
			
			Enumeration<String> keys = keys();
			while (keys.hasMoreElements()) {
				String name = keys.nextElement();
				String fieldName = name.replaceAll("-","_");
				Field f;
				if (fieldCache.containsKey(fieldName)) {
					f = fieldCache.get(fieldName);
				} else {
					try { f = classCache.getDeclaredField(fieldName); } catch (Throwable e) { continue; }
					fieldCache.put(fieldName, f);
				}
				
				Class<?> type = f.getType();
				if (String.class.isAssignableFrom(type)) {
					f.set(this, getProperty(name));
				} else if (Boolean.TYPE.isAssignableFrom(type)) {
					try { f.set(this, Boolean.parseBoolean(getProperty(name))); } catch (Throwable e) {}
				} else if (Integer.TYPE.isAssignableFrom(type)) {
					try { f.set(this, Integer.parseInt(getProperty(name))); } catch (Throwable e) {}
				} else if (Float.TYPE.isAssignableFrom(type)) {
					try { f.set(this, Float.parseFloat(getProperty(name))); } catch (Throwable e) {}
				} else if (Double.TYPE.isAssignableFrom(type)) {
					try { f.set(this, Double.parseDouble(getProperty(name))); } catch (Throwable e) {}
				}
			}
		} catch (Throwable e) {
			MinecraftServer.getServer().logger.log(Level.WARNING, "Unable to load ServerMod configuration: "+e);
		}
	}
	
	public void save() {
		try {
			for (Field f : getClass().getDeclaredFields()) {
				String name = f.getName();
				
				if (name.contains("_")) setProperty(name.replaceAll("_", "-"), ""+f.get(this));
			}
			
			store(new FileWriter(file), "ServerMod configuration");
		} catch (Throwable e) {
			MinecraftServer.getServer().logger.log(Level.WARNING, "Unable to save ServerMod configuration: "+e);
		}
	}
}
