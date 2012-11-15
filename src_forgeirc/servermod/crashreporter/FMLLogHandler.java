package servermod.crashreporter;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class FMLLogHandler extends Handler {
	@Override
	public void close() throws SecurityException {}

	@Override
	public void flush() {}

	@Override
	public void publish(LogRecord arg0) {
		if (arg0.getMessage().startsWith("A critical server error occured handling a packet, kicking ")) {
			CrashReporter.instance.handlePlayerCrash(arg0.getMessage().split(" ")[9], arg0.getThrown());
		}
	}
}
