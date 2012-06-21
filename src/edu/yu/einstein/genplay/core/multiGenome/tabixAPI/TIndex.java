package edu.yu.einstein.genplay.core.multiGenome.tabixAPI;

import java.util.HashMap;


class TIndex {
	HashMap<Integer, TPair64[]> b; // binning index
	long[] l; // linear index

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
