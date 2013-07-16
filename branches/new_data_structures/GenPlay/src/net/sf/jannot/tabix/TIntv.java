package net.sf.jannot.tabix;

/**
 * This class is part of the Tabix API written by Heng Li.
 * hengli@broadinstitute.org
 * 
 * Here what has been modified:
 * - attributes names more explicit
 * - javadoc added
 * - punctuation added
 * - getters & setters added (no more direct access to attributes)
 * - few processing improvements
 * 
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TIntv {
	
	private int tid;	// chromosome index
	private int beg;	// start offset
	private int end;	// stop offset
	private int bin;	// bin size
	
	
	/**
	 * Decrements beg by 1
	 */
	public void decrementBeg() {
		--beg;
	}
	
	
	/**
	 * Increments beg by 1
	 */
	public void incrementBeg() {
		beg++;
	}
	
	
	/**
	 * Increments beg by 1
	 */
	public void incrementEnd() {
		++end;
	}
	
	
	/**
	 * @return the tid
	 */
	public int getTid() {
		return tid;
	}
	
	
	/**
	 * @return the beg
	 */
	public int getBeg() {
		return beg;
	}
	
	
	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}
	
	
	/**
	 * @return the bin
	 */
	public int getBin() {
		return bin;
	}
	
	
	/**
	 * @param tid the tid to set
	 */
	public void setTid(int tid) {
		this.tid = tid;
	}
	
	
	/**
	 * @param beg the beg to set
	 */
	public void setBeg(int beg) {
		this.beg = beg;
	}
	
	
	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	
	
	/**
	 * @param bin the bin to set
	 */
	public void setBin(int bin) {
		this.bin = bin;
	}
	
};
