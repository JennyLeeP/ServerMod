package servermod.rcon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;

import servermod.core.Http;

import cpw.mods.fml.common.Loader;

public class RconClientThread extends Thread {
	private Socket socket;
	
	public RconClientThread(Socket socket) {
		super("ServerMod RCON Client Thread: "+socket.getInetAddress().getHostAddress()+":"+socket.getPort());
		
		this.socket = socket;
	}
	
	@Override
	public void run() {
		String username = null;
		
		try {
			BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter write = new PrintWriter(socket.getOutputStream(), true);
			
			write.println("Welcome to ServerMod RCON ("+Loader.instance().getMCVersionString()+" - FML "+Loader.instance().getFMLVersionString()+")");
			write.println();
			write.print("Username: ");
			write.flush();
			String user = read.readLine();
			write.print("Password: ");
			write.flush();
			String pass = read.readLine();
			
			// hack for telnet negotiation data
			int i = 0;
			while (i < user.length() && (Integer.valueOf(user.charAt(i)) > 127 || Integer.valueOf(user.charAt(i)) < 48)) {
				i++;
			}
			user = user.substring(i);
			
			write.println();
			
			String result;
			try {
				result = Http.post(new URL("http://login.minecraft.net/?user="+URLEncoder.encode(user, "UTF-8")+"&password="+URLEncoder.encode(pass, "UTF-8")+"&version=13"), null).text;
			} catch (Throwable e) {
				result = e.getMessage().replace(':', ' ');
			}
			if (!result.contains(":")) {
				Rcon.instance.log.log(Level.WARNING, "RCON: "+getIpPort()+" failed to login as "+user+": "+result.replace("\n", ""));
				write.println("Login failed");
				write.flush();
				socket.close();
				throw new IOException();
			}
			
			username = result.split(":")[2].trim();
			if (!Rcon.server.getConfigurationManager().areCommandsAllowed(username)) {
				Rcon.instance.log.log(Level.WARNING, "RCON: "+getIpPort()+" failed to login as "+username+": Not an op");
				write.println("Login failed");
				write.flush();
				socket.close();
				throw new IOException();
			}
			
			Rcon.instance.log.log(Level.INFO, "RCON: "+username+" ["+getIpPort()+"] logged in");
			write.println("Login successful");
			write.print("> ");
			write.flush();
			
			String line;
			while ((line = read.readLine()) != null) {
				if (line.equalsIgnoreCase("exit")) {
					write.println("Goodbye");
					write.flush();
					socket.close();
					throw new IOException();
				}
				
				Rcon.server.getCommandManager().executeCommand(new RconCommandSender(username, write), line);
				write.print("> ");
				write.flush();
			}
			
			throw new IOException();
		} catch (Throwable e) {
			Rcon.instance.log.log(Level.INFO, "RCON: "+(username == null ? "(not logged in)" : username)+" ["+getIpPort()+"] disconnected");
		}
	}
	
	private String getIpPort() {
		return socket.getInetAddress().getHostAddress()+":"+socket.getPort();
	}
}
