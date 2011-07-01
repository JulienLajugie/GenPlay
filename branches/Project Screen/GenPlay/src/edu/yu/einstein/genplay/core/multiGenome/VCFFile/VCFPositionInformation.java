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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.VCFFile;

import java.util.Map;

import edu.yu.einstein.genplay.core.enums.VariantType;


/**
 * This class manages the position information.
 * Those information are the indel type and the offset position.
 * @author Nicolas Fourel
 */
public class VCFPositionInformation {

	private VariantType 		type;						// The indel type
	private Map<String, String> info;					// The genome information (ex: GT:GQ -> X/X:Y)
	private int					length;						// The indel length
	private int					genomePosition;				// The genome position
	private int 				initialReferenceOffset;		// The offset between the genome position and the reference genome position
	private int 				initialMetaGenomeOffset;	// The offset between the genome position and the meta genome position
	private int 				extraOffset;				// Offset when multiple insertions happen at the same reference position


	/**
	 * Constructor of {@link VCFPositionInformation}
	 * @param type		indel type
	 * @param offset	difference between the meta genome and the current genome
	 */
	public VCFPositionInformation (VariantType type, int length, Map<String, String> info) {
		this.type = type;
		this.length = length;
		this.info = info;
		initialReferenceOffset = 0;
		initialMetaGenomeOffset = 0;
		extraOffset = 0;
	}


