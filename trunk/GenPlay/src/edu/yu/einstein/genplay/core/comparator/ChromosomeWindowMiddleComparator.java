/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.comparator;

import java.util.Comparator;

import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.SimpleGenomeWindow;

/**
 * Comparator between  the middle position of 2 {@link ChromosomeWindow}.
 * @author Julien Lajugie
 */
public class ChromosomeWindowMiddleComparator implements Comparator<ChromosomeWindow> {

	/**
	 * @return -1 if the middle position of the first specified {@link SimpleGenomeWindow} is smaller than the middle position of the second one <br>
	 * 0 if the middle positions are equals <br>
	 * 1 the middle position of the first specified {@link SimpleGenomeWindow} is greater than the middle position of the second one
	 */
	@Override
	public int compare(ChromosomeWindow chromosomeWindow1, ChromosomeWindow chromosomeWindow2) {
		if (chromosomeWindow1.getMiddlePosition() < chromosomeWindow2.getMiddlePosition()) {
			return -1;
		} else if (chromosomeWindow1.getMiddlePosition() == chromosomeWindow2.getMiddlePosition()) {
			return 0;
		} else {
			return 1;
		}
	}
}
