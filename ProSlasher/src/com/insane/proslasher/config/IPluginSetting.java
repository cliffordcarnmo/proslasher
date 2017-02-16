
package com.insane.proslasher.config;

public interface IPluginSetting{
	String getName();
	int getMinValue();
	int getMaxValue();
	int getValue();
	void setValue(int v);
	int getDefaultValue();
}
