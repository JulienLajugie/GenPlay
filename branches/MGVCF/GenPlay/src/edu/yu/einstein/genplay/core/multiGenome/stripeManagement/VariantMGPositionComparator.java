package edu.yu.einstein.genplay.core.multiGenome.stripeManagement;

import java.util.Comparator;

import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;

public class VariantMGPositionComparator implements Comparator<Variant> {

	@Override
	public int compare(Variant o1, Variant o2) {
		int position1 = o1.getMetaGenomePosition();
		int position2 = o2.getMetaGenomePosition();
		if (position1 < position2) {
			return -1;
		} else if (position1 == position2) {
			return 0;
		} else {
			return 1;
		}
	}

}
