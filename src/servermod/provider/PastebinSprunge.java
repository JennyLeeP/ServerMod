package servermod.provider;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import servermod.api.provider.PastebinProvider;
import servermod.api.provider.PastebinProvider.PasteException;
import servermod.core.Http;

public class PastebinSprunge implements PastebinProvider {
	@Override
	public String paste(String title, String text) throws PasteException {
		Map<String, String> postvars = new HashMap<String, String>();
		postvars.put("sprunge", text);
		
		try {
			return Http.post(new URL("http://sprunge.us"), postvars).text.trim().replaceAll("\n", "");
		} catch (Throwable e) {
			throw new PasteException(e);
		}
	}
}
