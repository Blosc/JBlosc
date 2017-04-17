package com.jblosc.examples;

import java.nio.ByteBuffer;

import com.jblosc.BloscWrapper;
import com.jblosc.PrimitiveSizes;
import com.jblosc.Util;

public class BloscWrapperInvoker {

	public static void main(String[] args) {
		int SIZE = 100 * 100 * 100;
		double data[] = new double[SIZE];
		for (int i = 0; i < SIZE; i++) {
			data[i] = i * 2;
		}
		ByteBuffer ibb = Util.array2ByteBuffer(data);
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		int iBufferSize = SIZE * PrimitiveSizes.DOUBLE_FIELD_SIZE;
		int oBufferSize = SIZE * PrimitiveSizes.DOUBLE_FIELD_SIZE + BloscWrapper.OVERHEAD;
		ByteBuffer obb = ByteBuffer.allocateDirect(oBufferSize);
		int w = bw.compress(5, 1, PrimitiveSizes.DOUBLE_FIELD_SIZE, ibb, iBufferSize, obb, oBufferSize);
		ByteBuffer abb = ByteBuffer.allocateDirect(iBufferSize);
		bw.decompress(obb, abb, iBufferSize);
		double[] data_again = Util.byteBufferToDoubleArray(abb);
		bw.destroy();
		System.out.println(
				"Items Original " + data.length + ", Items compressed " + w + ", Items again " + data_again.length);
		System.out.println("Finished!");
	}
}
