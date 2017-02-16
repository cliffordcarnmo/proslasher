/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.insane.proslasher.plugins;

import com.insane.proslasher.config.BasicPluginSetting;
import com.insane.proslasher.config.IPluginSetting;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author mrorigo
 */
public class PinkNoise extends PluginBase{
	private PinkNoiseGenerator pn;

	public class PinkNoiseGenerator{
		public final int poles;
		public final double alpha;
		private final double[] multipliers;

		private final double[] values;
		private final Random rnd;

		/**
		 * Generate pink noise with alpha=1.0 using a five-pole IIR.
		 */
		public PinkNoiseGenerator(){
			this(1.0, 5, new Random());
		}

		/**
		 * Generate a specific pink noise using a five-pole IIR.
		 *
		 * @param alpha the exponent of the pink noise, 1/f^alpha.
		 * @throws IllegalArgumentException if <code>alpha < 0</code> or
		 * <code>alpha > 2</code>.
		 */
		public PinkNoiseGenerator(double alpha){
			this(alpha, 5, new Random());
		}

		/**
		 * Generate pink noise specifying alpha and the number of poles. The
		 * larger the number of poles, the lower are the lowest frequency
		 * components that are amplified.
		 *
		 * @param alpha the exponent of the pink noise, 1/f^alpha.
		 * @param poles the number of poles to use.
		 * @throws IllegalArgumentException if <code>alpha < 0</code> or
		 * <code>alpha > 2</code>.
		 */
		public PinkNoiseGenerator(double alpha, int poles){
			this(alpha, poles, new Random());
		}

		/**
		 * Generate pink noise from a specific randomness source specifying
		 * alpha and the number of poles. The larger the number of poles, the
		 * lower are the lowest frequency components that are amplified.
		 *
		 * @param alpha the exponent of the pink noise, 1/f^alpha.
		 * @param poles the number of poles to use.
		 * @param random the randomness source.
		 * @throws IllegalArgumentException if <code>alpha < 0</code> or
		 * <code>alpha > 2</code>.
		 */
		public PinkNoiseGenerator(double alpha, int poles, Random random){
			if(alpha < 0 || alpha > 2){
				throw new IllegalArgumentException("Invalid pink noise alpha = "
						+ alpha);
			}

			this.rnd = random;
			this.alpha = alpha;
			this.poles = poles;
			this.multipliers = new double[poles];
			this.values = new double[poles];

			double a = 1;
			for(int i = 0; i < poles; i++){
				a = (i - alpha / 2) * a / (i + 1);
				multipliers[i] = a;
			}

			// Fill the history with random values
			for(int i = 0; i < 5 * poles; i++){
				this.nextValue();
			}
		}

		/**
		 * Return the next pink noise sample.
		 *
		 * @return the next pink noise sample.
		 */
		public double nextValue(){
			/*
			 * The following may be changed to  rnd.nextDouble()-0.5
			 * if strict Gaussian distribution of resulting values is not
			 * required.
			 */
			double x = rnd.nextGaussian();

			for(int i = 0; i < poles; i++){
				x -= multipliers[i] * values[i];
			}
			System.arraycopy(values, 0, values, 1, values.length - 1);
			values[0] = x;

			return x;
		}

	}

	@Override
	public HashMap<String, IPluginSetting> getSettings(){
		HashMap<String, IPluginSetting> settings = new HashMap<>();

		settings.put("alpha", new BasicPluginSetting("alpha", 0, 255, 127));
		settings.put("poles", new BasicPluginSetting("poles", 1, 10, 5));
		return settings;
	}

	private byte[] saveBuffer;

	@Override
	public void generateOutput(){
		int len = (int)parameters.get("sampleLength");
		int ialpha = (int)parameters.get("alpha");
		double alpha = (double)ialpha / 127.0f;
		int poles = (int)parameters.get("poles");
		if(pn == null || (saveBuffer != null && saveBuffer.length != len) || alpha != pn.alpha || poles != pn.poles){
			pn = new PinkNoiseGenerator(alpha, poles);

			saveBuffer = new byte[buffer.length];
			for(int i = 0; i < len; i++){
				saveBuffer[i] = (byte)(pn.nextValue() * 255 - 128);
			}
		}
		for(int i = 0; i < len; i++){
			buffer[i] = saveBuffer[i];
		}
	}

}