	/**
	 * @return the type
	 */
	public VariantType getType() {
		return type;
	}


	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}


	/**
	 * @param extraOffset the extraOffset to set
	 */
	public void addExtraOffset(int extraOffset) {
		this.extraOffset += extraOffset;
	}


	/**
	 * @return the extraOffset
	 */
	public int getExtraOffset() {
		return extraOffset;
	}


	/**
	 * @param title header name
	 * @return		the associated value
	 */
	public String getInfoValue (String title) {
		if (info != null) {
			if (info.get(title) != null) {
				return info.get(title);
			}
		}
		return null;
	}


	/**
	 * @return true if VCF information are phased
	 */
	public boolean isPhased () {
		String value = getInfoValue("GT").substring(1, 2);
		if (value.equals("/")) {
			return false;
		} else {
			return true;
		}
	}


	/**
	 * @return the Genome Type information.
	 */
	public int[] getGT () {
		int[] values = new int[2];
		if (type.equals(VariantType.BLANK)) {
			values[0] = 1;
			values[1] = 1;
		} else {
			values[0] = Integer.parseInt(getInfoValue("GT").substring(0, 1));
			values[1] = Integer.parseInt(getInfoValue("GT").substring(2));
		}
		return values;
	}


	/**
	 * @return the Quality information
	 */
	public double getQuality () {
		if (type.equals(VariantType.BLANK)) {
			return 100;
		}
		return Double.parseDouble(getInfoValue("GQ"));
	}

	////////////////////////////////////////////////////Genome

	/**
	 * @param value the new genome position
	 */
	public void setGenomePosition (int value) {
		genomePosition = value;
	}


	/**
	 * @return the genome position
	 */
	public int getGenomePosition() {
		return genomePosition;
	}


	/**
	 * @return the next valid genome position after the event
	 */
	public int getNextGenomePosition () {
		int nextGenomePosition = genomePosition + 1;
		if (type == VariantType.INSERTION) {
			nextGenomePosition += length;
		}
		return nextGenomePosition;
	}


	////////////////////////////////////////////////////// Reference Genome

	/**
	 * @param initialReferenceOffset the initialReferenceOffset to set
	 */
	public void setInitialReferenceOffset(int initialReferenceOffset) {
		this.initialReferenceOffset = initialReferenceOffset;
	}


	/**
	 * @return the initialReferenceOffset
	 */
	public int getInitialReferenceOffset() {
		return initialReferenceOffset;
	}


	/**
	 * @return the nextReferencePositionOffset
	 */
	public int getNextReferencePositionOffset() {
		int nextGenomePosition = getNextGenomePosition();
		int nextReferencePosition = getNextReferenceGenomePosition(nextGenomePosition);
		return nextReferencePosition - nextGenomePosition;
	}


	/**
	 * @return the reference genome position
	 */
	public int getReferenceGenomePosition () {
		int position = genomePosition + initialReferenceOffset;
		return position;
	}


	/**
	 * @return the next valid reference genome position after the event
	 */
	public int getNextReferenceGenomePosition () {
		int position = getNextGenomePosition();
		return getNextReferenceGenomePosition(position);
	}


	/**
	 * @param inputGenomePosition	genome position
	 * @return the reference genome position according to the input position
	 */
	public int getNextReferenceGenomePosition (int inputGenomePosition) {
		int position = getReferenceGenomePosition();
		int difference = inputGenomePosition - genomePosition;
		if (type == VariantType.INSERTION) {
			if (difference > length) {
				position += difference - length;
			} else {
				System.out.println("WARNING: difference < length");
			}
		} else {
			position += difference;
			if (type == VariantType.DELETION) {
				position += length;
			}
		}
		return position;
	}


	////////////////////////////////////////////////////// Meta Genome

	/**
	 * @param initialMetaGenomeOffset the initialMetaGenomeOffset to set
	 */
	public void setInitialMetaGenomeOffset(int initialMetaGenomeOffset) {
		this.initialMetaGenomeOffset = initialMetaGenomeOffset;
	}


	/**
	 * @return the initialMetaGenomeOffset
	 */
	public int getInitialMetaGenomeOffset() {
		return initialMetaGenomeOffset;
	}


	/**
	 * @return the nextMetaGenomePositionOffset
	 */
	public int getNextMetaGenomePositionOffset() {
		int nextGenomePosition = getNextGenomePosition();
		int nextMetaGenomePosition = getNextMetaGenomePosition(nextGenomePosition);
		return nextMetaGenomePosition - nextGenomePosition;
	}


	/**
	 * @return the meta genome position
	 */
	public int getMetaGenomePosition () {
		int position = genomePosition + initialMetaGenomeOffset;
		return position;
	}


	/**
	 * @return the next valid meta genome position after the event
	 */
	public int getNextMetaGenomePosition () {
		int position = getNextGenomePosition();
		return getNextMetaGenomePosition(position);
	}


	/**
	 * @param inputGenomePosition	genome position
	 * @return the meta genome position according to the input position
	 */
	public int getNextMetaGenomePosition (int inputGenomePosition) {
		int position = getMetaGenomePosition() + (inputGenomePosition - genomePosition);
		if (type != VariantType.INSERTION) {
			position += length;
		}
		if (inputGenomePosition > (genomePosition + length)) {
			position += extraOffset;
		}
		return position;
	}


	//////////////////////////////////////////////////////

	public void showData () {
		System.out.println("--------------------------------------------------------------------");
		System.out.println("Type: " + type.toString());
		System.out.println("Length: " + length);
		System.out.println("Offset (E): " + extraOffset);

		System.out.println("-- initial");
		System.out.println("Position (G): " + getGenomePosition());
		System.out.println("Position (MG): " + getMetaGenomePosition());
		System.out.println("Position (REF): " + getReferenceGenomePosition());
		System.out.println("Offset (MG): " + getInitialMetaGenomeOffset());
		System.out.println("Offset (Ref): " + getInitialReferenceOffset());

		System.out.println("-- next");
		System.out.println("Position (G): " + getNextGenomePosition());
		System.out.println("Position (MG): " + getNextMetaGenomePosition());
		System.out.println("Position (REF): " + getNextReferenceGenomePosition());
		System.out.println("Offset (MG): " + getNextMetaGenomePositionOffset());
		System.out.println("Offset (Ref): " + getNextReferencePositionOffset());
	}

}