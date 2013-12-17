package edu.yu.einstein.genplay.core.multiGenome.tabixAPI;

import java.util.HashMap;

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
public class TIndex {
	
	private HashMap<Integer, TPair64[]> b; // binning index
	private long[] l; // linear index
	

	/**
	 * @return the b
	 */
	public HashMap<Integer, TPair64[]> getB() {
		return b;
	}


	/**
	 * @return the l
	 */
	public long[] getL() {
		return l;
	}


	/**
	 * @param b the b to set
	 */
	public void setB(HashMap<Integer, TPair64[]> b) {
		this.b = b;
	}


	/**
	 * @param l the l to set
	 */
	public void setL(long[] l) {
		this.l = l;
	}


	/**
	 * @return a description of the class
	 */
	protected String getDescription () {
		String info = "";

		if (b != null) {
			if (b.size() > 0) {
				int cpt = 0;
				for (Integer key: b.keySet()) {
					info += "b[" + cpt + "]: " + key + " -> ";
					TPair64[] value = b.get(key);
					for (int i = 0; i < value.length; i++) {
						info += i + ": " + value[i].getDescription() + "; ";
					}
					info += "\n";
					cpt++;
				}
			} else {
				info += "b is empty\n";
			}
		} else {
			info += "b == null\n";
		}

		if (l != null) {
			if (l.length > 0) {
				for (int i = 0; i < l.length; i++) {
					info += "l[" + i + "]: " + l[i] + "\n";
				}
			} else {
				info += "l is empty\n";
			}
		} else {
			info += "l == null\n";
		}

		return info;
	}
};
