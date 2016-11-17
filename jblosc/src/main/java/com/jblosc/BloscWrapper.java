package com.jblosc;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * The purpose of this class is to group JNA i/o parameters
 * 
 * @author aalted
 *
 */
class CStruct {
	public int fieldSize;
	public int items;
	public long isize;
	public long size;
	public Memory m_in;
	public Memory m_out;

	/**
	 * Initialization of fields used in compress and decompress
	 * 
	 * @param fieldSize
	 * @param items
	 */
	private void inAssignments(int fieldSize, int items) {
		this.fieldSize = fieldSize;
		this.items = items;
		this.isize = items * fieldSize;
		m_in = new Memory(isize);
	}

	/**
	 * Constructor used for compress and decompress
	 * 
	 * @param fieldSize
	 * @param items
	 * @param compress
	 */
	public CStruct(int fieldSize, int items, boolean compress) {
		inAssignments(fieldSize, items);
		if (compress) {
			m_out = new Memory(isize);
		}
	}
}

/**
 * Class that adds a more Java oriented programming vision to te JNA interface
 * 
 * @author aalted
 *
 */
public class BloscWrapper {
	IBloscDll iBloscDll;

	public static final int OVERHEAD = 16;

	/**
	 * In the constructor we try to load the blosc shared library. For 64 bit
	 * JVM it wil try to load blosc.dll or libblosc.so For 32 bit JVM it will
	 * try to load blosc32.dll or libblosc32.so
	 */
	public BloscWrapper() {
		// iBloscDll = (IBloscDll) Native.loadLibrary("blosc" +
		// Util.getArchPlatform(), IBloscDll.class);
	}

	/**
	 * Helper method intended to reduce code in the public compress methods
	 * Calls the JNA blosc_compress and establishes the compressed size
	 * 
	 * @param clevel
	 * @param doshuffle
	 * @param cs
	 * @return
	 */
	private int itemsCompressed(int clevel, int doshuffle, CStruct cs) {
		int size = iBloscDll.blosc_compress(clevel, doshuffle, new NativeLong(cs.fieldSize), new NativeLong(cs.isize),
				cs.m_in, cs.m_out, new NativeLong(cs.isize * 2));
		if (size == 0) {
			throw new RuntimeException("Compressed size larger then dest length");
		}
		if (size == -1) {
			throw new RuntimeException("Error compressing data: src: " + cs.m_in + ", dst: " + cs.m_out);
		}
		cs.size = size;
		return size;
	}

	/**
	 * Helper method intended to reduce code in the publib decompressX mtehods
	 * Calls the JNA blosc_decompress, but before it reads the original
	 * decompressed size from the header (JNA call to blosc_cbuffer_sizes). It's
	 * slower than the version with the destsize
	 * 
	 * @param cs
	 * @return
	 */
	private int itemsDecompressed(CStruct cs) {
		BufferSizes bs = cbufferSizes(cs.m_in.getByteArray(0, (int) cs.m_in.size()));
		cs.m_out = new Memory(bs.getNbytes());
		int size = iBloscDll.blosc_decompress(cs.m_in, cs.m_out, new NativeLong(bs.getNbytes()));
		return size / cs.fieldSize;
	}

	/**
	 * Takes an additional parameter which is the decompressed size. It's faster
	 * since it is not need to call c_buffer_sizes.
	 * 
	 * @param cs
	 * @param destSize
	 * @return
	 */
	private int itemsDecompressed(CStruct cs, long destSize) {
		cs.m_out = new Memory(destSize);
		int size = iBloscDll.blosc_decompress(cs.m_in, cs.m_out, new NativeLong(destSize));
		return size / cs.fieldSize;
	}

	/**
	 * Call to the JNA blosc_init()
	 */
	public void init() {
		iBloscDll.blosc_init();
	}

	/**
	 * Call to the JNA blosc_destroy()
	 */
	public void destroy() {
		iBloscDll.blosc_destroy();
	}

	/**
	 * Call to the JNA blosc_set_nthreads
	 * 
	 * @param nthreads
	 */
	public void setNumThreads(int nthreads) {
		iBloscDll.blosc_set_nthreads(nthreads);
	}

	/**
	 * call to the JNA blosc_get_nthreads
	 * 
	 * @return
	 */
	public int getNumThreads() {
		return iBloscDll.blosc_get_nthreads();
	}

	/**
	 * Call the JNA blosc_list_compressors
	 * 
	 * @return
	 */
	public String listCompressors() {
		return iBloscDll.blosc_list_compressors();
	}

	/**
	 * Call to the JNA blosc_set_compressor
	 * 
	 * @param compname
	 */
	public void setCompressor(String compname) {
		iBloscDll.blosc_set_compressor(compname);
	}

	/**
	 * Call to the JNA blosc_get_compressor
	 * 
	 * @return
	 */
	public String getCompressor() {
		return iBloscDll.blosc_get_compressor();
	}

	/**
	 * Call to the JNA blosc_compname_to_compcode
	 * 
	 * @param compname
	 * @return
	 */
	public int compnameToCompcode(String compname) {
		return iBloscDll.blosc_compname_to_compcode(compname);
	}

