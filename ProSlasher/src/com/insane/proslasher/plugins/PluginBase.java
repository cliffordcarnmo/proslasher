package com.insane.proslasher.plugins;

import com.insane.proslasher.config.IPluginSetting;
import com.insane.proslasher.plugins.IPlugin;
import java.util.*;

public abstract class PluginBase implements IPlugin {
	public byte[] buffer;
	public final HashMap<String,Integer> parameters;
	private final String name;
	private IPlugin previous;
	
	public PluginBase()
	{
		name = this.getClass().getSimpleName();
		parameters = new HashMap<String,Integer>();
	}
	
	public PluginBase(HashMap<String,Integer> parameters){
		this();
		this.parameters.putAll(parameters);
		previous = null;
	}

	public PluginBase(HashMap<String,Integer> parameters, IPlugin previous){
		this(parameters);
		this.previous = previous;
	}

	public void setParameters(HashMap<String,Integer> newParams)
	{
		this.parameters.putAll(newParams);
	}

	public void setSettings(Collection<IPluginSetting> settings)
	{
		Iterator<IPluginSetting> s = settings.iterator();
		while(s.hasNext()) {
			IPluginSetting se = s.next();
			this.parameters.put(se.getName(), se.getValue());
		}
	}

	public void setPrevious(IPlugin p)
	{
		previous = p;
	}
	
	public boolean hasPrevious()
	{
		return previous != null;
	}
	
	public byte[] getOutput(){
		if(previous != null)
			buffer = ((PluginBase)previous).getOutput();
		else
			buffer = new byte[(int)parameters.get("sampleLength")];
		generateOutput();
		
		return buffer;
	}

	public int getOutputLength(){
		return buffer.length;
	}
	
	public abstract void generateOutput();

	public String getName(){
		return name;
	}
}
