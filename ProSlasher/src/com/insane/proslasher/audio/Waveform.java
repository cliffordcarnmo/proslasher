package com.insane.proslasher.audio;

public class Waveform{
	private final float sampleRate;
	private final int sampleSize;
	private final int length;
	private final byte[] outputBuffer;
	
	public Waveform(byte[] outputBuffer, float sampleRate, int sampleSize, int length){
		this.sampleRate = sampleRate;
		this.sampleSize = sampleSize;
		this.length = length;
		this.outputBuffer = outputBuffer;
	}

	public byte[] getWaveform(){
		return outputBuffer;
	}

	public int getWaveformLength(){
		return length;
	}

	public float getSampleRate(){
		return sampleRate;
	}

	public int getSampleSize(){
		return sampleSize;
	}
}
