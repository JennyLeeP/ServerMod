package servermod.rcon;

import java.net.ServerSocket;
import java.util.logging.Level;

public class RconListenThread extends Thread {
	private ServerSocket socket;
	
	public RconListenThread(ServerSocket socket) {
		super("ServerMod RCON Listen Thread");
		
		this.socket = socket;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				RconClientThread thread = new RconClientThread(socket.accept());
				thread.start();
			} catch (Throwable e) {
				Rcon.instance.log.log(Level.WARNING, "RCON: Accept failed", e);
			}
		}
	}
}
