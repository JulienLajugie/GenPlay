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
package edu.yu.einstein.genplay.core.multiGenome.display.variant;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.display.MGVariantListForDisplay;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MixVariant implements VariantInterface {
	
	private final int 	start;
	private final int 	stop;
	
	
	/**
	 * Constructor of {@link MixVariant}
	 * @param start 
	 * @param stop
	 */
	public MixVariant(int start, int stop) {
		this.start = start;
		this.stop = stop;
	}

	
	@Override
	public MGVariantListForDisplay getVariantListForDisplay() {
		return null;
	}
	
	
	@Override
	public int getReferenceGenomePosition() {
		return -1;
	}


	@Override
	public int getLength() {
		return stop - start;
	}


	@Override
	public float getScore() {
		return 100;
	}


	@Override
	public int phasedWithPos() {
		return -1;
	}


	@Override
	public VariantType getType() {
		return VariantType.MIX;
	}
	
	
	@Override
	public void show() {
		String info = "T: " + getType() + "; ";
		info += "St: " + start + "; ";
		info += "Sp': " + stop + "]";
		System.out.println(info);
	}


	@Override
	public int getStart() {
		return start;
	}


	@Override
	public int getStop() {
		return stop;
	}
	
	
	@Override
	public MGPosition getFullVariantInformation() {
		return null;
	}
}
