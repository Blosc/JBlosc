package org.blosc;

public class BufferSizes {
	private long nbytes;
	private long cbytes;
	private long bloksize;

	public BufferSizes(long nbytes, long cbytes, long bloksize) {
		super();
		this.nbytes = nbytes;
		this.cbytes = cbytes;
		this.bloksize = bloksize;
	}

	public long getNbytes() {
		return nbytes;
	}

	public void setNbytes(long nbytes) {
		this.nbytes = nbytes;
	}

	public long getCbytes() {
		return cbytes;
	}

	public void setCbytes(long cbytes) {
		this.cbytes = cbytes;
	}

	public long getBloksize() {
		return bloksize;
	}

	public void setBloksize(long bloksize) {
		this.bloksize = bloksize;
	}

}
