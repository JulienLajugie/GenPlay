package edu.yu.einstein.genplay.core.multiGenome.tabixAPI;

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
public class TPair64 implements Comparable<TPair64> {
	
	private long u;
	private long v;
	
	
	/**
	 * Constructor of {@link TPair64}
	 * @param u u
	 * @param v v
	 */
	public TPair64(final long u, final long v) {
		this.u = u;
		this.v = v;
	}
	
	
	/**
	 * Constructor of {@link TPair64}
	 * @param p pair 64
	 */
	public TPair64(final TPair64 p) {
		u = p.u;
		v = p.v;
	}
	
	
	@Override
	public int compareTo(final TPair64 p) {
		return u == p.u? 0 : ((u < p.u) ^ (u < 0) ^ (p.u < 0))? -1 : 1; // unsigned 64-bit comparison
	}
	
	
	/**
	 * @return the u
	 */
	public long getU() {
		return u;
	}


	/**
	 * @return the v
	 */
	public long getV() {
		return v;
	}


	/**
	 * @param u the u to set
	 */
	public void setU(long u) {
		this.u = u;
	}


	/**
	 * @param v the v to set
	 */
	public void setV(long v) {
		this.v = v;
	}


	/**
	 * @return a description of the class
	 */
	protected String getDescription () {
		return "u: " + u + "; v: " + v;
	}
	
}