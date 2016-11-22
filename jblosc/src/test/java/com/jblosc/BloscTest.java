package com.jblosc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;

public class BloscTest {

	@Test
	public void testCompressDecompressJNA() {
		int SIZE = 100 * 100 * 100;
		float data[] = new float[SIZE];
		for (int i = 0; i < SIZE; i++) {
			data[i] = i * 2;
		}
		float data_out[] = new float[SIZE];
		long isize = SIZE * 4;
		Memory m = new Memory(isize);
		m.write(0, data, 0, SIZE);
		Memory m2 = new Memory(isize);
		// IBloscDll iBlosc = (IBloscDll) Native.loadLibrary("blosc" +
		// Util.getArchPlatform(), IBloscDll.class);
		IBloscDll.blosc_init();
		int size = IBloscDll.blosc_compress(5, 1, new NativeLong(4), new NativeLong(isize), m, m2,
				new NativeLong(isize));
		data_out = m2.getFloatArray(0, SIZE);
		Memory m3 = new Memory(isize);
		IBloscDll.blosc_decompress(m2, m3, new NativeLong(isize));
		float[] data_in = m3.getFloatArray(0, SIZE);
		assertArrayEquals(data, data_in, (float) 0);
		IBloscDll.blosc_destroy();
		assertNotNull(data_out);
		assert (size < isize);
	}

	@Test
	public void testSetCompressor() {
		System.out.println("*** testSetCompressor ***");
		int SIZE = 26214400;
		char data[] = new char[SIZE];
		for (int i = 0; i < SIZE; i++) {
			// data[i] = Math.random();
			data[i] = (char) i;
		}
		ByteBuffer b = Util.array2ByteBuffer(data);
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		System.out.println("Blosc version " + bw.getVersionString());
		bw.setNumThreads(4);
		System.out.println("Working with " + bw.getNumThreads() + " threads");
		assertEquals(bw.getNumThreads(), 4);
		String compnames = bw.listCompressors();
		String compnames_array[] = compnames.split(",");
		for (String compname : compnames_array) {
			bw.setCompressor(compname);
			String compname_out = bw.getCompressor();
			assertEquals(compname, compname_out);
			String[] ci = bw.getComplibInfo(compname);
			int compcode = bw.compnameToCompcode(compname);
			compname_out = bw.compcodeToCompname(compcode);
			assertEquals(compname, compname_out);
			System.out
					.println("Working with compressor " + compname + " (code " + compcode + ") " + ci[0] + " " + ci[1]);
			long startTime = System.currentTimeMillis();
			ByteBuffer o = ByteBuffer.allocateDirect(SIZE * 2 + BloscWrapper.OVERHEAD);
			// int s = bw.compressCtx(5, Shuffle.BYTE_SHUFFLE,
			// PrimitiveSizes.DOUBLE_FIELD_SIZE, b, SIZE * 8, o,
			// SIZE * 8 + BloscWrapper.OVERHEAD, compname, 0, 1);
			bw.compress(5, Shuffle.BYTE_SHUFFLE, PrimitiveSizes.CHAR_FIELD_SIZE, b,
					SIZE * PrimitiveSizes.CHAR_FIELD_SIZE, o,
					SIZE * PrimitiveSizes.CHAR_FIELD_SIZE + BloscWrapper.OVERHEAD);
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			printRatio(bw, "Char Array", o);
			BufferSizes bs = bw.cbufferSizes(o);
			double mb = bs.getNbytes() * 1.0 / (1024 * 1024);
			System.out.println("Compress time " + elapsedTime + " ms. "
					+ String.format("%.2f", (mb / elapsedTime) * 1000) + " Mb/s");
			startTime = System.currentTimeMillis();
			// double[] data_again = bw.decompressToDoubleArray(data_out,
			// bs.getNbytes());
			ByteBuffer a = ByteBuffer.allocateDirect(SIZE * 2);
			bw.decompress(o, a, SIZE * 2);
			stopTime = System.currentTimeMillis();
			elapsedTime = stopTime - startTime;
			mb = (bs.getNbytes() * 1.0) / (1024 * 1024);
			char[] data_again = Util.byteBufferToCharArray(a);
			System.out.println("Decompress time " + elapsedTime + " ms. "
					+ String.format("%.2f", (mb / elapsedTime) * 1000) + " Mb/s");
			assertArrayEquals(data, data_again);
		}
		bw.freeResources();
		bw.destroy();
	}

	private void printRatio(BloscWrapper bw, String title, ByteBuffer cbuffer) {
		BufferSizes bs = bw.cbufferSizes(cbuffer);
		System.out.println(title + ": " + bs.getCbytes() + " from " + bs.getNbytes() + ". Ratio: "
				+ (String.format("%.2f", (0.0 + bs.getNbytes()) / bs.getCbytes())));
	}

