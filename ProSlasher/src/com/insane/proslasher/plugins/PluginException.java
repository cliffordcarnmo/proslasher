package com.insane.proslasher.plugins;

public class PluginException extends Exception {

	public PluginException() {
		super();
	}

	public PluginException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public PluginException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public PluginException(String arg0) {
		super(arg0);
	}

	public PluginException(Throwable arg0) {
		super(arg0);
	}

}
