/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.insane.proslasher.config;

/**
 *
 * @author mrorigo
 */
public class BasicPluginSetting implements IPluginSetting {
	private int value;
	private String name;
	private int maxValue;
	private int minValue;
	private int defaultValue;
	
	public BasicPluginSetting(String name, int min, int max, int dflt) {
		this.name = name;
		minValue = min;
		maxValue = max;
		defaultValue = dflt;
	}

	public int getMinValue(){
		return minValue;
	}
	public int getMaxValue() {
		return maxValue;
	}

	public String getName() {
		return name;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void setValue(int v) {
		value = v;
	}

	public int getDefaultValue() {
		return defaultValue;
	}
}
