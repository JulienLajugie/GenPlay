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
package edu.yu.einstein.genplay.dataStructure.gene;

import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.AbstractChromosomeWindow;
import edu.yu.einstein.genplay.util.HashCodeUtil;

/**
 * This class provides a skeletal implementation of the {@link Gene} interface
 * to minimize the effort required to implement this interface.
 * @author Julien Lajugie
 */
public abstract class AbstractGene extends AbstractChromosomeWindow implements Gene {

	/** Generated serial ID */
	private static final long serialVersionUID = -3877949005491405885L;


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
		Gene other = (Gene) obj;
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		if (getStrand() == null) {
			if (other.getStrand() != null) {
				return false;
			}
		} else if (!getStrand().equals(other.getStrand())) {
			return false;
		}
		if (getStart() != other.getStart()) {
			return false;
		}
		if (getStop() != other.getStop()) {
			return false;
		}
		if (getScore() != other.getScore()) {
			return false;
		}
		if (getUTR5Bound() != other.getUTR5Bound()) {
			return false;
		}
		if (getUTR3Bound() != other.getUTR3Bound()) {
			return false;
		}
		if (getExons() == null) {
			if (other.getExons() != null) {
				return false;
			}
		} else if (!getExons().equals(other.getExons())) {
			return false;
		}
		return true;
	}


	@Override
	public int hashCode() {
		int hashCode = HashCodeUtil.SEED;
		hashCode = HashCodeUtil.hash(hashCode, getName());
		hashCode = HashCodeUtil.hash(hashCode, getStrand());
		hashCode = HashCodeUtil.hash(hashCode, getStart());
		hashCode = HashCodeUtil.hash(hashCode, getStop());
		hashCode = HashCodeUtil.hash(hashCode, getScore());
		hashCode = HashCodeUtil.hash(hashCode, getUTR5Bound());
		hashCode = HashCodeUtil.hash(hashCode, getUTR3Bound());
		hashCode = HashCodeUtil.hash(hashCode, getExons());
		return hashCode;
	}
}
