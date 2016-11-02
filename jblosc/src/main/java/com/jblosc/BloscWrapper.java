package com.jblosc;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;

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
			m_out = new Memory(isize * 1024);
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

	/**
	 * In the constructor we try to load the blosc shared library.
	 * For 64 bit JVM it wil try to load blosc.dll or libblosc.so
	 * For 32 bit JVM it will try to load blosc32.dll or libblosc32.so
	 */
	public BloscWrapper() {
		iBloscDll = (IBloscDll) Native.loadLibrary("blosc" + Util.getArchPlatform(), IBloscDll.class);
	}

	/**
	 * Helper method intended to reduce code in the public compress methods
	 * Calls the JNA blosc_compress and establishes the compressed size 
	 * @param clevel
	 * @param doshuffle
	 * @param cs
	 * @return
	 */
	private int itemsCompressed(int clevel, int doshuffle, CStruct cs) {
		int size = iBloscDll.blosc_compress(clevel, doshuffle, new NativeLong(cs.fieldSize), new NativeLong(cs.isize),
				cs.m_in, cs.m_out, new NativeLong(cs.isize * 2));
		cs.size = size;
		return size;
	}

	/**
	 * Helper method intended to reduce code in the publib decompressX mtehods
	 * Calls the JNA blosc_decompress, but before it reads the original decompressed size
	 * from  the header (JNA call to blosc_cbuffer_sizes). It's slower than the
	 * version with the destsize  
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
	 * @param nthreads
	 */
	public void setNumThreads(int nthreads) {
		iBloscDll.blosc_set_nthreads(nthreads);
	}

	/**
	 * call to the JNA blosc_get_nthreads
	 * @return
	 */
	public int getNumThreads() {
		return iBloscDll.blosc_get_nthreads();
	}

	/**
	 * Call the JNA blosc_list_compressors
	 * @return
	 */
	public String listCompressors() {
		return iBloscDll.blosc_list_compressors();
	}

	/**
	 * Call to the JNA blosc_set_compressor
	 * @param compname
	 */
	public void setCompressor(String compname) {
		iBloscDll.blosc_set_compressor(compname);
	}

	/**
	 * Call to the JNA blosc_get_compressor
	 * @return
	 */
	public String getCompressor() {
		return iBloscDll.blosc_get_compressor();
	}

	/**
	 * Call to the JNA blosc_cbuffer_sizes
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
		cs.m_in.write(0, src, 0, cs.items);
		return cs.m_out.getByteArray(0, itemsCompressed(clevel, doshuffle, cs));
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
	
}
