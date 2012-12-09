package servermod.crashreporter;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ServerLogHandler extends Handler {
	@Override
	public void close() throws SecurityException {}

	@Override
	public void flush() {}

	@Override
	public void publish(LogRecord arg0) {
		if (arg0.getMessage().startsWith("This crash report has been saved to: ")) {
			CrashReporter.instance.handleServerCrash(new File(arg0.getMessage().substring(37)));
		} else if (arg0.getMessage().startsWith("Could not save crash report to ")) {
			CrashReporter.instance.handleServerCrash(arg0.getThrown());
		} else if (arg0.getMessage().startsWith("Failed to handle packet for ")) {
			CrashReporter.instance.handlePlayerCrash(arg0.getMessage().split(" ")[5].split("/")[0], arg0.getThrown());
		}
	}
}
