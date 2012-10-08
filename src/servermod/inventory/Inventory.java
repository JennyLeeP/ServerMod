package servermod.inventory;

import java.util.logging.Level;

import net.minecraft.src.ServerCommandManager;
import cpw.mods.fml.common.registry.LanguageRegistry;

import servermod.core.ServerMod;

public class Inventory {
	protected ServerMod sm;
	
	public Inventory(ServerMod sm) {
		this.sm = sm;
		
		ServerCommandManager commands = (ServerCommandManager)sm.server.getCommandManager();
		LanguageRegistry lang = LanguageRegistry.instance();
		
		if (sm.settings.inventory_enderchest_enable) {
			lang.addStringLocalization("commands.servermod_enderchest.usage", "/enderchest");
			commands.registerCommand(new CommandEnderChest("enderchest", this));
		}
		
		if (sm.settings.inventory_crafting_enable) {
			lang.addStringLocalization("commands.servermod_craft.usage", "/craft");
			commands.registerCommand(new CommandCraft("craft", this));
		}
		
		if (sm.settings.inventory_user_enable) {
			lang.addStringLocalization("commands.servermod_inventory.usage", "/inventory <player>");
			lang.addStringLocalization("commands.servermod_inventory.success", "Viewing inventory for %s");
			commands.registerCommand(new CommandInventory("inventory", this));
		}
		
		if (sm.settings.inventory_helmet_enable) {
			lang.addStringLocalization("commands.servermod_helmet.usage", "/inventory <player>");
			lang.addStringLocalization("commands.servermod_helmet.fail", "You're not holding a block");
			lang.addStringLocalization("commands.servermod_helmet.success", "Swapping helmet for current block");
			commands.registerCommand(new CommandHelmet("helmet", this));
		}
		
		sm.server.logger.log(Level.INFO, "Inventory: Initialized");
	}
}
