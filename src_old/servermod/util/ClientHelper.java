package servermod.util;

import net.minecraft.src.Enchantment;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientHelper {
	public static String getEnchantmentName(Enchantment enchantment, int potency) {
		return enchantment.getTranslatedName(potency);
	}
}
