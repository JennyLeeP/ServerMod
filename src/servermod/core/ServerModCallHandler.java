package servermod.core;

import servermod.api.CallHandler;
import servermod.api.provider.PastebinProvider;

public class ServerModCallHandler extends CallHandler {
	@Override
	public PastebinProvider getPastebin() {
		return ServerMod.instance.pastebin;
	}
}
