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
package edu.yu.einstein.genplay.core.multiGenome.operation.BED;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.CoordinateSystemType;


/**
 * This class helps the processing of a track for a specific allele.
 * It makes calculation for synchronisation and helps to handle the current information.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class AlleleSettingsBed {

	protected final AlleleType allele;						// The allele of the helper
	protected final CoordinateSystemType coordinateSystem;	// The coordinate system to convert the position.

	protected int	charIndex;			// The index of the genotype matching the allele.
	protected int	currentOffset;		// The current offset (for synchronization).
	protected int	currentLength;		// The length of the current variation.
	protected int	currentStart;		// The start position of the current variation.
	protected int	currentStop;;		// The stop position of the current variation.
	protected int	currentAltIndex;	// The index of the alternative among the other alternatives of the line for the current variation.


	/**
	 * Constructor of {@link AlleleSettingsBed}
	 * @param path
	 * @param allele
	 */
	protected AlleleSettingsBed (AlleleType allele, CoordinateSystemType coordinateSystem) {
		this.allele = allele;
		this.coordinateSystem = coordinateSystem;

		// initialize parameters
		currentOffset = 0;
		if (allele.equals(AlleleType.ALLELE01)) {
			charIndex = 0;
		} else if (allele.equals(AlleleType.ALLELE02)) {
			charIndex = 2;
		}
	}


	/**
	 * @param offset the offset to set
	 */
	public void addOffset(int offset) {
		currentOffset += offset;
	}


	/**
	 * Make the last changes on position after they had been updated.
	 */
	public void finalizePosition () {
		if (coordinateSystem == CoordinateSystemType.METAGENOME) {
			if (currentLength != 0) {
				currentStart++;
			}
		}
	}


	/**
	 * @return the allele
	 */
	public AlleleType getAllele() {
		return allele;
	}


	/**
	 * @return the charIndex
	 */
	public int getCharIndex() {
		return charIndex;
	}


	/**
	 * @return the coordinateSystem
	 */
	public CoordinateSystemType getCoordinateSystem() {
		return coordinateSystem;
	}


	/**
	 * @return the currentAltIndex
	 */
	public int getCurrentAltIndex() {
		return currentAltIndex;
	}


	/**
	 * @return the currentLength
	 */
	public int getCurrentLength() {
		return currentLength;
	}


	/**
	 * @return the current and usable start position
	 */
	public int getCurrentStart () {
		if (coordinateSystem == CoordinateSystemType.METAGENOME) {
			return getCurrentStartForMetaGenome();
		} else if (coordinateSystem == CoordinateSystemType.REFERENCE) {
			return getCurrentStartForReferenceGenome();
		} else if (coordinateSystem == CoordinateSystemType.CURRENT_GENOME) {
			return getCurrentStartForCurrentGenome();
		}
		return -1;
	}


	/**
	 * @return the current start for writing purpose (BED rules) and for the current genome
	 */
	private int getCurrentStartForCurrentGenome () {
		if (isAlternative() && (currentLength > 0)) {
			return currentStart + 1;
		}
		return currentStart;
	}


	/**
	 * @return the current start for writing purpose (BED rules) and for the meta genome
	 */
	private int getCurrentStartForMetaGenome () {
		if (currentLength == 0) {
			return currentStart;
		}
		return currentStart + 1;
	}


	/**
	 * @return the current start for writing purpose (BED rules) and for the reference genome
	 */
	private int getCurrentStartForReferenceGenome () {
		if (currentLength < 0) {
			return currentStart + 1;
		}
		return currentStart;
	}


	/**
	 * @return the current and usable stop position
	 */
	public int getCurrentStop () {
		return currentStop;
	}


	/**
	 * @return the displayable current stop
	 */
	public int getDisplayableCurrentStop () {
		if (coordinateSystem == CoordinateSystemType.METAGENOME) {
			return getDisplayableCurrentStopForMetaGenome();
		} else if (coordinateSystem == CoordinateSystemType.REFERENCE) {
			return getDisplayableCurrentStopForReferenceGenome();
		} else if (coordinateSystem == CoordinateSystemType.CURRENT_GENOME) {
			return getDisplayableCurrentStopForCurrentGenome();
		}
		return -1;
	}


	/**
	 * @return the current displayable stop for the current genome
	 */
	private int getDisplayableCurrentStopForCurrentGenome () {
		if (currentLength > 0) {
			return currentStop - 1;
		}
		return currentStop;
	}


	/**
	 * @return the current displayable stop for the meta genome
	 */
	private int getDisplayableCurrentStopForMetaGenome () {
		return currentStop;
	}


	/**
	 * @return the current displayable stop for the reference genome
	 */
	private int getDisplayableCurrentStopForReferenceGenome () {
		if (currentLength < 0) {
			return currentStop - 1;
		}
		return currentStop;
	}


	/**
	 * @return the raw currentStart
	 */
	public int getNativeCurrentStart() {
		return currentStart;
	}


	/**
	 * @return the raw currentStop
	 */
	public int getNativeCurrentStop() {
		return currentStop;
	}


	/**
	 * @return the offset
	 */
	public int getOffset() {
		return currentOffset;
	}


	/**
	 * Initializes the current information about:
	 * - start and stop position
	 * - length
	 * - alternative index
	 * @param lengths		lengths of variations in the line
	 * @param currentLine	the current line
	 * @param altIndex		the index of the alternative
	 */
	public void initializeCurrentInformation (int[] lengths, VCFLine currentLine, int altIndex) {
		if (coordinateSystem == CoordinateSystemType.METAGENOME) {
			initializeCurrentInformationForMetaGenome(lengths, currentLine, altIndex);
		} else if (coordinateSystem == CoordinateSystemType.REFERENCE) {
			initializeCurrentInformationForReferenceGenome(lengths, currentLine, altIndex);
		} else if (coordinateSystem == CoordinateSystemType.CURRENT_GENOME) {
			initializeCurrentInformationForGenome(lengths, currentLine, altIndex);
		}
	}


	/**
	 * Initializes the current information about:
	 * - start and stop position (on the current genome)
	 * - length
	 * - alternative index
	 * @param lengths		lengths of variations in the line
	 * @param currentLine	the current line
	 * @param altIndex		the index of the alternative
	 */
	protected void initializeCurrentInformationForGenome (int[] lengths, VCFLine currentLine, int altIndex) {
		currentAltIndex = altIndex;
		currentStart = currentLine.getReferencePosition() + currentOffset;
		currentLength = 0;
		if (currentAltIndex >= 0) {
			int length = lengths[currentAltIndex];
			if (length > 0) {
				currentLength = length;
			} else {
				currentOffset += length;
			}
		}
		currentStop = (currentStart + currentLength) + 1;
	}


	/**
	 * Initializes the current information about:
	 * - start and stop position (on the meta genome)
	 * - length
	 * - alternative index
	 * @param chromosome 	the current chromosome
	 * @param lengths		lengths of variations in the line
	 * @param currentLine	the current line
	 * @param altIndex		the index of the alternative
	 */
	protected void initializeCurrentInformationForMetaGenome (int[] lengths, VCFLine currentLine, int altIndex) {
		initializeCurrentInformationForReferenceGenome(lengths, currentLine, altIndex);
	}


	/**
	 * Initializes the current information about:
	 * - start and stop position (on the reference genome)
	 * - length
	 * - alternative index
	 * @param lengths		lengths of variations in the line
	 * @param currentLine	the current line
	 * @param altIndex		the index of the alternative
	 */
	protected void initializeCurrentInformationForReferenceGenome (int[] lengths, VCFLine currentLine, int altIndex) {
		currentAltIndex = altIndex;
		currentStart = currentLine.getReferencePosition();
		currentLength = 0;
		int length = currentLength;
		if (currentAltIndex >= 0) {
			currentLength = lengths[currentAltIndex];
			if (currentLength < 0) {
				length = Math.abs(currentLength);
			}
		}
		currentStop = (currentStart + length) + 1;
	}


	/**
	 * @return true if the current information refers to an alternative, false otherwise
	 */
	public boolean isAlternative () {
		if (currentAltIndex > -1) {
			return true;
		}
		return false;
	}


	/**
	 * @return true if the current information refers to known variation (reference/alternatives), false otherwise (if '.')
	 */
	public boolean isKnown () {
		if (currentAltIndex > -2) {
			return true;
		}
		return false;
	}


	/**
	 * @return true if the current information refers to the reference, false otherwise
	 */
	public boolean isReference () {
		if (currentAltIndex == -1) {
			return true;
		}
		return false;
	}


	/**
	 * Updates the current information using information from the other allele.
	 * e.g.: with a 0/1 genotype, information in the 0 allele has to be updated with information from the 1 allele
	 * @param allele the other allele
	 * @param chromosome the current chromosome
	 */
	public void updateCurrentInformation (AlleleSettingsBed allele, Chromosome chromosome) {
		if ((currentAltIndex < 0) && allele.isAlternative()) {
			currentLength = allele.getCurrentLength();
			if (currentLength < 0) {
				currentStop += Math.abs(currentLength);
			}
		}
		if (coordinateSystem == CoordinateSystemType.METAGENOME) {
			currentStart =  ShiftCompute.getPosition(FormattedMultiGenomeName.REFERENCE_GENOME_NAME, this.allele, currentStart, chromosome, FormattedMultiGenomeName.META_GENOME_NAME);
			currentStop =  ShiftCompute.getPosition(FormattedMultiGenomeName.REFERENCE_GENOME_NAME, this.allele, currentStop, chromosome, FormattedMultiGenomeName.META_GENOME_NAME);
		}
	}
}
