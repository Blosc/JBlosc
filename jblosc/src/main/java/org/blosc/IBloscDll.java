package org.blosc;

import java.nio.Buffer;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

public class IBloscDll {

	static {
		Native.register("blosc" + Util.getArchPlatform());
	}

	public static native void blosc_init();

	public static native int blosc_get_nthreads();

	public static native int blosc_set_nthreads(int nthreads);

	public static native String blosc_get_compressor();

	public static native int blosc_set_compressor(String compname);

	public static native int blosc_compcode_to_compname(int compcode, PointerByReference compname);

	public static native int blosc_compname_to_compcode(String compname);

	public static native String blosc_list_compressors();

	public static native String blosc_get_version_string();

	public static native int blosc_compress(int clevel, int doshuffle, NativeLong typesize, NativeLong nbytes,
			Pointer src, Pointer dest, NativeLong destsize);

	public static native int blosc_compress(int clevel, int doshuffle, NativeLong typesize, NativeLong nbytes,
			Buffer src, Buffer dest, NativeLong destsize);

	public static native int blosc_compress_ctx(int clevel, int doshuffle, NativeLong typesize, NativeLong nbytes,
			Pointer src, Pointer dest, NativeLong destsize, String compressor, NativeLong blocksize,
			int numinternalthreads);

	public static native int blosc_compress_ctx(int clevel, int doshuffle, NativeLong typesize, NativeLong nbytes,
			Buffer src, Buffer dest, NativeLong destsize, String compressor, NativeLong blocksize,
			int numinternalthreads);

	public static native int blosc_decompress(Pointer src, Pointer dest, NativeLong destsize);

	public static native int blosc_decompress(Buffer src, Buffer dest, NativeLong destsize);

	public static native int blosc_decompress_ctx(Pointer src, Pointer dest, NativeLong destsize,
			int numinternalthreads);

	public static native int blosc_decompress_ctx(Buffer src, Buffer dest, NativeLong destsize, int numinternalthreads);

	public static native int blosc_getitem(Pointer src, int start, int nitems, Pointer dest);

	public static native int blosc_get_complib_info(String compname, PointerByReference complib,
			PointerByReference version);

	public static native int blosc_free_resources();

	public static native void blosc_cbuffer_sizes(Buffer cbuffer, NativeLongByReference nbytes,
			NativeLongByReference cbytes, NativeLongByReference blocksize);

	public static native void blosc_cbuffer_metainfo(Buffer cbuffer, NativeLongByReference typesize,
			IntByReference flags);

	public static native void blosc_cbuffer_versions(Buffer cbuffer, IntByReference version, IntByReference versionlz);

	public static native Buffer blosc_cbuffer_complib(Buffer cbuffer);

	public static native int blosc_get_blocksize();

	public static native void blosc_set_blocksize(NativeLong blocksize);

	public static native void blosc_destroy();

}
