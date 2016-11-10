# jblosc
Java interface for blosc

The purpose of this project is to create a Java interface for the compressor Blosc. JNA has been chosen as the mechanism to communicate with the Blosc shared library. JNA is easy to use but performance is not as good as with JNI. However, for a proof of the concept approach that should be enough.

All the API is available through JNA programming using the IBloscDll class. However, JNA pure programming is cumbersome, so a BloscWrapper class that wrapps almost all the Blosc API functions has been written:

		BloscWrapper bw = new BloscWrapper();
		bw.init();
		double[] data_out = bw.compress(5, 1, data);
		double[] data_again=bw.decompressToDoubleArray(data_out);
    bw.destroy()

