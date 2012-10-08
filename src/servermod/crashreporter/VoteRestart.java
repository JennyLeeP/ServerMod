package servermod.crashreporter;

import java.util.ArrayList;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class VoteRestart extends ListenerAdapter {
	private final CrashReporter cr;
	protected boolean voting = false;
	protected List<User> votes = new ArrayList<User>();
	
	public VoteRestart(CrashReporter cr) {
		this.cr = cr;
		
		cr.sm.irc.bot.getListenerManager().addListener(this);
	}
	
	@Override
	public void onMessage(MessageEvent event) {
		if (voting && event.getMessage().equalsIgnoreCase("!vote")) {
			PircBotX bot = event.getBot();
			Channel chan = event.getChannel();
			User user = event.getUser();
			
			if (votes.contains(user)) {
				event.getBot().sendMessage(chan, Colors.BOLD+user+Colors.BOLD+" has already voted!");
				return;
			}
			
			votes.add(user);
			bot.sendMessage(chan, "Adding "+Colors.BOLD+user.getNick()+Colors.BOLD+"'s vote");
			
			if (votes.size() >= cr.sm.settings.crash_reporter_vote_restart_votes) {
				bot.sendMessage(chan, "Votes acquired, restarting the server...");
				
				voting = false;
				votes.clear();
				bot.sendMessage(chan, "== TODO =="); // TODO restarthelper coremod - discussed with cpw
			}
		}
	}
}
