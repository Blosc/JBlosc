package com.blosc_jna;
import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

public interface IBloscDll extends Library  {

	void blosc_init();
	int blosc_get_nthreads();
	int blosc_set_nthreads(int nthreads);
	String blosc_get_compressor();
	int blosc_set_compressor(String compname);
	int blosc_compcode_to_compname(int compcode, PointerByReference compname);
	int blosc_compname_to_compcode(String compname);
	String blosc_list_compressors();
	String blosc_get_version_string();
	int blosc_compress (int clevel, int doshuffle, NativeLong typesize, 
			NativeLong nbytes, Pointer src, Pointer dest, NativeLong destsize);
	int blosc_compress_ctx(int clevel, int doshuffle, NativeLong typesize, NativeLong nbytes, 
			Pointer src, Pointer dest, NativeLong destsize, String compressor, NativeLong blocksize, 
			int numinternalthreads);
	int blosc_decompress(Pointer src, Pointer dest, NativeLong destsize);
	int blosc_decompress_ctx(Pointer src, Pointer dest, NativeLong destsize, int numinternalthreads);
	int blosc_getitem(Pointer src, int start, int nitems, Pointer dest);
	int blosc_get_complib_info(String compname, PointerByReference complib, PointerByReference version);
	int blosc_free_resources();
	void blosc_cbuffer_sizes(Pointer cbuffer, NativeLongByReference nbytes, NativeLongByReference cbytes, NativeLongByReference blocksize);
	void blosc_cbuffer_metainfo(Pointer cbuffer, NativeLongByReference typesize, IntByReference flags);
	void blosc_cbuffer_versions(Pointer cbuffer, IntByReference version, IntByReference versionlz);
	Pointer blosc_cbuffer_complib(Pointer cbuffer);
	int blosc_get_blocksize();
	void blosc_destroy();
	
}
