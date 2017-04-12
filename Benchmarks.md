A simple benchmark has been created using  https://github.com/ning/jvm-compressor-benchmark.

Compression:

![alt text](https://github.com/Blosc/jblosc/blob/master/benchmarks/2017_04_10_18_42_l7_s0_4threads_lz4/testcase0.jpg "Compression")

Decompression:

![alt text](https://github.com/Blosc/jblosc/blob/master/benchmarks/2017_04_10_18_42_l7_s0_4threads_lz4/testcase1.jpg "Decompression")

The full benchmark is available at folder benchmarks

You can add jblosc to the benchmark drivers available in jvm-compressor-benchmark following these steps:

1. Add the following class to the jvm-compressor-benchmark

```java
package com.ning.jcbm.jblosc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jblosc.BloscWrapper;
import com.jblosc.PrimitiveSizes;
import com.ning.jcbm.DriverBase;

/**
 * Driver for jblosc codec from [https://github.com/blosc/jblosc].
 */
public class jbloscDriver extends DriverBase {
	BloscWrapper bw = new BloscWrapper();

	public jbloscDriver() {
		super("jblosc");
		bw.init();
		bw.setNumThreads(4);
		bw.setCompressor("lz4");
	}

	@Override
	protected int compressBlock(byte[] uncompressed, byte[] compressBuffer) throws IOException {
		int SIZE = uncompressed.length;
		int oBufferSize = SIZE + BloscWrapper.OVERHEAD;
		int iReturn = bw.compress(7, 0, PrimitiveSizes.BYTE_FIELD_SIZE, uncompressed, SIZE, compressBuffer,
				oBufferSize);
		return iReturn;
	}

	@Override
	protected int uncompressBlock(byte[] compressed, byte[] uncompressBuffer) throws IOException {
		int SIZE = uncompressBuffer.length;
		int iReturn = bw.decompress(compressed, uncompressBuffer, SIZE);
		return iReturn;
	}

	@Override
	protected void compressToStream(byte[] uncompressed, OutputStream rawOut) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected int uncompressFromStream(InputStream compIn, byte[] inputBuffer) throws IOException {
		throw new UnsupportedOperationException();
	}
}
```

2. Add the required jars

Create a lib\jblosc folder inside the downloaded jvm-compressor-benchmark-master\ folder
Then add there the jblosc.jar (You have to compile jblosc from the sources and export to jar) and jna-4.4.0.jar (or any jna version starting from 4.x) in that folder.

3. Add the jblosc driver to any test case you want to run

The test cases are in the cfg folder of jvm-compressor-benchmark-master. You can, for example, copy the tests-minimal.xml into tests-minimal-jblosc.xml and inside this file add the jblosc driver:

```xml
  <driver name="jblosc/block" normal="false">
        <description><div xmlns=""><p>jblosc, block mode</p></div></description>
        <param name="japex.classPath" value="build/classes"/>
        <param name="japex.classPath" value="lib/jblosc/*.jar"/>
        <param name="japex.driverClass" value="com.ning.jcbm.jblosc.jbloscDriver"/>
  </driver>
```
4. Create a .sh that launches the new test case:

For example:

```
#!/bin/sh
 
echo "About to run minimal sanity test on 3 input files, couple of codecs"

java -server -cp lib/japex/\* \
 -Xmx256M \
 -Djava.library.path=lib/jni \
 -Djava.awt.headless=true \
 -Djapex.runsPerDriver=1 \
 -Djapex.warmupTime=5 \
 -Djapex.runTime=15 \
 -Djapex.numberOfThreads=1 \
 -Djapex.reportsDirectory=reports/minimal \
 -Djapex.plotGroupSize=5 \
 -Djapex.inputDir="testdata/canterbury" \
 com.sun.japex.Japex \
 cfg/tests-minimal-jblosc.xml

echo "Done!";
```
5. Compile jvm-compressor-benchmark as it is stated in its instructions±

ant compile

6. Run the benchmark

You can use cygwin on windows, in order tu run the shell , or create .bat equivalent file
