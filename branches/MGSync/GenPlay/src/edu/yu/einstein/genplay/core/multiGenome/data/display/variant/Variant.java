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
import edu.yu.einstein.genplay.core.multiGenome.data.display.content.MGChromosomeContent;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public abstract class Variant {

	protected final MGChromosomeContent chromosomeContent;
	protected final int referencePositionIndex;
	protected int start;


	/**
	 * Constructor of {@link Variant}
	 * @param chromosomeContent the {@link MGChromosomeContent}
	 * @param referencePositionIndex the index position on the reference genome
	 */
	public Variant (MGChromosomeContent chromosomeContent, int referencePositionIndex) {
		this(chromosomeContent, referencePositionIndex, -1);
	}


	/**
	 * Constructor of {@link Variant}
	 * @param chromosomeContent the {@link MGChromosomeContent}
	 * @param referencePositionIndex the index position on the reference genome
	 * @param start start position
	 */
	public Variant (MGChromosomeContent chromosomeContent, int referencePositionIndex, int start) {
		this.chromosomeContent = chromosomeContent;
		this.referencePositionIndex = referencePositionIndex;
		this.start = start;
	}


	/**
	 * @return the chromosomeContent
	 */
	public MGChromosomeContent getChromosomeContent() {
		return chromosomeContent;
	}


	/**
	 * @return the referencePositionIndex
	 */
	public int getReferencePositionIndex() {
		return referencePositionIndex;
	}


	/**
	 * @return the reference genome position
	 */
	public int getReferenceGenomePosition() {
		return chromosomeContent.getPositions().get(referencePositionIndex);
	}


	/**
	 * @return the score of the {@link Variant}
	 */
	public float getScore () {
		return chromosomeContent.getScore(referencePositionIndex);
	}


	/**
	 * @return the start position on the meta genome
	 */
	public int getStart () {
		return start;
	}


	/**
	 * @param start start position to set
	 */
	public void setStart (int start) {
		this.start = start;
	}


	/**
	 * @return the stop position on the meta genome
	 */
	public abstract int getStop ();


	/**
	 * @param stop stop position to set
	 */
	public abstract void setStop (int stop);


	/**
	 * @return the {@link VariantType} of the {@link Variant}
	 */
	public abstract VariantType getType ();


	/**
	 * @return a description of the {@link Variant}
	 */
	public String getDescription () {
		String description = "";
		description += "INDEX: " + referencePositionIndex + "; ";
		description += "REF: " + chromosomeContent.getPositions().get(referencePositionIndex) + "; ";
		description += "START: " + start + ";";
		return description;
	}


	/**
	 * @return the length of the variant
	 */
	public int getLength() {
		return getStop() - getStart();
	}


	public String getVariantSequence() {
		return ".";
	}

}
