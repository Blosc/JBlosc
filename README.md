# jblosc
Java interface for Blosc

The purpose of this project is to create a Java interface for the compressor Blosc. JNA has been chosen as the mechanism to communicate with the Blosc shared library.

A simple example extracted from the unit tests:

		int SIZE = 100 * 100 * 100;
		ByteBuffer ibb = ByteBuffer.allocateDirect(SIZE * PrimitiveSizes.DOUBLE_FIELD_SIZE);
		for (int i = 0; i < SIZE; i++) {
			ibb.putDouble(i);
		}
		BloscWrapper bw = new BloscWrapper();
		bw.init();
		ByteBuffer obb = ByteBuffer.allocateDirect(ibb.limit() + BloscWrapper.OVERHEAD);
		bw.compress(5, Shuffle.BYTE_SHUFFLE, PrimitiveSizes.DOUBLE_FIELD_SIZE, ibb, ibb.limit(), obb, obb.limit());
		ByteBuffer abb = ByteBuffer.allocateDirect(ibb.limit());
		bw.decompress(obb, abb, abb.limit());
		bw.destroy();
		assertEquals(ibb, abb);

Blosc shared library should be on PATH on Windows or in LD_LIBRARY_PATH on Linux/Unix.

Build: mvn clean install
