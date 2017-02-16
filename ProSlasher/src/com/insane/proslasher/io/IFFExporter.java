package com.insane.proslasher.io;

import com.insane.proslasher.audio.Waveform;
import java.io.*;
import java.util.*;

public class IFFExporter implements IExporter{
	
	private Waveform Waveform;
	
	private abstract class IFF_Chunk{
		public String Type_ID;

		public IFF_Chunk(String typeId){
			this.Type_ID = typeId.substring(0,4).toUpperCase();
		}

		abstract byte[] getData();

	}

	private class IFF_FORM_Chunk extends IFF_Chunk{

		public List<IFF_Chunk> SubChunks;

		public IFF_FORM_Chunk(List<IFF_Chunk> subChunks){
			super("FORM");
			SubChunks = subChunks;
		}

		@Override
		public byte[] getData(){
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				for(IFF_Chunk chunk : SubChunks) {
					bos.write(chunk.getData());
				}
				int len = bos.size();
				ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
				bos2.write("FORM".getBytes());
				bos2.write(len>>24&255);
				bos2.write(len>>16&255);
				bos2.write(len>>8&255);
				bos2.write(len&255);
				bos2.write(bos.toByteArray());
				return bos2.toByteArray();
			} catch(Exception e) {
				
			}
			return new byte[0];
		}

	}
	
	private class IFF_8SVX_Chunk extends IFF_Chunk{
		private List<IFF_Chunk> SubChunks;
		
		public IFF_8SVX_Chunk(byte[] sampleData, int sampleRate) {
			super("8SVX");
			SubChunks = new ArrayList<>();
			SubChunks.add(new IFF_8SVX_VHDR_Chunk(sampleData.length, sampleRate));
			SubChunks.add(new IFF_8SVX_NAME_Chunk("ProSlashes Sample"));
			SubChunks.add(new IFF_8SVX_ANNO_Chunk("ProSlasher"));
			SubChunks.add(new IFF_8SVX_BODY_Chunk(sampleData));
		}
		
		@Override
		public byte[] getData(){
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				for(IFF_Chunk chunk : SubChunks) {
					bos.write(chunk.getData());
				}

				ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
				bos2.write("8SVX".getBytes());
				bos2.write(bos.toByteArray());
				return bos2.toByteArray();
			} catch(Exception e) {
				
			}
			return new byte[]{0};
		}
	}

	private class IFF_8SVX_NAME_Chunk extends IFF_Chunk{
		public String Name;
		
		public IFF_8SVX_NAME_Chunk(String sampleName) {
			super("NAME");
			Name = sampleName;
		}
	
		@Override
		public byte[] getData(){
			try {
				int pad = 24-Name.getBytes().length;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bos.write("NAME".getBytes());
				bos.write(new byte[]{0,0,0,24});  // PT requires 24!
				bos.write(Name.substring(0, Math.min(Name.length(),23)).getBytes());
				bos.write(new byte[pad]);
				return bos.toByteArray();
			} catch(Exception e) {
			
			}
			return new byte[]{0};
		}
	}

	private class IFF_8SVX_ANNO_Chunk extends IFF_Chunk{
		public String Anno;
		
		public IFF_8SVX_ANNO_Chunk(String annotation) {
			super("ANNO");
			Anno = annotation;
		}
	
		@Override
		public byte[] getData(){
			try {
				int pad = 16-Anno.getBytes().length;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bos.write("ANNO".getBytes());
				bos.write(new byte[]{0,0,0,16});  // PT requires 16!
				bos.write(Anno.substring(0, Math.min(Anno.length(),15)).getBytes());
				bos.write(new byte[pad]);
				return bos.toByteArray();
			} catch(Exception e) {
			
			}
			return new byte[]{0};
		}
	}

		private class IFF_8SVX_BODY_Chunk extends IFF_Chunk{
		public byte[] Data;
		
		public IFF_8SVX_BODY_Chunk(byte[] data) {
			super("BODY");
			Data = data;
		}
	
		@Override
		public byte[] getData(){
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bos.write("BODY".getBytes());
				bos.write(Data.length >> 24 & 255);
				bos.write(Data.length >> 16 & 255);
				bos.write(Data.length >> 8 & 255);
				bos.write(Data.length & 255);
				bos.write(Data);
				return bos.toByteArray();
			} catch(Exception e) {

			}
			return new byte[]{0};
		}
	}

	private class IFF_8SVX_VHDR_Chunk extends IFF_Chunk{
		public int SampleLength;
		public int SampleRate;

		public IFF_8SVX_VHDR_Chunk(int sampleLength, int sampleRate) {
			super("VHDR");
			SampleLength = sampleLength;
			SampleRate = sampleRate;
		}

		@Override
		public byte[] getData(){
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bos.write("VHDR".getBytes());
				bos.write(new byte[]{0,0,0,20});  // samplesPerHiCycle
				bos.write(SampleLength >> 24 & 255);
				bos.write(SampleLength >> 16 & 255);
				bos.write(SampleLength >> 8 & 255);
				bos.write(SampleLength & 255);
				bos.write(new byte[]{0,0,0,0});  // repeatHiSamples
				bos.write(new byte[]{0,0,0,32});  // samplesPerHiCycle

				bos.write(SampleRate >> 8 & 255); 
				bos.write(SampleRate & 255);	// samplesPerSec

				bos.write(1); // ctOctave
				bos.write(0); // sCompression
				bos.write(new byte[]{0,1,0,0});
				return bos.toByteArray();
			} catch(Exception e) {
				
			}
			return new byte[]{0};
		}
	}
	
	private class IFF_File{
		public List<IFF_Chunk> Chunks;

		public IFF_File(){
			this.Chunks = new ArrayList<IFF_Chunk>();
		}

		public byte[] getData() {
			// Calc size
			Iterator<IFF_Chunk> i = Chunks.iterator();
			int totSize = 0;
			while(i.hasNext())
				totSize += i.next().getData().length;
			
			byte[] buf = new byte[totSize];
			i = Chunks.iterator();
			int k=0;
			while(i.hasNext()) {
				byte[] b = i.next().getData();
				for(int a=0; a<b.length;a++)
					buf[k++] = b[a];
			}
			
			return buf;
		}
	}

	public IFFExporter(Waveform waveform){
		Waveform = waveform;
	}

	@Override
	public void export(java.io.File outFile) throws Exception {
		IFF_File fil = new IFF_File();
		List<IFF_Chunk> subchunks = new ArrayList<IFF_Chunk>();
		subchunks.add(new IFF_8SVX_Chunk(Waveform.getWaveform(), (int)Waveform.getSampleRate()));
		fil.Chunks.add(new IFF_FORM_Chunk(subchunks));
		try {
			FileOutputStream fo = new FileOutputStream(outFile);
			fo.write(fil.getData());
			fo.flush();
			fo.close();
		} catch(Exception e) {
			System.out.println("Exception while writing file " + outFile.getAbsolutePath() + ": " + e.getMessage());
			throw e;
		}
	}

}