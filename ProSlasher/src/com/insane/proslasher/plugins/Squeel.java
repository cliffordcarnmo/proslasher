package com.insane.proslasher.plugins;

import com.insane.proslasher.config.BasicPluginSetting;
import java.util.HashMap;
import com.insane.proslasher.config.IPluginSetting;

public class Squeel extends PluginBase implements IPlugin{
	public Squeel(){
		super();
	}

	public Squeel(HashMap<String, Integer> parameters){
		super(parameters);
	}

	@Override
	public HashMap<String, IPluginSetting> getSettings()
	{
		 HashMap<String, IPluginSetting> settings = new HashMap<>();
		 
		 settings.put("amplitude", new BasicPluginSetting("amplitude",0,127, 127));
		 settings.put("freqeuency", new BasicPluginSetting("frequency",0,44100, 22050));
		 return settings;
	}

	@Override
	public void generateOutput(){
		int sampleRate = (int)parameters.get("sampleRate");
		int frequency = (int)(parameters.get("frequency") == null ? sampleRate : parameters.get("frequency"));
		int len = (int)parameters.get("sampleLength");
		float ampl = 127;//(float)(parameters.get("amplitude") == null ? 127 : parameters.get("amplitude"));
		float stp = (((float)Math.PI/2) * frequency/1000) / (float)len;

		for(int i = 0; i < len; i++){
			float sv = (float)Math.sin(stp*(float)i)*ampl;
			if(sv > 127)
				sv = 127;
			else if(sv < -127)
				sv = -127;
			buffer[i] = (byte)Math.floor(sv*ampl);
		}
	}
	
}