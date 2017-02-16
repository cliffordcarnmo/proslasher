package com.insane.proslasher.plugins;

import com.insane.proslasher.config.IPluginSetting;
import java.util.*;

public interface IPlugin{

	void setParameters(HashMap<String,Integer> newParams);
	HashMap<String, IPluginSetting> getSettings();
	void setSettings(Collection<IPluginSetting> s);
	void setPrevious(IPlugin p);
	void generateOutput();
}