	@Test
	public void testCompressDecompressDouble() {
		int SIZE = 100 * 100 * 100;
		double data[] = new double[SIZE];
		for (int i = 0; i < SIZE; i++) {
			data[i] = i * 2;
		}
		ByteBuffer ibb = Util.array2ByteBuffer(data);
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		ByteBuffer obb = ByteBuffer.allocateDirect(ibb.limit() + BloscWrapper.OVERHEAD);
		bw.compress(5, Shuffle.BYTE_SHUFFLE, PrimitiveSizes.DOUBLE_FIELD_SIZE, ibb, ibb.limit(), obb, obb.limit());
		printRatio(bw, "Double", obb);
		ByteBuffer abb = ByteBuffer.allocateDirect(ibb.limit());
		bw.decompress(obb, abb, abb.limit());
		double[] data_again = Util.byteBufferToDoubleArray(abb);
		bw.destroy();
		assertArrayEquals(data, data_again, (float) 0);
	}

	@Test
	public void testCompressDecompressFloat() {
		int SIZE = 100 * 100 * 100;
		float data[] = new float[SIZE];
		for (int i = 0; i < SIZE; i++) {
			data[i] = i * 2;
		}
		ByteBuffer ibb = Util.array2ByteBuffer(data);
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		ByteBuffer obb = ByteBuffer.allocateDirect(ibb.limit() + BloscWrapper.OVERHEAD);
		bw.compress(5, Shuffle.BYTE_SHUFFLE, PrimitiveSizes.FLOAT_FIELD_SIZE, ibb, ibb.limit(), obb, obb.limit());
		printRatio(bw, "Float", obb);
		ByteBuffer abb = ByteBuffer.allocateDirect(ibb.limit());
		bw.decompress(obb, abb, abb.limit());
		float[] data_again = Util.byteBufferToFloatArray(abb);
		bw.destroy();
		assertArrayEquals(data, data_again, (float) 0);
	}

	@Test
	public void testCompressDecompressLong() {
		int SIZE = 100 * 100 * 100;
		long data[] = new long[SIZE];
		for (int i = 0; i < SIZE; i++) {
			data[i] = i * 2;
		}
		ByteBuffer ibb = Util.array2ByteBuffer(data);
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		ByteBuffer obb = ByteBuffer.allocateDirect(ibb.limit() + BloscWrapper.OVERHEAD);
		bw.compress(5, Shuffle.BYTE_SHUFFLE, PrimitiveSizes.LONG_FIELD_SIZE, ibb, ibb.limit(), obb, obb.limit());
		printRatio(bw, "Long", obb);
		ByteBuffer abb = ByteBuffer.allocateDirect(ibb.limit());
		bw.decompress(obb, abb, abb.limit());
		long[] data_again = Util.byteBufferToLongArray(abb);
		bw.destroy();
		assertArrayEquals(data, data_again);
	}

	@Test
	public void testCompressDecompressInt() {
		int SIZE = 100 * 100 * 100;
		int data[] = new int[SIZE];
		for (int i = 0; i < SIZE; i++) {
			data[i] = i * 2;
		}
		ByteBuffer ibb = Util.array2ByteBuffer(data);
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		ByteBuffer obb = ByteBuffer.allocateDirect(ibb.limit() + BloscWrapper.OVERHEAD);
		bw.compress(5, Shuffle.BYTE_SHUFFLE, PrimitiveSizes.INT_FIELD_SIZE, ibb, ibb.limit(), obb, obb.limit());
		printRatio(bw, "Int", obb);
		ByteBuffer abb = ByteBuffer.allocateDirect(ibb.limit());
		bw.decompress(obb, abb, abb.limit());
		int[] data_again = Util.byteBufferToIntArray(abb);
		bw.destroy();
		assertArrayEquals(data, data_again);
	}

	@Test
	public void testCompressDecompressDoubleCtx() {
		int SIZE = 100 * 100 * 100;
		double data[] = new double[SIZE];
		for (int i = 0; i < SIZE; i++) {
			data[i] = i * 2;
		}
		ByteBuffer ibb = Util.array2ByteBuffer(data);
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		ByteBuffer obb = ByteBuffer.allocateDirect(ibb.limit() + BloscWrapper.OVERHEAD);
		bw.compressCtx(5, Shuffle.BYTE_SHUFFLE, PrimitiveSizes.DOUBLE_FIELD_SIZE, ibb, ibb.limit(), obb, obb.limit(),
				"blosclz", 0, 4);
		printRatio(bw, "Double", obb);
		ByteBuffer abb = ByteBuffer.allocateDirect(ibb.limit());
		bw.decompressCtx(obb, abb, abb.limit(), 4);
		double[] data_again = Util.byteBufferToDoubleArray(abb);
		bw.destroy();
		assertArrayEquals(data, data_again, (float) 0);
	}
	
	@Test
	public void testCompressDecompressDirectBuffer() {
		int SIZE = 100 * 100 * 100;
		ByteBuffer ibb = ByteBuffer.allocateDirect(SIZE * PrimitiveSizes.DOUBLE_FIELD_SIZE);
		for (int i = 0; i < SIZE; i++) {
			ibb.putDouble(i);
		}
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		ByteBuffer obb = ByteBuffer.allocateDirect(ibb.limit() + BloscWrapper.OVERHEAD);
		bw.compress(5, Shuffle.BYTE_SHUFFLE, PrimitiveSizes.DOUBLE_FIELD_SIZE, ibb, ibb.limit(), obb, obb.limit());
		printRatio(bw, "Double", obb);
		ByteBuffer abb = ByteBuffer.allocateDirect(ibb.limit());
		bw.decompress(obb, abb, abb.limit());
		bw.destroy();
		assertEquals(ibb, abb);
	}
}
