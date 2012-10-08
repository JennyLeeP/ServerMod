package servermod.worldedit;

import java.util.HashMap;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ServerCommandManager;
import net.minecraft.src.StatCollector;
import cpw.mods.fml.common.registry.LanguageRegistry;
import servermod.core.ServerMod;

public class WorldEdit {
	protected final ServerMod sm;
	protected CommandWorldEdit command;
	protected static final ItemStack tool = new ItemStack(Block.pistonExtension); // block 36
	private HashMap<String, PlayerData> data = new HashMap<String, PlayerData>();
	
	public WorldEdit(ServerMod sm) {
		this.sm = sm;
		
		ServerCommandManager commands = (ServerCommandManager)sm.server.getCommandManager();
		LanguageRegistry lang = LanguageRegistry.instance();
		
		lang.addStringLocalization("commands.servermod_worldedit.usage", "/worldedit {command} {parameters...}");
		lang.addStringLocalization("commands.servermod_worldedit.noSelection", "No selection");
		lang.addStringLocalization("commands.servermod_worldedit.noClipboard", "Empty clipboard");
		lang.addStringLocalization("commands.servermod_worldedit.badBlock", "Bad block type %d:%d");
		lang.addStringLocalization("commands.servermod_worldedit.4kWarning", "Warning: The clipboard contains %d blocks with ID above 255. Those are not supported by the schematic format and have been replaced by air.");
		lang.addStringLocalization("commands.servermod_worldedit.tool.usage", "/worldedit tool");
		lang.addStringLocalization("commands.servermod_worldedit.tool.success", "Giving the world editing tool (%s)");
		lang.addStringLocalization("commands.servermod_worldedit.sel.usage", "/worldedit sel{1|2} [<x> <y> <z>]");
		lang.addStringLocalization("commands.servermod_worldedit.sel.success", "Set selection point %d to (%d,%d,%d)");
		lang.addStringLocalization("commands.servermod_worldedit.clearsel.usage", "/worldedit clearsel [{1|2}]");
		lang.addStringLocalization("commands.servermod_worldedit.clearsel.success", "Cleared selection point %d");
		lang.addStringLocalization("commands.servermod_worldedit.clearsel.all", "Cleared selection");
		lang.addStringLocalization("commands.servermod_worldedit.set.usage", "/worldedit set <block>[:<meta>]");
		lang.addStringLocalization("commands.servermod_worldedit.load.usage", "/worldedit load <filename>");
		lang.addStringLocalization("commands.servermod_worldedit.load.fileNotFound", "File not found");
		lang.addStringLocalization("commands.servermod_worldedit.load.readFail", "Failed to read the schematic file");
		lang.addStringLocalization("commands.servermod_worldedit.load.usage", "/worldedit save <filename>");
		lang.addStringLocalization("commands.servermod_worldedit.load.noWrite", "Cannot write to file");
		lang.addStringLocalization("commands.servermod_worldedit.load.writeFail", "Failed to save the schematic file");
		command = new CommandWorldEdit("worldedit", this);
		commands.registerCommand(command);
		
		int id = tool.itemID;
		int sprite = tool.getIconIndex();
		int maxStack = tool.getMaxStackSize();
		Item.itemsList[id] = null;
		Item.itemsList[id] = new ItemWorldEditTool(id - 256, this);
		Item.itemsList[id].setIconIndex(sprite);
		Item.itemsList[id].setMaxStackSize(maxStack);
	}
	
	protected PlayerData getPlayerData(String username) {
		String nusername = username.toLowerCase();
		if (!data.containsKey(nusername)) data.put(nusername, new PlayerData(nusername));
		return data.get(nusername);
	}
}
