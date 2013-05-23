/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow;

import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.AbstractChromosomeWindow;
import edu.yu.einstein.genplay.util.HashCodeUtil;
import edu.yu.einstein.genplay.util.NumberFormats;


/**
 * This class provides a skeletal implementation of the {@link ScoredChromosomeWindow} interface
 * to minimize the effort required to implement this interface.
 * @author Julien Lajugie
 */
public abstract class AbstractScoredChromosomeWindow extends AbstractChromosomeWindow implements ScoredChromosomeWindow {

	/** Generated serial ID */
	private static final long serialVersionUID = -1041340326045619128L;


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ScoredChromosomeWindow other = (ScoredChromosomeWindow) obj;
		if (getStart() != other.getStart()) {
			return false;
		}
		if (getStop() != other.getStop()) {
			return false;
		}
		if (getScore() != other.getScore()) {
			return false;
		}
		return true;
	}


	@Override
	public int hashCode() {
		int hashCode = HashCodeUtil.SEED;
		hashCode = HashCodeUtil.hash(hashCode, getStart());
		hashCode = HashCodeUtil.hash(hashCode, getStop());
		hashCode = HashCodeUtil.hash(hashCode, getScore());
		return hashCode;
	}


	@Override
	public String toString() {
		String startStr = NumberFormats.getPositionFormat().format(getStart());
		String stopStr = NumberFormats.getPositionFormat().format(getStop());
		return startStr + "-" + stopStr + " : " + getScore();
	}
}
