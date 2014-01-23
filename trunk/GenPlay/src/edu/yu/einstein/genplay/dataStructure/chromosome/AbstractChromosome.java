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
package edu.yu.einstein.genplay.dataStructure.chromosome;

import edu.yu.einstein.genplay.util.HashCodeUtil;


/**
 * This class provides a skeletal implementation of the {@link Chromosome} interface
 * to minimize the effort required to implement this interface.
 * @author Julien Lajugie
 */
public abstract class AbstractChromosome implements Chromosome {

	/** Generated serial ID */
	private static final long serialVersionUID = -1761826757704422452L;


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
		Chromosome other = (Chromosome) obj;
		if (getLength() != other.getLength()) {
			return false;
		}
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}


	@Override
	public int hashCode(){
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, getName());
		result = HashCodeUtil.hash(result, getLength());
		return result;
	}


	@Override
	public String toString() {
		return getName();
	}
}
