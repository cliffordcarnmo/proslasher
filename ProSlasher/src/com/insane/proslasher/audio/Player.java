package com.insane.proslasher.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Player implements Runnable{
	private SourceDataLine sourceDataLine;
	private boolean isRunning;

	public Player(){
	}

	public boolean isRunning(){
		return isRunning;
	}

	public boolean stop(){
		if(!isRunning){
			isRunning = false;
		}
		return !isRunning;
	}

	@Override
	protected void finalize() throws Throwable{
		if(isRunning){
			endPlay();
		}
		if(sourceDataLine != null){
			sourceDataLine = null;
		}
		super.finalize(); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void run(){
		isRunning = true;
		try{
		}catch(Exception ex){
		}
		isRunning = false;
	}

	public void endPlay(){
		if(sourceDataLine != null){
			sourceDataLine.stop();
			sourceDataLine.close();
		}
		sourceDataLine = null;
	}

	public void startPlay(final Waveform waveform, final boolean loop) throws LineUnavailableException{
		AudioFormat audioFormat = new AudioFormat(waveform.getSampleRate(), waveform.getSampleSize(), 1, true, false);

		sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
		sourceDataLine.open(audioFormat);
		sourceDataLine.start();
		sourceDataLine.write(waveform.getWaveform(), 0, waveform.getWaveformLength());
		sourceDataLine.addLineListener(new LineListener(){
			@Override
			public void update(LineEvent le){
				if(le.getType() == LineEvent.Type.STOP) {
					if(loop)
						sourceDataLine.write(waveform.getWaveform(), 0, waveform.getWaveformLength());
				}
			}

		});
		sourceDataLine.drain();
		sourceDataLine.stop();
		sourceDataLine.close();
	}

}
