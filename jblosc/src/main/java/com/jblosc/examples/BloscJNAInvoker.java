package com.jblosc.examples;

import com.jblosc.IBloscDll;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class BloscJNAInvoker {

	public static void main(String[] args) {
		int SIZE = 100 * 100 * 100 * 10;
		byte data[] = new byte[SIZE];
		for (int i = 0; i < SIZE; i++) {
			data[i] = 'a';
		}
		byte dataout[] = new byte[SIZE];
		long isize = SIZE * 1;
		long startTime = System.currentTimeMillis();
		// IBloscDll iBlosc = (IBloscDll) Native.loadLibrary("blosc",
		// IBloscDll.class);
		Memory m = new Memory(isize);
		m.write(0, data, 0, SIZE);
		Memory m2 = new Memory(isize);
		IBloscDll.blosc_init();
		IBloscDll.blosc_set_nthreads(2);
		System.out.println("Threads " + IBloscDll.blosc_get_nthreads());
		int size = IBloscDll.blosc_compress(5, 1, new NativeLong(4), new NativeLong(isize), data, dataout,
				new NativeLong(isize));
		long stopTime = System.currentTimeMillis();
		System.out.println("Compress time " + (stopTime - startTime) + " ms");
		System.out.println("Size " + size);
		IBloscDll.blosc_set_compressor("blosclz");
		System.out.println(IBloscDll.blosc_get_compressor());
		PointerByReference ptr = new PointerByReference();
		Pointer p = ptr.getValue();
		Memory m3 = new Memory(100);
		m3.setPointer(0, p);
		IBloscDll.blosc_compcode_to_compname(2, ptr);
		System.out.println("Compname " + ptr.getValue().getString(0));
		int compcode = IBloscDll.blosc_compname_to_compcode("lz4hc");
		System.out.println("Compcode " + compcode);
		System.out.println("List " + IBloscDll.blosc_list_compressors());
		System.out.println("List " + IBloscDll.blosc_get_version_string());
		IBloscDll.blosc_destroy();
		System.out.println("Finished!");
	}

}
