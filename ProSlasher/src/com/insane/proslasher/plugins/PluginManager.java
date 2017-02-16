package com.insane.proslasher.plugins;

public class PluginManager {

	public static PluginManager instance = null;
	
	public static PluginManager getInstance() {
		if(instance == null) {
			instance = new PluginManager();
		}
		return instance;
	}
	
	private PluginManager() {		
		// needs to be private so everyone is forced to use getInstance
	}
	
	public IPlugin getPlugin(String className) throws PluginException {
		try {
		Class pc = Class.forName("com.insane.proslasher.plugins." + className);		
		if(pc != null) {
			IPlugin plugin = (IPlugin)pc.newInstance();
			if(plugin != null) {
				return plugin;
			} else {
				throw new PluginException("Unable to load plugin");
			}
		} else {
			throw new PluginException("Couldn't find plugin");
		}
		} catch(ClassNotFoundException e) {
			throw new PluginException(e);
		} catch(InstantiationException e) {
			throw new PluginException(e);
		} catch(IllegalAccessException e) {
			throw new PluginException(e);
		}
	}
}
