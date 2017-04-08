package com.jblosc;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Class that adds a more Java oriented programming vision to te JNA interface
 * 
 * @author aalted
 *
 */
public class BloscWrapper {

	public static final int OVERHEAD = 16;

	/**
	 * Call to the JNA blosc_init()
	 */
	public void init() {
		IBloscDll.blosc_init();
	}

	/**
	 * Call to the JNA blosc_destroy()
	 */
	public void destroy() {
		IBloscDll.blosc_destroy();
	}

	/**
	 * Call to the JNA blosc_set_nthreads
	 * 
	 * @param nthreads
	 */
	public void setNumThreads(int nthreads) {
		IBloscDll.blosc_set_nthreads(nthreads);
	}

	/**
	 * call to the JNA blosc_get_nthreads
	 * 
	 * @return
	 */
	public int getNumThreads() {
		return IBloscDll.blosc_get_nthreads();
	}

	/**
	 * Call the JNA blosc_list_compressors
	 * 
	 * @return
	 */
	public String listCompressors() {
		return IBloscDll.blosc_list_compressors();
	}

	/**
	 * Call to the JNA blosc_set_compressor
	 * 
	 * @param compname
	 */
	public void setCompressor(String compname) {
		IBloscDll.blosc_set_compressor(compname);
	}

	/**
	 * Call to the JNA blosc_get_compressor
	 * 
	 * @return
	 */
	public String getCompressor() {
		return IBloscDll.blosc_get_compressor();
	}

	/**
	 * Call to the JNA blosc_compname_to_compcode
	 * 
	 * @param compname
	 * @return
	 */
	public int compnameToCompcode(String compname) {
		return IBloscDll.blosc_compname_to_compcode(compname);
	}

	/**
	 * Call to the JNA blosc_compcode_to_compname
	 * 
	 * @param compcode
	 * @return
	 */
	public String compcodeToCompname(int compcode) {
		PointerByReference ptr = new PointerByReference();
		IBloscDll.blosc_compcode_to_compname(compcode, ptr);
		Pointer p = ptr.getValue();
		return p.getString(0);
	}

	/**
	 * Call to the JNA blosc_get_version_string
	 * 
	 * @return
	 */
	public String getVersionString() {
		return IBloscDll.blosc_get_version_string();
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
		int compcode = IBloscDll.blosc_get_complib_info(compname, ptrComplib, ptrVersion);
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
		if (IBloscDll.blosc_free_resources() == -1) {
			throw new RuntimeException();
		}
	}

	/**
	 * Call to the JNA blosc_get_blocksize method
	 * 
	 * @return
	 */
	public int getBlocksize() {
		return IBloscDll.blosc_get_blocksize();
	}

	public BufferSizes cbufferSizes(ByteBuffer cbuffer) {
		NativeLongByReference nbytes = new NativeLongByReference();
		NativeLongByReference cbytes = new NativeLongByReference();
		NativeLongByReference blocksize = new NativeLongByReference();
		IBloscDll.blosc_cbuffer_sizes(cbuffer, nbytes, cbytes, blocksize);
		BufferSizes bs = new BufferSizes(nbytes.getValue().longValue(), cbytes.getValue().longValue(),
				blocksize.getValue().longValue());
		return bs;
	}

	private void checkSizes(long srcLength, long destLength) {
		if (srcLength > (Integer.MAX_VALUE - BloscWrapper.OVERHEAD)) {
			throw new IllegalArgumentException("Source array is too large");
		}
		if (destLength < (srcLength + BloscWrapper.OVERHEAD)) {
			throw new IllegalArgumentException("Dest array is not large enough.");
		}
	}

	private void checkExit(int w) {
		if (w == 0) {
			throw new RuntimeException("Compressed size larger then dest length");
		}
		if (w == -1) {
			throw new RuntimeException("Error compressing data");
		}
	}

	public int compress(int compressionLevel, int shuffleType, int typeSize, ByteBuffer src, long srcLength,
			ByteBuffer dest, long destLength) {
		checkSizes(srcLength, destLength);
		src.position(0);
		dest.position(0);
		src.order(ByteOrder.nativeOrder());
		dest.order(ByteOrder.nativeOrder());
		int w = IBloscDll.blosc_compress(compressionLevel, shuffleType, new NativeLong(typeSize),
				new NativeLong(srcLength), src, dest, new NativeLong(destLength));
		checkExit(w);
		return w;
	}

	public int compress(int compressionLevel, int shuffleType, int typeSize, byte[] src, long srcLength, byte[] dest,
			long destLength) {
		checkSizes(srcLength, destLength);
		int w = IBloscDll.blosc_compress(compressionLevel, shuffleType, new NativeLong(typeSize),
				new NativeLong(srcLength), src, dest, new NativeLong(destLength));
		checkExit(w);
		return w;
	}

	public int decompress(Buffer src, Buffer dest, long destSize) {
		src.position(0);
		dest.position(0);
		return IBloscDll.blosc_decompress(src, dest, new NativeLong(destSize));
	}

	public int decompress(byte[] src, byte[] dest, long destSize) {
		return IBloscDll.blosc_decompress(src, dest, new NativeLong(destSize));
	}

	public int compressCtx(int compressionLevel, int shuffleType, int typeSize, ByteBuffer src, long srcLength,
			ByteBuffer dest, long destLength, String compressorName, int blockSize, int numThreads) {
		src.position(0);
		dest.position(0);
		// src.order(ByteOrder.nativeOrder());
		// dest.order(ByteOrder.nativeOrder());
		int w = IBloscDll.blosc_compress_ctx(compressionLevel, shuffleType, new NativeLong(typeSize),
				new NativeLong(srcLength), src, dest, new NativeLong(destLength), compressorName,
				new NativeLong(blockSize), numThreads);
		checkExit(w);
		return w;
	}

	public int decompressCtx(Buffer src, Buffer dest, long destSize, int numThreads) {
		src.position(0);
		dest.position(0);
		return IBloscDll.blosc_decompress_ctx(src, dest, new NativeLong(destSize), numThreads);
	}

	public void cbufferMetainfo(Buffer cbuffer, NativeLongByReference typesize, IntByReference flags) {
		IBloscDll.blosc_cbuffer_metainfo(cbuffer, typesize, flags);
	}

	public void cbufferVersions(Buffer cbuffer, IntByReference version, IntByReference versionlz) {
		IBloscDll.blosc_cbuffer_versions(cbuffer, version, versionlz);
	}

	public Buffer cbufferComplib(Buffer cbuffer) {
		return IBloscDll.blosc_cbuffer_complib(cbuffer);
	}

}
