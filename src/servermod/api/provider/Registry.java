package servermod.api.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for API provider objects.
 * 
 * @author Richard
 */
public class Registry {
	private static final Map<String, PastebinProvider> pastebinProviders = new HashMap<String, PastebinProvider>();
	
	/**
	 * Register a {@link PastebinProvider}.
	 * 
	 * @param id ID name for the provider, used in the config file
	 * @param provider The provider
	 */
	public static void registerPastebinProvider(String id, PastebinProvider provider) {
		if (pastebinProviders.containsKey(id)) throw new IllegalArgumentException("Pastebin provider "+id+" already registered by "+pastebinProviders.get(id)+" when registering "+provider);
		
		pastebinProviders.put(id, provider);
	}
	
	/**
	 * Get a {@link PastebinProvider} by its ID.
	 * 
	 * @param id ID name for the provider
	 * @return The provider, or null if there is no such provider
	 */
	public static PastebinProvider getPastebinProvider(String id) {
		return pastebinProviders.get(id);
	}
}
