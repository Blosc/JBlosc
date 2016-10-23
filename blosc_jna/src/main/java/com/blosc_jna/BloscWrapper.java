package com.blosc_jna;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;

class CStruct {
	public int fieldSize;
	public int items;
	public long isize;
	public long size;
	public Memory m_in;
	public Memory m_out;
	
	private void inAssignments(int fieldSize, int items) {
		this.fieldSize = fieldSize;
		this.items = items;
		this.isize = items * fieldSize;
		m_in = new Memory(isize);		
	}
	
	public CStruct (int fieldSize, int items, boolean compress) {
		inAssignments(fieldSize, items);
		if (compress) {
			m_out = new Memory(isize*2);
		}
	}	
}

public class BloscWrapper {
	IBloscDll iBloscDll;
	
	public BloscWrapper() {
		iBloscDll = (IBloscDll)Native.loadLibrary("blosc", IBloscDll.class);
	}
	
	private int itemsCompressed (int clevel, int doshuffle, CStruct cs) {
		int size = iBloscDll.blosc_compress(clevel, doshuffle, new NativeLong(cs.fieldSize), 
				new NativeLong(cs.isize), cs.m_in, cs.m_out, new NativeLong(cs.isize*2));
		cs.size=size;
		return size;		
	}
	
	private int itemsDecompressed(CStruct cs) {
		BufferSizes bs = cbufferSizes(cs.m_in.getByteArray(0, (int)cs.m_in.size()));
		cs.m_out = new Memory(bs.getNbytes());
		int size = iBloscDll.blosc_decompress(cs.m_in, cs.m_out, new NativeLong(bs.getNbytes()));
		return size/cs.fieldSize;		
	}
	
	public void init() {
		iBloscDll.blosc_init();
	}

	public void destroy() {
		iBloscDll.blosc_destroy();
	}

	public void setNumThreads(int nthreads) {
		iBloscDll.blosc_set_nthreads(nthreads);
	}
	
	public String listCompressors() {
		return iBloscDll.blosc_list_compressors();
	}

	public void setCompressor(String compname) {
		iBloscDll.blosc_set_compressor(compname);
	}
	
	public BufferSizes cbufferSizes(byte[] cbuffer) {
		NativeLongByReference nbytes = new NativeLongByReference();
		NativeLongByReference cbytes = new NativeLongByReference();
		NativeLongByReference blocksize = new NativeLongByReference();
		Memory m_in = new Memory(cbuffer.length);
		m_in.write(0, cbuffer, 0, cbuffer.length);
		iBloscDll.blosc_cbuffer_sizes(m_in, nbytes, cbytes, blocksize);
		BufferSizes bs = new BufferSizes(nbytes.getValue().longValue(), cbytes.getValue().longValue(),blocksize.getValue().longValue());
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
	
	public float[] decompressToFloatArray(byte[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.FLOAT_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs);
		return cs.m_out.getFloatArray(0, nitems);
	}
		
	public double[] decompressToDoubleArray(byte[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.DOUBLE_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs);		
		return cs.m_out.getDoubleArray(0, nitems);
	}

	public char[] decompressToCharArray(byte[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.CHAR_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs);		
		return cs.m_out.getCharArray(0, nitems);
	}

	public int[] decompressToIntArray(byte[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.INT_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs);
		return cs.m_out.getIntArray(0, nitems);
	}

	public long[] decompressToLongArray(byte[] src) {
		CStruct cs = new CStruct(PrimitiveSizes.LONG_FIELD_SIZE, src.length, false);
		cs.m_in.write(0, src, 0, src.length);
		int nitems = itemsDecompressed(cs);
		return cs.m_out.getLongArray(0, nitems);
	}
	
}

