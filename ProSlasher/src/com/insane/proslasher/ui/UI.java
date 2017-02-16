package com.insane.proslasher.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import com.insane.proslasher.audio.Player;
import com.insane.proslasher.audio.Waveform;
import com.insane.proslasher.config.IPluginSetting;
import com.insane.proslasher.config.ProslasherConfig;
import com.insane.proslasher.io.IFFExporter;
import com.insane.proslasher.plugins.IPlugin;
import com.insane.proslasher.plugins.PluginBase;
import com.insane.proslasher.plugins.PluginManager;

@SuppressWarnings("unchecked")
public class UI extends javax.swing.JFrame{
	
	public interface IPluginSettingUI
	{
		public JLabel getLabel();
		public JSlider getSlider();
	}

	public class BasicPluginSettingUI implements IPluginSettingUI
	{
		private JLabel label;
		private JSlider slider;
		
		public BasicPluginSettingUI(final IPluginSetting setting)
		{
			label = new JLabel(setting.getName());
			slider = new JSlider();
			slider.setMinimum(setting.getMinValue());
			slider.setMaximum(setting.getMaxValue());
			slider.setValue(setting.getDefaultValue());
			slider.setSize(256, 32);
			setting.setValue(setting.getDefaultValue());
			slider.addChangeListener(	new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e){
					JSlider slider = (JSlider)e.getSource();
					setting.setValue(slider.getValue());
					generateAndPlay();
				}
			});
		}
		
		public JSlider getSlider()
		{
			return slider;
		}
		
		public JLabel getLabel()
		{
			return label;
		}
	}
	
	private static final long serialVersionUID = 68010L;
	private ProslasherConfig currentConfig = new ProslasherConfig();
	private boolean loop;
	private boolean clickedA;
	private boolean clickedD;
	private boolean clickedS;
	private boolean clickedR;
	private JButton buttonAddPlugin;
	private JButton buttonPlay;
	private JButton buttonSave;
	private JComboBox comboAddPlugin;
	private JLabel labelAddPlugin;
	private JLabel labelWaveform;
	private JLabel labelModifyPlugin;
	private JLabel labelSampleFrequency;
	private JLabel labelSampleLength;
	private JList listPlugin;
	private Visualizer panelWaveform;
	private JScrollPane scrollPanelPlugin;
	private JSlider sliderSamplerate;
	private JSlider sliderSampleLength;
	private JCheckBox checkboxLoop;
	private Player player;
	private Thread playerThread;

	private javax.swing.BoxLayout settingsLayout;
	private javax.swing.JPanel settingsPanel;
	
	private	int currentActivePlugin=-1;
	private IPlugin activePlugin=null;
	private HashMap<String, IPluginSetting> activePluginSettings=null;
	
	private HashMap<IPluginSetting, UI.IPluginSettingUI> PluginSettingUIs = new HashMap<>();
	
	public UI(){
		initComponents();
	}

	private void initComponents(){
		panelWaveform = new Visualizer();
		comboAddPlugin = new JComboBox();
		buttonAddPlugin = new JButton();
		scrollPanelPlugin = new JScrollPane();
		listPlugin = new JList();
		sliderSamplerate = new JSlider(1, 44100);
		sliderSampleLength = new JSlider(1, 2048);

		buttonPlay = new JButton();
		buttonSave = new JButton();
		labelAddPlugin = new JLabel();
		labelWaveform = new JLabel();
		labelModifyPlugin = new JLabel();
		labelSampleFrequency = new JLabel();
		labelSampleLength = new JLabel();
		checkboxLoop = new JCheckBox("Loop");

		labelAddPlugin.setText("Add plugin");
		labelWaveform.setText("Waveform");
		labelModifyPlugin.setText("Active plugins");

		sliderSamplerate.setValue(5050);
		sliderSampleLength.setValue(512);
		updateLabels();

		ChangeListener cl = new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e){
				JSlider slider = (JSlider)e.getSource();
				if(!slider.getValueIsAdjusting())
					sliderChanged(e);
			}
		};
	
		checkboxLoop.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e){
				toggleLoop();
			}

		});

		sliderSamplerate.addChangeListener(cl);
		sliderSampleLength.addChangeListener(cl);
		buttonSave.setText("Save as 8SVX (IFF)");
		buttonSave.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt){
				save();				
			}

		});
		buttonPlay.setText("Play");
		buttonPlay.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt){
				playButtonAction(evt);
			}
		});
		
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		comboAddPlugin.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"SID_Square", "SID_Sine", "PinkNoise", "Squeel", "ADSR", "HighPassFilter", "Example"}));
		buttonAddPlugin.setText("Add");
		buttonAddPlugin.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt){
				String className = (String)comboAddPlugin.getSelectedItem();
				try {
					IPlugin plugin = PluginManager.getInstance().getPlugin(className);
					currentConfig.addPlugin(plugin, plugin.getSettings());
					listPlugin.updateUI();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

		});
		
		listPlugin.setModel(new javax.swing.AbstractListModel(){

			@Override
			public int getSize(){
				return currentConfig.Plugins.size();
			}

			@Override
			public Object getElementAt(int i){
				return currentConfig.Plugins.get(i).getClass().getCanonicalName();
			}

		});

		listPlugin.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse){
				int s = listPlugin.getSelectedIndex();
				if(s != currentActivePlugin) {

					updateLabels();  // Triggers save of current values to activePluginSettings before switching

					if(activePluginSettings != null) {
						for(Iterator it = activePluginSettings.keySet().iterator(); it.hasNext();){
							IPluginSettingUI o = PluginSettingUIs.get(activePluginSettings.get((String)it.next()));
							settingsPanel.remove(o.getLabel());
							settingsPanel.remove(o.getSlider());
						}
					}
					settingsLayout.invalidateLayout(settingsPanel);
					settingsLayout.layoutContainer(settingsPanel);
					activePlugin = currentConfig.Plugins.get(s);
					activePluginSettings = currentConfig.PluginSettings.get(activePlugin);
					currentActivePlugin = s;

					// Make sure there are UI elements for each setting
					for(Iterator it = activePluginSettings.keySet().iterator(); it.hasNext();){
						IPluginSetting o = activePluginSettings.get((String)it.next());
						if(!PluginSettingUIs.containsKey(o))
							PluginSettingUIs.put(o, new BasicPluginSettingUI(o));
						IPluginSettingUI ui = PluginSettingUIs.get(o);
						settingsPanel.add(ui.getLabel());
						settingsPanel.add(ui.getSlider());
					}
//					settingsLayout.layoutContainer(settingsPanel);
					pack();

					// TODO: Update UI to reflect new pluginsettings
					updateLabels();
				}
		}
		});
		
		scrollPanelPlugin.setViewportView(listPlugin);

		javax.swing.GroupLayout jPanel1Layout = new GroupLayout(panelWaveform);
		panelWaveform.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(
				jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGap(0, 0, Short.MAX_VALUE)
		);
		jPanel1Layout.setVerticalGroup(
				jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGap(0, 139, Short.MAX_VALUE)
		);

		settingsPanel = new JPanel();
		settingsLayout = new BoxLayout(settingsPanel,BoxLayout.PAGE_AXIS);

		javax.swing.GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(sliderSamplerate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(buttonPlay, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(buttonSave, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)								
								.addComponent(sliderSampleLength, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(settingsPanel)
								.addComponent(checkboxLoop, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
														.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
																.addComponent(comboAddPlugin, 0, 88, Short.MAX_VALUE)
																.addGap(10, 10, 10)
																.addComponent(buttonAddPlugin))
														.addComponent(scrollPanelPlugin))
												.addComponent(labelAddPlugin)
												.addComponent(labelModifyPlugin))
										.addGap(10, 10, 10)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addGroup(layout.createSequentialGroup()
														.addComponent(labelWaveform)
														.addGap(0, 360, Short.MAX_VALUE))
												.addComponent(panelWaveform, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(labelSampleFrequency)
												.addComponent(labelSampleLength)
												.addComponent(settingsPanel))
										.addGap(0, 0, Short.MAX_VALUE)))
						.addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(labelAddPlugin)
								.addComponent(labelWaveform))
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(buttonAddPlugin)
												.addComponent(comboAddPlugin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addGap(5, 5, 5)
										.addComponent(labelModifyPlugin)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(scrollPanelPlugin, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE))
								.addComponent(panelWaveform, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGap(10, 10, 10)
						.addComponent(labelSampleFrequency)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(sliderSamplerate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(labelSampleLength)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(sliderSampleLength, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(settingsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(checkboxLoop, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(10, 10, 10)
						.addComponent(buttonPlay)
						.addComponent(buttonSave)
						.addGap(10, 10, 10)
				)
		);
		
		pack();
	}

	private void save() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return(f.getName().toLowerCase().endsWith("iff"));
			}

			@Override
			public String getDescription() {
				return "8SVX IFF";
			}
			
		});
		if(JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(UI.this)) {
			File file = chooser.getSelectedFile();
			if(!file.getName().toLowerCase().endsWith(".iff")) {
				file = new File(file.getAbsoluteFile() + ".iff");
			}
			if(!file.exists() || JOptionPane.showConfirmDialog(UI.this, "The file " + file.getName() + " already exists, overwrite?", "Overwrite?", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {			
				IFFExporter ie = new IFFExporter(generate());
				try {
					ie.export(file);
				} catch(Exception e) {
					JOptionPane.showMessageDialog(UI.this, e.getMessage(), "Failed to save file", JOptionPane.ERROR_MESSAGE);
				}
			}
		}		
	}

	public static void main(String args[]){
		java.awt.EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){
				new UI().setVisible(true);
			}

		});
	}

	private void toggleLoop(){
		loop = checkboxLoop.isSelected();
	}

	private void sliderChanged(ChangeEvent e){
		Object source = e.getSource();
		JSlider eventSource = (JSlider)source;

		if((!clickedA || !clickedD || !clickedS || !clickedR) && eventSource.getValueIsAdjusting()){
			generateAndPlay();
		}
	}

	private void updateLabels(){
		labelSampleFrequency.setText("Sample frequency: " + sliderSamplerate.getValue());
		labelSampleLength.setText("Sample Length: " + sliderSampleLength.getValue());
	}

	private Waveform generate()
	{
		HashMap<String, Integer> parameters = new HashMap<>();
		parameters.put("sampleRate", sliderSamplerate.getValue());
		parameters.put("sampleSize", 8);
		parameters.put("sampleLength", sliderSampleLength.getValue());
		
		java.util.Iterator<IPlugin> it = currentConfig.Plugins.iterator();
		IPlugin first = it.next();
		first.setParameters(parameters);
		first.setSettings(currentConfig.PluginSettings.get(first).values());

		IPlugin next = null;
		while(it.hasNext()) {
			next = it.next();
			next.setPrevious(first);
			next.setParameters(parameters);
			next.setSettings(currentConfig.PluginSettings.get(next).values());
			first = next;
		}
		if(next == null)
			next = first; // Only 1 in chain
		

		return new Waveform(((PluginBase)next).getOutput(),
				(float)sliderSamplerate.getValue(),
				(int)8,
				(int)sliderSampleLength.getValue());
		
	}
	
	private void generateAndPlay(){
		updateLabels();

		final Waveform waveform = generate();

		if(playerThread != null){
			player.endPlay();
		}else{
			player = new Player();
			playerThread = new Thread(player, "ProSlasher Generator Thread");
			playerThread.start();
		}
		try{
			player.startPlay(waveform, loop);
		}catch(LineUnavailableException e){

		}

		new Thread(new Runnable() {
			@Override
			public void run(){
				panelWaveform.setData(waveform.getWaveform());
				panelWaveform.draw();
			}
			
		}).start();
	}

	private void playButtonAction(ActionEvent evt){
		generateAndPlay();
	}

}
