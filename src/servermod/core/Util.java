package servermod.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Util {
	public static String readFileToString(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String data = "";
		
		char[] buffer = new char[1024];
		int read = 0;
		while ((read = reader.read(buffer)) != -1) {
			data += String.valueOf(buffer, 0, read);
			buffer = new char[1024];
		}
			
		return data;
	}
}
