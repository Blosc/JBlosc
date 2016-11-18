package com.jblosc;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

public class Util {
	public static String getArchPlatform() {
		String archDataModel = System.getProperty("sun.arch.data.model");
		if (archDataModel.equals("64")) {
			archDataModel = "";
		}
		return archDataModel;
	}

	public static ByteBuffer array2ByteBuffer(char[] values) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(PrimitiveSizes.CHAR_FIELD_SIZE * values.length);

		for (char value : values) {
			buffer.putChar(value);
		}

		return buffer;
	}

	public static ByteBuffer array2ByteBuffer(double[] values) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(PrimitiveSizes.DOUBLE_FIELD_SIZE * values.length);

		for (double value : values) {
			buffer.putDouble(value);
		}

		return buffer;
	}

	public static ByteBuffer array2ByteBuffer(float[] values) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(PrimitiveSizes.FLOAT_FIELD_SIZE * values.length);

		for (float value : values) {
			buffer.putFloat(value);
		}

		return buffer;
	}

	public static ByteBuffer array2ByteBuffer(long[] values) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(PrimitiveSizes.LONG_FIELD_SIZE * values.length);

		for (long value : values) {
			buffer.putLong(value);
		}

		return buffer;
	}

	public static ByteBuffer array2ByteBuffer(int[] values) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(PrimitiveSizes.INT_FIELD_SIZE * values.length);

		for (int value : values) {
			buffer.putInt(value);
		}

		return buffer;
	}

	public static int[] byteBufferToIntArray(ByteBuffer buffer) {
		IntBuffer b = buffer.asIntBuffer();
		int[] array = new int[b.limit()];
		b.get(array);
		return array;
	}

	public static long[] byteBufferToLongArray(ByteBuffer buffer) {
		LongBuffer b = buffer.asLongBuffer();
		long[] array = new long[b.limit()];
		b.get(array);
		return array;
	}

	public static float[] byteBufferToFloatArray(ByteBuffer buffer) {
		FloatBuffer b = buffer.asFloatBuffer();
		float[] array = new float[b.limit()];
		b.get(array);
		return array;
	}

	public static double[] byteBufferToDoubleArray(ByteBuffer buffer) {
		DoubleBuffer b = buffer.asDoubleBuffer();
		double[] array = new double[b.limit()];
		b.get(array);
		return array;
	}

	public static char[] byteBufferToCharArray(ByteBuffer buffer) {
		CharBuffer b = buffer.asCharBuffer();
		char[] array = new char[b.limit()];
		b.get(array);
		return array;
	}

}
