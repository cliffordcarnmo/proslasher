package com.insane.proslasher.plugins;

import java.util.HashMap;
import com.insane.proslasher.config.*;

public class ADSR extends PluginBase {
	public ADSR() {
		super();
	}

	public ADSR(HashMap<String, Integer> parameters, IPlugin plugin){
		super(parameters, plugin);
	}

	@Override
	public HashMap<String, IPluginSetting> getSettings(){
		HashMap<String, IPluginSetting> settings = new HashMap<>();
		settings.put("attack", new BasicPluginSetting("attack",0,4095, 2048));
		settings.put("decay", new BasicPluginSetting("decay",0,511, 64));
		settings.put("sustain", new BasicPluginSetting("sustain",0,255,127));
		settings.put("release", new BasicPluginSetting("release",0,4095, 128));

		return settings;
	}

	@Override
	public void generateOutput(){
		int mode = 0;
		int attack = (int)parameters.get("attack");
		int decay = (int)parameters.get("decay");
		int sustain = (int)parameters.get("sustain");
		int release = (int)parameters.get("release");

		
		int x, cm = 0; // cm=current multiplier
		int len = (int)parameters.get("sampleLength");
		int releasepos = len - (int)Math.floor((((float)len*(float)sustain)/100.0f * (float)release));

		sustain <<= 8;

		for(int i = 0; i < len; i++){
			x = buffer[i];
			switch(mode){
				case 0:  // Attack
					cm += attack;
					if(cm >= 255<<8){
						cm = 255<<8;
						mode = 1;  // Goto decay
					}
					break;

				case 1:  // Decay
					cm -= decay;
					if(cm <= sustain) {
						mode = 2;   // Sustain
					}
					break;

				case 2:  // Sustain
					if(i >= releasepos){
						mode = 3;   // Release
					}
					break;

				case 3:  // Release
					cm -= release;
					if(cm < 0) {
						cm = 0;
					}
					break;

			}
			x = (x * cm) >> 16;
			buffer[i] = (byte)(x);
		}
	}

}