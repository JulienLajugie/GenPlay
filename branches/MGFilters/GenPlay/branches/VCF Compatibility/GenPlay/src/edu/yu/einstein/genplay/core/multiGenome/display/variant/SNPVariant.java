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
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SNPVariant implements VariantInterface {
	
	private final MGVariantListForDisplay 	variantListForDisplay;
	private final int 						referenceGenomePosition;
	private final float 					score;
	private final int 						phasedWithPos;
	
	
	/**
	 * Constructor of {@link SNPVariant}
	 * @param variantListForDisplay 
	 * @param referenceGenomePosition
	 * @param score
	 * @param phasedWithPos
	 */
	public SNPVariant (MGVariantListForDisplay variantListForDisplay, int referenceGenomePosition, float score, int phasedWithPos) {
		this.variantListForDisplay = variantListForDisplay;
		this.referenceGenomePosition = referenceGenomePosition;
		this.score = score;
		this.phasedWithPos = phasedWithPos;
	}
	
	
	@Override
	public MGVariantListForDisplay getVariantListForDisplay() {
		return variantListForDisplay;
	}


	@Override
	public int getReferenceGenomePosition() {
		return referenceGenomePosition;
	}


	@Override
	public int getLength() {
		return 1;
	}


	@Override
	public float getScore() {
		return score;
	}


	@Override
	public int phasedWithPos() {
		return phasedWithPos;
	}


	@Override
	public VariantType getType() {
		return VariantType.SNPS;
	}


	@Override
	public void show() {
		String info = "[P:" + referenceGenomePosition + "; ";
		info += "T: " + VariantType.SNPS + "; ";
		info += "L: 1; ";
		info += "St: " + getStart() + "; ";
		info += "P': " + phasedWithPos + "]";
		System.out.println(info);
	}


	@Override
	public int getStart() {
		return ShiftCompute.computeShiftForReferenceGenome(variantListForDisplay.getChromosome(), referenceGenomePosition);
	}

	
	@Override
	public MGPosition getFullVariantInformation() {
		return variantListForDisplay.getFullVariantInformation(this);
	}
	

	@Override
	public int getStop() {
		//System.err.println("Illegal use of the method \"SNPVariant.getStop\", this method shouldn't be invoked for redundancy purpose");
		//return getStart() + 1;	// if getStop() is called, getStart() has been probably called right before. In case of SNP, just add 1 to the getStart() result to have its stop.
		//return 0;
		return ShiftCompute.computeShiftForReferenceGenome(variantListForDisplay.getChromosome(), referenceGenomePosition + 1);
	}
	
}
