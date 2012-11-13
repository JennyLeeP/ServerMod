package servermod.provider;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.src.HttpUtil;

import servermod.api.provider.PastebinProvider;
import servermod.api.provider.PastebinProvider.PasteException;
import servermod.core.Http;

public class PastebinStikked implements PastebinProvider {
	private final String apiRoot;
	
	public PastebinStikked(String apiRoot) {
		this.apiRoot = apiRoot;
	}
	
	@Override
	public String paste(String title, String text) throws PasteException {
		Map<String, String> postvars = new HashMap<String, String>();
		postvars.put("text", text);
		postvars.put("title", title);
		postvars.put("name", "ServerMod");
		postvars.put("private", "1");
		
		try {
			return Http.post(new URL(apiRoot+"/create"), postvars).text;
		} catch (Throwable e) {
			throw new PasteException(e);
		}
	}
}
