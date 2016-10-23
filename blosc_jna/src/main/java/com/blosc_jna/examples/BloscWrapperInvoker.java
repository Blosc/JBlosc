package com.blosc_jna.examples;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import com.blosc_jna.BloscWrapper;

public class BloscWrapperInvoker {
	public static byte[] FloatArray2ByteArray(float[] values){
        ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);

        for (float value : values){
            buffer.putFloat(value);
        }

        return buffer.array();
    }
	
	private static float[] toFloatArray(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        FloatBuffer fb = buffer.asFloatBuffer();
        float[] floatArray = new float[fb.limit()];
        fb.get(floatArray);
        return floatArray;
    }	

	private static double[] toDoubleArray(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        DoubleBuffer db = buffer.asDoubleBuffer();
        double[] doubleArray = new double[db.limit()];
        db.get(doubleArray);
        return doubleArray;
    }	
	
	public static void main(String[] args) {
		int SIZE = 100 * 100 * 100;
		double data[] = new double[SIZE];
		for (int i=0; i<SIZE; i++) {
			data[i]=i*2;
		}
		byte data_out[];
		BloscWrapper bw = new BloscWrapper();
		bw.init();
//		data_out = bw.compress(5, 1, FloatArray2ByteArray(data));
		data_out = bw.compress(5, 1, data);
		System.out.println("Items Original " + SIZE + ", Items Compressed " + data_out.length);
		double[] data_again=bw.decompressToDoubleArray(data_out);
//		double data_float_again[] = toDoubleArray(data_again);
		bw.destroy();
		System.out.println("Items Original " + data_out.length + ", Items Decompressed " + data_again.length);
		System.out.println("Finished!");
	}
}
