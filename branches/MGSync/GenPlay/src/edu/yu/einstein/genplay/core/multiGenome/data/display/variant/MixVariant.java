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
package edu.yu.einstein.genplay.core.multiGenome.data.display.variant;

import edu.yu.einstein.genplay.core.enums.VariantType;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MixVariant extends MultiNucleotideVariant {


	/**
	 * Constructor of {@link MixVariant}
	 */
	public MixVariant() {
		super(null, -1);
	}


	/**
	 * Constructor of {@link MixVariant}
	 * @param start start position
	 * @param stop stop position
	 */
	public MixVariant(int start, int stop) {
		super(null, -1, start, stop);
	}


	@Override
	public VariantType getType() {
		return VariantType.INSERTION;
	}


	/**
	 * @return a description of the {@link Variant}
	 */
	@Override
	public String getDescription () {
		String description = super.getDescription();
		description += " TYPE: MIX;";
		return description;
	}
}
