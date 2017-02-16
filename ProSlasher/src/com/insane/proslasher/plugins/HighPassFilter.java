package com.insane.proslasher.plugins;

import com.insane.proslasher.config.BasicPluginSetting;
import com.insane.proslasher.config.IPluginSetting;
import java.util.HashMap;

public class HighPassFilter extends PluginBase implements IPlugin {
	public HighPassFilter(){
		super();
	}

	public HighPassFilter(HashMap<String, Integer> parameters){
		super(parameters);
	}

	@Override
	public HashMap<String, IPluginSetting> getSettings(){
		HashMap<String, IPluginSetting> settings = new HashMap<>();
		settings.put("cutoff", new BasicPluginSetting("cutoff", 0, 44100, 22050));
		return settings;
	}

	/*
	 */
	@Override
	public void generateOutput(){
		int iCutoff = (int)parameters.get("cutoff");
		float cutoff = ((float)iCutoff) / 44100.0f;
		float b0 = buffer[0];
		for(int i = 0; i < (int)parameters.get("length"); i++){
			int input = buffer[i];
			float z = input - (b0 + cutoff * (input - b0));
			buffer[i] = (byte)z;
		}
	}

}