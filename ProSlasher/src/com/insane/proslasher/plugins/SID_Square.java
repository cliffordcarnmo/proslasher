package com.insane.proslasher.plugins;

import java.util.HashMap;

public class SID_Square extends SID_Sine {
	public SID_Square() {
		super(new HashMap<String, Integer>());
	}

	public SID_Square(HashMap<String, Integer> parameters){
		super(parameters);
	}

	@Override
	public void generateOutput(){
		super.generateOutput();
		int ampl = (int)parameters.get("amplitude");
		for(int i = 0; i < (int)parameters.get("sampleLength"); i++){
			byte x = buffer[i];
			x = (byte)(x > 0 ? ampl : -ampl);
			buffer[i] = x;
		}

	}

}