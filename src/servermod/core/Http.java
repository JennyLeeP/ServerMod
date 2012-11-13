package servermod.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import cpw.mods.fml.common.Loader;

import net.minecraft.src.HttpUtil;

public class Http {
	public static Response post(URL url, Map<String, String> postData) throws IOException {
		try {
			String data = HttpUtil.buildPostString(postData);
		
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", ""+data.getBytes().length);
			connection.setRequestProperty("Content-Language", "en-US");
			Loader loader = Loader.instance();
			connection.setRequestProperty("User-Agent", "ServerMod/"+ServerMod.VERSION+" "+loader.getMCVersionString().replace(' ', '/')+" "+loader.getFMLVersionString().replace(' ', '/'));
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(data);
			out.flush();
			out.close();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String text = "";
			String line;
			while ((line = in.readLine()) != null) {
				text += line+"\n";
			}
			
			Map<String, String> headers = new HashMap<String, String>();
			for (String header : connection.getHeaderFields().keySet()) headers.put(header, connection.getHeaderField(header));
			
			return new Response(text, headers);
		} catch (Throwable e) {
			ServerMod.instance.log.log(Level.WARNING, "Could not POST to "+url, e);
			throw new IOException(e);
		}
	}
	
	public static class Response {
		public final String text;
		public final Map<String, String> headers;
		
		private Response(String text, Map<String, String> headers) {
			this.text = text;
			this.headers = headers;
		}
	}
}
