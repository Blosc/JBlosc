package com.jblosc.examples;

import java.nio.ByteBuffer;

import com.jblosc.JBlosc;
import com.jblosc.PrimitiveSizes;
import com.jblosc.Util;

public class JBloscInvoker {

	public static void main(String[] args) {
		int SIZE = 100 * 100 * 100;
		double data[] = new double[SIZE];
		for (int i = 0; i < SIZE; i++) {
			data[i] = i * 2;
		}
		ByteBuffer ibb = Util.array2ByteBuffer(data);
		JBlosc jb = new JBlosc();
		int iBufferSize = SIZE * PrimitiveSizes.DOUBLE_FIELD_SIZE;
		int oBufferSize = SIZE * PrimitiveSizes.DOUBLE_FIELD_SIZE + JBlosc.OVERHEAD;
		ByteBuffer obb = ByteBuffer.allocateDirect(oBufferSize);
		int w = jb.compress(5, 1, PrimitiveSizes.DOUBLE_FIELD_SIZE, ibb, iBufferSize, obb, oBufferSize);
		ByteBuffer abb = ByteBuffer.allocateDirect(iBufferSize);
		jb.decompress(obb, abb, iBufferSize);
		double[] data_again = Util.byteBufferToDoubleArray(abb);
		jb.destroy();
		System.out.println(
				"Items Original " + data.length + ", Items compressed " + w + ", Items again " + data_again.length);
		System.out.println("Finished!");
	}
}
