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

Blosc shared library should be in PATH on Windows or in LD_LIBRARY_PATH on Linux/Unix.

Also check your OS, Java Virtual Machine and blosc.dll are from the same architecture, or it won't work.  
In windows with Microsoft Visual Studio compiler you may need to enforce the 64 bits architecture on blosc.dll, you can do this by adding in cmake the flag `-A x64` (e.g. `cmake -A x64 -DDEACTIVATE_ZLIB=ON ..`)

Build: mvn clean install

If you want to use it in another maven project, after installing it you can use it as a dependency like this:

		<dependencies>
			<dependency>
				<groupId>com.jblosc</groupId>
				<artifactId>jblosc</artifactId>
				<version>0.0.1-SNAPSHOT</version>
			</dependency>
		</dependencies>
