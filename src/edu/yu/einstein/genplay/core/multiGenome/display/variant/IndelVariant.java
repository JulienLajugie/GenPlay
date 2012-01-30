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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.display.MGVariantListForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class IndelVariant implements Serializable, VariantInterface {

	/** Generated serial version ID */
	private static final long serialVersionUID = -2068590198125427396L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private MGVariantListForDisplay 	variantListForDisplay;
	private int 						referenceGenomePosition;
	private int 						length;
	private float 						score;
	private int 						phasedWithPos;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(variantListForDisplay);
		out.writeInt(referenceGenomePosition);
		out.writeInt(length);
		out.writeFloat(score);
		out.writeInt(phasedWithPos);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		variantListForDisplay = (MGVariantListForDisplay) in.readObject();
		referenceGenomePosition = in.readInt();
		length = in.readInt();
		score = in.readFloat();
		phasedWithPos = in.readInt();
	}


	/**
	 * Constructor of {@link IndelVariant}
	 * @param variantListForDisplay 
	 * @param referenceGenomePosition
	 * @param length
	 * @param score
	 * @param phasedWithPos
	 */
	public IndelVariant(MGVariantListForDisplay variantListForDisplay, int referenceGenomePosition, int length, float score, int phasedWithPos) {
		this.variantListForDisplay = variantListForDisplay;
		this.referenceGenomePosition = referenceGenomePosition;
		this.length = length;
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
		return length;
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
		if (length > 0) {
			return VariantType.INSERTION;
		} else if (length < 0) {
			return VariantType.DELETION;
		}
		return null;
	}


	@Override
	public void show() {
		String info = "[P:" + referenceGenomePosition + "; ";
		info += "T: " + getType() + "; ";
		info += "L: " + length + "; ";
		info += "St: " + getStart() + "; ";
		info += "Sp: " + getStop() + "; ";
		info += "P': " + phasedWithPos + "]";
		System.out.println(info);
	}


	@Override
	public int getStart() {
		return ShiftCompute.computeShiftForReferenceGenome(variantListForDisplay.getChromosome(), referenceGenomePosition) + 1;
	}


	@Override
	public MGPosition getFullVariantInformation() {
		return variantListForDisplay.getFullVariantInformation(this);
	}


	@Override
	public int getStop() {
		if (getType() == VariantType.INSERTION) {
			return ShiftCompute.computeShiftForReferenceGenome(variantListForDisplay.getChromosome(), referenceGenomePosition + 1) - 1;
		} else {
			return ShiftCompute.computeShiftForReferenceGenome(variantListForDisplay.getChromosome(), referenceGenomePosition - length);
		}
	}

	
	@Override
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		
		if (this.hashCode() != obj.hashCode()) {
			return false;
		}
		
		// object must be Test at this point
		IndelVariant test = (IndelVariant)obj;
		return referenceGenomePosition == test.getReferenceGenomePosition() &&
		length == test.getLength() &&
		score == test.getScore() &&
		phasedWithPos == test.getScore();
	}

}
