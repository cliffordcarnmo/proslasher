package com.insane.proslasher.plugins;

import java.util.HashMap;
import com.insane.proslasher.config.IPluginSetting;

public class ExamplePlugin extends PluginBase implements IPlugin{
	public ExamplePlugin(HashMap<String,Integer> parameters){
		super(parameters);
	}

	@Override
	public HashMap<String, IPluginSetting> getSettings()
	{
		 HashMap<String, IPluginSetting> settings = new HashMap<>();
		 return settings;
	}
	
	/*
	This is the method where you add code for your sound generator.
	Basically, you fill the byte-array "buffer" with signed values of your choice.
	The length of "buffer" is provided by parameters.get("sampleLength").
	
	You get these parameters from the user interface sliders:
	parameters.get("sampleRate")
	parameters.get("sampleSize")
	parameters.get("value1")
	parameters.get("value2")
	parameters.get("value3")
	parameters.get("value4")
	parameters.get("value5")
	*/

	@Override
	public void generateOutput(){
		double b1, b2, b3, b4;
		for (int i = 0; i < (int)parameters.get("sampleLength"); i++) {
			b1=Math.sin(i)*(int)parameters.get("value1");
			b3=Math.sin(i*0.5)*(int)parameters.get("value2");
			b4=Math.sin(i*4)*(int)parameters.get("value3");
			b2=(3.0-Math.sin(i*0.025))/4.0;
			buffer[i]=(byte)((b1+b3+b4)*b2);
		}
	}
}