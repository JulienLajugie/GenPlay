package edu.yu.einstein.genplay.core.multiGenome.tabixAPI;


class TPair64 implements Comparable<TPair64> {
	
	long u, v;
	
	
	public TPair64(final long _u, final long _v) {
		u = _u;
		v = _v;
	}
	
	
	public TPair64(final TPair64 p) {
		u = p.u;
		v = p.v;
	}
	
	
	@Override
	public int compareTo(final TPair64 p) {
		return u == p.u? 0 : ((u < p.u) ^ (u < 0) ^ (p.u < 0))? -1 : 1; // unsigned 64-bit comparison
	}
	
}