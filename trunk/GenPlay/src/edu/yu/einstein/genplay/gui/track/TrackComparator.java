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
package edu.yu.einstein.genplay.gui.track;

import java.util.Comparator;

import edu.yu.einstein.genplay.core.comparator.StringComparator;

/**
 * Comparator for {@link Track}.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TrackComparator implements Comparator<Track> {

	@Override
	public int compare(Track o1, Track o2) {
		if ((o1 == null) && (o2 == null)) {
			return 0;
		} else if ((o1 != null) && (o2 == null)) {
			return -1;
		} else if ((o1 == null) && (o2 != null)) {
			return 1;
		} else {
			String s1 = o1.getName();
			String s2 = o2.getName();
			StringComparator stringComparator = new StringComparator();
			return stringComparator.compare(s1, s2);
		}
	}
}
