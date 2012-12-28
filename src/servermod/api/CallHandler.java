package servermod.api;

import servermod.api.provider.PastebinProvider;

public abstract class CallHandler {
	public static CallHandler instance;
	
	public abstract PastebinProvider getPastebin();
}