	/**
	 * Call to the JNA blosc_compcode_to_compname
	 * 
	 * @param compcode
	 * @return
	 */
	public String compcodeToCompname(int compcode) {
		PointerByReference ptr = new PointerByReference();
		iBloscDll.blosc_compcode_to_compname(compcode, ptr);
		Pointer p = ptr.getValue();
		return p.getString(0);
	}

	/**
	 * Call to the JNA blosc_get_version_string
	 * 
	 * @return
	 */
	public String getVersionString() {
		return iBloscDll.blosc_get_version_string();
	}

	/**
	 * Call to the JNA blosc_get_complib_info If compname is wrong then
	 * unchecked IllegalArgumentException is thrown
	 * 
	 * @param compname
	 * @return a 2 elements array: 0 -> complib, 1 -> version
	 */
	String[] getComplibInfo(String compname) {
		PointerByReference ptrComplib = new PointerByReference();
		PointerByReference ptrVersion = new PointerByReference();
		int compcode = iBloscDll.blosc_get_complib_info(compname, ptrComplib, ptrVersion);
		if (compcode == -1) {
			throw new IllegalArgumentException();
		}
		String[] result = new String[2];
		result[0] = ptrComplib.getValue().getString(0);
		result[1] = ptrVersion.getValue().getString(0);
		return result;
	}

	/**
	 * Call to the JNA blosc_free_resources throws an uncheked RuntimeException
	 * if there are problems freeing resources
	 */
	public void freeResources() {
		if (iBloscDll.blosc_free_resources() == -1) {
			throw new RuntimeException();
		}
	}

	/**
	 * Call to the JNA blosc_get_blocksize method
	 * 
	 * @return
	 */
	public int getBlocksize() {
		return iBloscDll.blosc_get_blocksize();
	}

	/**
	 * Call to the JNA blosc_cbuffer_sizes
	 * 
	 * @param cbuffer
	 * @return
	 */
	public BufferSizes cbufferSizes(byte[] cbuffer) {
		NativeLongByReference nbytes = new NativeLongByReference();
		NativeLongByReference cbytes = new NativeLongByReference();
		NativeLongByReference blocksize = new NativeLongByReference();
		Memory m_in = new Memory(cbuffer.length);
		m_in.write(0, cbuffer, 0, cbuffer.length);
		iBloscDll.blosc_cbuffer_sizes(m_in, nbytes, cbytes, blocksize);
		BufferSizes bs = new BufferSizes(nbytes.getValue().longValue(), cbytes.getValue().longValue(),
				blocksize.getValue().longValue());
		return bs;
	}

	public byte[] compress(int clevel, int doshuffle, byte[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.BYTE_FIELD_SIZE, src.length, true);
		cs.m_in.write(0, src, 0, cs.items);
		return cs.m_out.getByteArray(0, itemsCompressed(clevel, doshuffle, cs));
	}

	public byte[] compress(int clevel, int doshuffle, float[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.FLOAT_FIELD_SIZE, src.length, true);
		cs.m_in.write(0, src, 0, cs.items);
		return cs.m_out.getByteArray(0, itemsCompressed(clevel, doshuffle, cs));
	}

	public byte[] compress(int clevel, int doshuffle, double[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.DOUBLE_FIELD_SIZE, src.length, true);
		long startTime = System.currentTimeMillis();
		cs.m_in.write(0, src, 0, cs.items);
		long stopTime = System.currentTimeMillis();
		System.out.println("Memory write time " + (stopTime - startTime) + " ms");
		startTime = System.currentTimeMillis();
		int items = itemsCompressed(clevel, doshuffle, cs);
		stopTime = System.currentTimeMillis();
		System.out.println("JNA call " + (stopTime - startTime) + " ms");
		startTime = System.currentTimeMillis();
		byte[] result = cs.m_out.getByteArray(0, items);
		stopTime = System.currentTimeMillis();
		System.out.println("Memory read time " + (stopTime - startTime) + " ms");
		return result;
	}

	public byte[] compress(int clevel, int doshuffle, char[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.CHAR_FIELD_SIZE, src.length, true);
		cs.m_in.write(0, src, 0, cs.items);
		return cs.m_out.getByteArray(0, itemsCompressed(clevel, doshuffle, cs));
	}

	public byte[] compress(int clevel, int doshuffle, int[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.INT_FIELD_SIZE, src.length, true);
		cs.m_in.write(0, src, 0, cs.items);
		return cs.m_out.getByteArray(0, itemsCompressed(clevel, doshuffle, cs));
	}

	public byte[] compress(int clevel, int doshuffle, long[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.LONG_FIELD_SIZE, src.length, true);
		cs.m_in.write(0, src, 0, cs.items);
		return cs.m_out.getByteArray(0, itemsCompressed(clevel, doshuffle, cs));
	}

	public byte[] compress(int clevel, int doshuffle, short[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.SHORT_FIELD_SIZE, src.length, true);
		cs.m_in.write(0, src, 0, cs.items);
		return cs.m_out.getByteArray(0, itemsCompressed(clevel, doshuffle, cs));
	}

