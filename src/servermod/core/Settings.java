package servermod.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Settings {
	private final Map<String, String> stringSettings = new HashMap<String, String>();
	private final Map<String, Boolean> booleanSettings = new HashMap<String, Boolean>();
	private final Map<String, Integer> intSettings = new HashMap<String, Integer>();
	private final Map<String, Float> floatSettings = new HashMap<String, Float>();
	private final Map<String, String> comments = new HashMap<String, String>();
	private final File file;
	private final String comment;
	
	public Settings(File file, String comment) {
		this.file = file;
		this.comment = comment;
	}
	
	public void addSetting(String key, String defaultValue, String comment) {
		stringSettings.put(key, defaultValue);
		comments.put(key, comment);
	}
	
	public void addSetting(String key, boolean defaultValue, String comment) {
		booleanSettings.put(key, defaultValue);
		comments.put(key, comment);
	}
	
	public void addSetting(String key, int defaultValue, String comment) {
		intSettings.put(key, defaultValue);
		comments.put(key, comment);
	}
	
	public void addSetting(String key, float defaultValue, String comment) {
		floatSettings.put(key, defaultValue);
		comments.put(key, comment);
	}
	
	public String getString(String key) {
		return stringSettings.get(key);
	}
	
	public boolean getBoolean(String key) {
		return booleanSettings.get(key);
	}
	
	public int getInt(String key) {
		return intSettings.get(key);
	}
	
	public float getFloat(String key) {
		return floatSettings.get(key);
	}
	
	public void load() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty() || line.charAt(0) == '#') continue;
			
			int separator = line.indexOf('=');
			if (separator == -1) continue; // not a valid setting
			String key = line.substring(0, separator - 1);
			if (key.isEmpty()) continue; // not valid either
			String value = line.substring(separator + 1);
			
			if (stringSettings.containsKey(key)) {
				stringSettings.put(key, value);
			} else if (booleanSettings.containsKey(key)) {
				booleanSettings.put(key, Boolean.parseBoolean(value));
			} else if (intSettings.containsKey(key)) {
				intSettings.put(key, Integer.parseInt(value));
			} else if (floatSettings.containsKey(key)) {
				floatSettings.put(key, Float.parseFloat(key));
			}
		}
		
		reader.close();
	}
	
	public void save() throws IOException {
		PrintWriter writer = new PrintWriter(file);
		
		if (comment != null) {
			for (String line : comment.split("\n")) {
				writer.println("# "+line);
			}
			
			writer.print("#########################");
		}
		
		for (String key : stringSettings.keySet()) {
			if (comments.containsKey(key)) writer.println("# "+comments.get(key));
			writer.println(key+"="+stringSettings.get(key));
		}
		for (String key : booleanSettings.keySet()) {
			if (comments.containsKey(key)) writer.println("# "+comments.get(key));
			writer.println(key+"="+booleanSettings.get(key));
		}
		for (String key : intSettings.keySet()) {
			if (comments.containsKey(key)) writer.println("# "+comments.get(key));
			writer.println(key+"="+intSettings.get(key));
		}
		for (String key : floatSettings.keySet()) {
			if (comments.containsKey(key)) writer.println("# "+comments.get(key));
			writer.println(key+"="+floatSettings.get(key));
		}
		
		writer.flush();
		writer.close();
	}
}
