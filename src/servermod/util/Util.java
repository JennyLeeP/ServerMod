package servermod.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import net.minecraft.src.Enchantment;
import net.minecraft.src.EnchantmentHelper;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;

public class Util {
	private static Class clientHelper = null;
	private static Method getEnchantmentName = null;
	
	private static final String[] enchantments = new String[Enchantment.enchantmentsList.length];
	private static final String[] trueValues = {"true","1","y","yes","enable","enabled"};
	private static final String[] falseValues = {"false","0","n","no","disable","disabled"};
	
	/**
	 * Stolen from a javascript calculator
	 */
	public static String getRomanNumeral(int num) {
		String op = "";
		
		while (num - 1000000 >= 0) {
			op += "m";
			num -= 1000000;
		}
		while (num - 900000 >= 0) {
		    op += "cm";
		    num -= 900000;
		}
		while (num - 500000 >= 0) {
		    op += "d";
		    num -= 500000;
		}
		while (num - 400000 >= 0) {
		    op += "cd";
		    num -= 400000;
		}
		while (num - 100000 >= 0) {
		    op += "c";
		    num -= 100000;
		}
		while (num - 90000 >= 0) {
		    op += "xc";
		    num -= 90000;
		}
		while (num - 50000 >= 0) {
		    op += "l";
		    num -= 50000;
		}
		while (num - 40000 >= 0) {
		    op += "xl";
		    num -= 40000;
		}
		while (num - 10000 >= 0) {
		    op += "x";
		    num -= 10000;
		}
		while (num - 9000 >= 0) {
		    op += "Mx";
		    num -= 9000;
		}
		while (num - 5000 >= 0) {
		    op += "v";
		    num -= 5000;
		}
		while (num - 4000 >= 0) {
		    op += "Mv";
		    num -= 4000;
		}
		while (num - 1000 >= 0) {
		    op += "M";
		    num -= 1000;
		}
		while (num - 900 >= 0) {
		    op += "CM";
		    num -= 900;
		}
		while (num - 500 >= 0) {
		    op += "D";
		    num -= 500;
		}
		while (num - 400 >= 0) {
		    op += "CD";
		    num -= 400;
		}
		while (num - 100 >= 0) {
		    op += "C";
		    num -= 100;
		}
		while (num - 90 >= 0) {
		    op += "XC";
		    num -= 90;
		}
		while (num - 50 >= 0) {
		    op += "L";
		    num -= 50;
		}
		while (num - 40 >= 0) {
		    op += "XL";
		    num -= 40;
		}
		while (num - 10 >= 0) {
		    op += "X";
		    num -= 10;
		}
		while (num - 9 >= 0) {
		    op += "IX";
		    num -= 9;
		}
		while (num - 5 >= 0) {
		    op += "V";
		    num -= 5;
		}
		while (num - 4 >= 0) {
		    op += "IV";
		    num -= 4;
		}
		while (num - 1 >= 0) {
		    op += "I";
		    num -= 1;
		}
		
		return op;
	}
	
	public static String getEnchantmentName(int id, int potency) {
		if (clientHelper != null) {
			try {
				return (String)getEnchantmentName.invoke(null, Enchantment.enchantmentsList[id], potency);
			} catch (Throwable e) {}
		}
		
		return (enchantments[id] == null ? id : enchantments[id])+" "+getRomanNumeral(potency);
	}
	
	public static String getWorldName(World world) {
		return world.provider.getDimensionName();
	}
	
	public static String getDuration(int tick) {
		int ticks = tick / 20;
        int seconds = ticks / 60;
        ticks %= 60;
        return ticks < 10 ? seconds + ":0" + ticks : seconds + ":" + ticks;
	}
	
	public static String getPotionEffectString(ICommandSender sender, PotionEffect effect) {
		String ret = sender.translateString(effect.getEffectName());
		
		int potency = effect.getAmplifier();
		if (potency > 0) ret += " "+getRomanNumeral(potency-1);
		
		return ret;
	}
	
	public static String readFileToString(File f) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String s = "";
		char[] buffer = new char[1024];
		int read = 0;
		while ((read = reader.read(buffer)) != -1) {
			s += String.valueOf(buffer, 0, read);
			buffer = new char[1024];
		}
			
		return s;
	}
	
	public static String getDifficulty(int difficulty) {
		switch (difficulty) {
			case 0: return "Peaceful";
			case 1: return "Easy";
			case 2: return "Normal";
			case 3: return "Hard";
			default: return ""+difficulty;
		}
	}
	
	public static boolean parseBoolean(String bool) throws IllegalArgumentException {
		for (String value : trueValues) {
			if (value.equalsIgnoreCase("bool")) return true;
		}
		for (String value : falseValues) {
			if (value.equalsIgnoreCase("bool")) return false;
		}
		throw new IllegalArgumentException("Bad boolean string: "+bool);
	}
	
	public static Field getDeclaredFieldTraverse(Class clazz, String field) throws NoSuchFieldException, SecurityException {
		Class sc = clazz;
		while (sc != null) {
			try {
				return sc.getDeclaredField(field);
			} catch (NoSuchFieldException e) {}
			
			sc = sc.getSuperclass();
		}
		
		throw new NoSuchFieldException(field);
	}
	
	public static String colorCode(String s) {
		String ret = "";
		
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '&') {
				if (i+1 < s.length() && s.charAt(i+1) == '&') ret += "&";
				else ret += "\u00A7";
			} else ret += s.charAt(i);
		}
		
		if (ret.endsWith("\u00A7")) ret = ret.substring(0, ret.length()-1); // avoid client DoS with messages ending with the control character
		return ret;
	}
	
	static {
		try {
			clientHelper = Class.forName("servermod.util.ClientHelper");
			getEnchantmentName = clientHelper.getMethod("getEnchantmentName", Enchantment.class, int.class);
		} catch (Throwable e) {}
		
		enchantments[0] = "Protection";
		enchantments[1] = "Fire Protectiion";
		enchantments[2] = "Feather Falling";
		enchantments[3] = "Blast Protection";
		enchantments[4] = "Projectile Protection";
		enchantments[5] = "Respiration";
		enchantments[6] = "Aqua Affinity";
		enchantments[16] = "Sharpness";
		enchantments[17] = "Smite";
		enchantments[18] = "Bane of Arthropods";
		enchantments[19] = "Knockback";
		enchantments[20] = "Fire Aspect";
		enchantments[21] = "Looting";
		enchantments[32] = "Efficiency";
		enchantments[33] = "Silk Touch";
		enchantments[34] = "Unbreaking";
		enchantments[35] = "Fortune";
		enchantments[48] = "Power";
		enchantments[49] = "Punch";
		enchantments[50] = "Flame";
		enchantments[51] = "Infinity";
	}
}
