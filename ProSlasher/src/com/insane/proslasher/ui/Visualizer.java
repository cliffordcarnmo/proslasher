package com.insane.proslasher.ui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Visualizer extends JPanel{
	private static final long serialVersionUID = 68020L;
	private byte[] buffer;
	private int midline;
	private int width;
	
	public Visualizer(){
		buffer = new byte[0];
	}

	public void draw(){
		this.midline = getSize().height / 2;
		repaint();
	}
	
	public void setData(byte[] buffer){
		this.buffer = buffer;
	}

	@Override
	public void paint(Graphics g){
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getSize().width, getSize().height);

		g.setColor(new Color(255, 255, 255));
		g.drawLine(0, midline, getSize().width, midline);

		if(buffer.length == 0)
			return;
		
		g.setColor(new Color(255, 255, 100));

		int width = getSize().width;
		int height = getSize().height/2;
		float step = ((float)(buffer.length)) / ((float)width);
		for(int i = 0; i < width; i++){
			int ii = (int)Math.floor(i*step);
			int sv = buffer[ii >= buffer.length ? buffer.length-1 : ii];
			float fr = ((float)height / 127.0f);
			g.drawLine(i, midline, i, midline + (int)Math.floor((float)sv*fr));
		}
	}
}