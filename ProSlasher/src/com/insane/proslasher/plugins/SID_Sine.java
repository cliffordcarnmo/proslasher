package com.insane.proslasher.plugins;

import java.util.HashMap;
import com.insane.proslasher.config.*;

public class SID_Sine extends PluginBase {
	public SID_Sine() {
		super(new HashMap<String, Integer>());
	}

	public SID_Sine(HashMap<String, Integer> parameters){
		super(parameters);
	}

	@Override
	public HashMap<String, IPluginSetting> getSettings()
	{
		 HashMap<String, IPluginSetting> settings = new HashMap<>();
		 
		 settings.put("amplitude", new BasicPluginSetting("amplitude",0,127, 127));

		 settings.put("frequency", new BasicPluginSetting("frequency", 0, 44100, 22050));
		 return settings;
	}

	@Override
	public void generateOutput(){
		int frequency = (int)parameters.get("frequency");
		int len = (int)parameters.get("sampleLength");
		float ampl = (float)(parameters.get("amplitude") == null ? 127 : parameters.get("amplitude"));
		float stp = (((float)Math.PI/2) * ((float)frequency/(float)len/256f));

		for(int i = 0; i < len; i++){
			byte x = hasPrevious() ? buffer[i] : 127;
			float sv = (float)Math.sin(stp*(float)i)*ampl;
			if(sv > 127)
				sv = 127;
			else if(sv < -127)
				sv = -127;
			buffer[i] = (byte)(((float)Math.floor(sv)*x)/128.0f);
		}
	}
	
}