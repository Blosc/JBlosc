package com.blosc_jna;

import static org.junit.Assert.*;

import org.junit.Test;

import com.blosc_jna.BloscWrapper;
import com.blosc_jna.IBloscDll;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;

public class BloscTest {
		
	@Test
	public void testCompressDecompressJNA() {
		int SIZE = 100 * 100 * 100;
		float data[] = new float[SIZE];
		for (int i=0; i<SIZE; i++) {
			data[i]=i*2;
		}
		float data_out[] = new float[SIZE];
		long isize = SIZE * 4;
		Memory m = new Memory(isize);
		m.write(0, data, 0, SIZE);
		Memory m2 = new Memory(isize);		
		IBloscDll iBlosc = (IBloscDll)Native.loadLibrary("blosc", IBloscDll.class);
		iBlosc.blosc_init();
		int size = iBlosc.blosc_compress(5, 1,new NativeLong(4), new NativeLong(isize), m, m2, new NativeLong(isize));
		data_out=m2.getFloatArray(0, SIZE);	
		Memory m3 = new Memory(isize);
		iBlosc.blosc_decompress(m2, m3, new NativeLong(isize));
		float[] data_in = m3.getFloatArray(0, SIZE);
		assertArrayEquals(data,data_in,(float)0);
		iBlosc.blosc_destroy();
		assertNotNull(data_out);
		assert(size<isize);
	}
	
	@Test
	public void testSetThreads() {
		int SIZE = 100 * 100 * 100;
		float data[] = new float[SIZE];
		for (int i=0; i<SIZE; i++) {
			data[i]=i*2;
		}
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		bw.setNumThreads(4);
		byte[] data_out = bw.compress(5, 1, data);
		printRatio(bw, "Float", data_out);
		float[] data_again=bw.decompressToFloatArray(data_out);
		bw.destroy();
		assertArrayEquals(data,data_again,(float)0);		
	}
	
	@Test
	public void testSetCompressor() {
		int SIZE = 100 * 100 * 100;
		float data[] = new float[SIZE];
		for (int i=0; i<SIZE; i++) {
			data[i]=i*2;
		}
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		bw.setNumThreads(4);
		String compnames = bw.listCompressors();
		String compnames_array[] = compnames.split(",");
		for (String compname: compnames_array) {
			bw.setCompressor(compname);
			byte[] data_out = bw.compress(5, 1, data);
			printRatio(bw, compname + " Float", data_out);
			float[] data_again=bw.decompressToFloatArray(data_out);
			assertArrayEquals(data,data_again,(float)0);
		}
		bw.destroy();
	}
	
	
	private void printRatio(BloscWrapper bw, String title, byte[] cbuffer) {
		BufferSizes bs = bw.cbufferSizes(cbuffer);
		System.out.println(title + ": " + bs.getCbytes() + " from " + bs.getNbytes());
	}
	
	@Test
	public void testCompressDecompressFloat() {
		int SIZE = 100 * 100 * 100;
		float data[] = new float[SIZE];
		for (int i=0; i<SIZE; i++) {
			data[i]=i*2;
		}
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		byte[] data_out = bw.compress(5, 1, data);
		printRatio(bw, "Float", data_out);
		float[] data_again=bw.decompressToFloatArray(data_out);
		bw.destroy();
		assertArrayEquals(data,data_again,(float)0);
	}
	
	@Test
	public void testCompressDecompressDouble() {
		int SIZE = 100 * 100 * 100;
		double data[] = new double[SIZE];
		for (int i=0; i<SIZE; i++) {
			data[i]=i*2;
		}
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		byte[] data_out = bw.compress(5, 1, data);
		printRatio(bw, "Double", data_out);
		double[] data_again=bw.decompressToDoubleArray(data_out);
		bw.destroy();
		assertArrayEquals(data,data_again,(float)0);
	}
	
	@Test
	public void testCompressDecompressByte() {
		int SIZE = 100 * 100 * 100;
		byte[] data = new byte[SIZE];
		for (int i=0; i<SIZE; i++) {
			data[i]=(byte) (i*2);
		}
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		byte[] data_out = bw.compress(5, 1, data);
		printRatio(bw, "Byte", data_out);
		byte[] data_again=bw.decompressToByteArray(data_out);
		bw.destroy();
		assertArrayEquals(data,data_again);
	}

	@Test
	public void testCompressDecompressInt() {
		int SIZE = 100 * 100 * 100;
		int[] data = new int[SIZE];
		for (int i=0; i<SIZE; i++) {
			data[i]= (i*2);
		}
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		byte[] data_out = bw.compress(5, 1, data);
		printRatio(bw, "Int", data_out);
		int[] data_again=bw.decompressToIntArray(data_out);
		bw.destroy();
		assertArrayEquals(data,data_again);
	}

	@Test
	public void testCompressDecompressLong() {
		int SIZE = 100 * 100 * 100;
		long[] data = new long[SIZE];
		for (int i=0; i<SIZE; i++) {
			data[i]= (i*2);
		}
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		byte[] data_out = bw.compress(5, 1, data);
		printRatio(bw, "Long", data_out);
		long[] data_again=bw.decompressToLongArray(data_out);
		bw.destroy();
		assertArrayEquals(data,data_again);
	}

	@Test
	public void testCompressDecompressChar() {
		int SIZE = 100 * 100 * 100;
		char[] data = new char[SIZE];
		for (int i=0; i<SIZE; i++) {
			data[i]= (char) (i*2);
		}
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		byte[] data_out = bw.compress(5, 1, data);
		printRatio(bw, "Char", data_out);
		char[] data_again=bw.decompressToCharArray(data_out);
		bw.destroy();
		assertArrayEquals(data,data_again);
	}
	
}