	public byte[] decompressToByteArray(byte[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.BYTE_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs);
		return cs.m_out.getByteArray(0, nitems);
	}

	public byte[] decompressToByteArray(byte[] src, long destSize) {
		CStruct cs = new CStruct(PrimitiveSizes.BYTE_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs, destSize);
		return cs.m_out.getByteArray(0, nitems);
	}

	public short[] decompressToShortArray(byte[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.SHORT_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs);
		return cs.m_out.getShortArray(0, nitems);
	}

	public short[] decompressToShortArray(byte[] src, long destSize) {
		CStruct cs = new CStruct(PrimitiveSizes.SHORT_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs, destSize);
		return cs.m_out.getShortArray(0, nitems);
	}

	public float[] decompressToFloatArray(byte[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.FLOAT_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs);
		return cs.m_out.getFloatArray(0, nitems);
	}

	public float[] decompressToFloatArray(byte[] src, long destSize) {
		CStruct cs = new CStruct(PrimitiveSizes.FLOAT_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs, destSize);
		return cs.m_out.getFloatArray(0, nitems);
	}

	public double[] decompressToDoubleArray(byte[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.DOUBLE_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs);
		return cs.m_out.getDoubleArray(0, nitems);
	}

	public double[] decompressToDoubleArray(byte[] src, long destSize) {
		CStruct cs = new CStruct(PrimitiveSizes.DOUBLE_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs, destSize);
		return cs.m_out.getDoubleArray(0, nitems);
	}

	public char[] decompressToCharArray(byte[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.CHAR_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs);
		return cs.m_out.getCharArray(0, nitems);
	}

	public char[] decompressToCharArray(byte[] src, long destSize) {
		CStruct cs = new CStruct(PrimitiveSizes.CHAR_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs, destSize);
		return cs.m_out.getCharArray(0, nitems);
	}

	public int[] decompressToIntArray(byte[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.INT_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs);
		return cs.m_out.getIntArray(0, nitems);
	}

	public int[] decompressToIntArray(byte[] src, long destSize) {
		CStruct cs = new CStruct(PrimitiveSizes.INT_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs, destSize);
		return cs.m_out.getIntArray(0, nitems);
	}

	public long[] decompressToLongArray(byte[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.LONG_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs);
		return cs.m_out.getLongArray(0, nitems);
	}

	public long[] decompressToLongArray(byte[] src, long destSize) {
		CStruct cs = new CStruct(PrimitiveSizes.LONG_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs, destSize);
		return cs.m_out.getLongArray(0, nitems);
	}

	public static int compress(int compressionLevel, int shuffleType, int typeSize, Buffer src, long srcLength,
			Buffer dest, long destLength) {
		if (srcLength > (Integer.MAX_VALUE - BloscWrapper.OVERHEAD)) {
			throw new IllegalArgumentException("Source array is too large");
		}
		if (destLength < (srcLength + BloscWrapper.OVERHEAD)) {
			throw new IllegalArgumentException("Dest array is not large enough.");
		}
		src.position(0);
		dest.position(0);
		int w = IBloscDll.blosc_compress(compressionLevel, shuffleType, new NativeLong(typeSize),
				new NativeLong(srcLength), src, dest, new NativeLong(destLength));
		if (w == 0) {
			throw new RuntimeException("Compressed size larger then dest length");
		}
		if (w == -1) {
			throw new RuntimeException("Error compressing data: src: " + src + ", dst: " + dest);
		}
		return w;
	}

	public static int decompress(Buffer src, Buffer dest, long destSize) {
		src.position(0);
		dest.position(0);
		return IBloscDll.blosc_decompress(src, dest, new NativeLong(destSize));
	}

	public static int compressCtx(int compressionLevel, int shuffleType, int typeSize, ByteBuffer src, long srcLength,
			ByteBuffer dest, long destLength, String compressorName, int blockSize, int numThreads) {
		if (srcLength > (Integer.MAX_VALUE - BloscWrapper.OVERHEAD)) {
			throw new IllegalArgumentException("Source array is too large");
		}
		if (destLength < (srcLength + BloscWrapper.OVERHEAD)) {
			throw new IllegalArgumentException("Dest array is not large enough.");
		}
		src.position(0);
		dest.position(0);
		src.order(ByteOrder.nativeOrder());
		dest.order(ByteOrder.nativeOrder());
		int w = IBloscDll.blosc_compress_ctx(compressionLevel, shuffleType, new NativeLong(typeSize),
				new NativeLong(srcLength), src, dest, new NativeLong(destLength), compressorName,
				new NativeLong(blockSize), numThreads);
		if (w == 0) {
			throw new RuntimeException("Compressed size larger than dest length");
		}
		if (w == -1) {
			throw new RuntimeException("Error compressing data: src: " + src + ", dst: " + dest);
		}
		return w;
	}

	public static int decompressCtx(Buffer src, Buffer dest, long destSize, int numThreads) {
		src.position(0);
		dest.position(0);
		return IBloscDll.blosc_decompress_ctx(src, dest, new NativeLong(destSize), numThreads);
	}

}
