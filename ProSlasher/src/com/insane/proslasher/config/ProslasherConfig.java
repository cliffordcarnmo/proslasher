/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.insane.proslasher.config;
import com.insane.proslasher.plugins.*;
import java.util.*;

/**
 *
 * @author mrorigo
 */
public class ProslasherConfig{

	public int sampleLength;
	public int playbackFrequency;
	public boolean loop;
	public int loopStart;
	public int loopEnd;
	
	public List<IPlugin> Plugins;
	public HashMap<IPlugin, HashMap<String, IPluginSetting>> PluginSettings;

	public ProslasherConfig()
	{
		Plugins = new LinkedList<>(); 
		PluginSettings = new HashMap<>(); 
	}
	
	public boolean addPlugin(IPlugin plugin, HashMap<String, IPluginSetting> settings)
	{
		if(PluginSettings.containsKey(plugin))
			return false;
		PluginSettings.put(plugin, settings);
		Plugins.add(plugin);
		return true;
	}

	public boolean removePlugin(IPlugin plugin)
	{
		if(!PluginSettings.containsKey(plugin))
			return false;
		PluginSettings.remove(plugin);
		Plugins.remove(plugin);
		return true;
	}

	public boolean swapPluginOrder(IPlugin plugin1, IPlugin plugin2)
	{
		if(!PluginSettings.containsKey(plugin1))
			return false;
		if(!PluginSettings.containsKey(plugin2))
			return false;
		int i1 = Plugins.indexOf(plugin1);
		int i2 = Plugins.indexOf(plugin2);
		Plugins.set(i2, plugin1);
		Plugins.set(i1, plugin2);
		return true;
	}

}
