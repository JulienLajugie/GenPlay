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
package edu.yu.einstein.genplay.core.multiGenome.utils;
import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.manager.multiGenomeManager.MultiGenomeManager;
import edu.yu.einstein.genplay.core.multiGenome.VCFFile.VCFChromosomeInformation;
import edu.yu.einstein.genplay.core.multiGenome.VCFFile.VCFPositionInformation;


/**
 * This class manages the shifting process in order to get a meta genome position from a genome position.
 * In a multi genome project, every position must be shifted in order to synchronize tracks. 
 * @author Nicolas Fourel
 */
public class ShiftCompute {

	private static VCFChromosomeInformation chromInfo;		// Chromosome information which contains position list
	private static boolean 	found;							// Determines is the new position has been found
	private static int[] 	indexes;						// List of indexed reference position
	private static int 		referencePosition;				// The closest inferior reference position according to the given genome position
	private static int 		newPosition;					// The final shifted position


	/**
	 * Calculates the meta genome position according to the given genome position
	 * @param genome			the raw genome name (containing the genome position)
	 * @param chromosome		the chromosome (containing the genome position)
	 * @param genomePosition	the given genome position
	 * @return					the corresponding meta genome position
	 */
	public static int computeShift (String genome, Chromosome chromosome, int genomePosition) {
		// Parameters initialization
		initParameters(genome, chromosome, genomePosition);

		// Dichotomic scan
		if (indexes.length > 0) {
			dichotomicScan(genomePosition);
			finishShifting(genomePosition);
		}

		// Return the final shifted position
		return newPosition;
	}


	/**
	 * Sets initial parameters.
	 * @param genome			genome raw name
	 * @param chromosome		chromosome
	 * @param genomePosition	the given genome position
	 */
	private static void initParameters (String genome, Chromosome chromosome, int genomePosition) {
		referencePosition = -1;
		newPosition = genomePosition;
		chromInfo = MultiGenomeManager.getInstance().getChromosomeInformation(genome, chromosome);
		indexes = chromInfo.getPositionIndex();
		found = false;
	}


	/**
	 * Performs the dichotomic scan
	 * @param genomePosition	the given genome position
	 */
	private static void dichotomicScan (int genomePosition) {
		int start = 0;
		int stop = indexes.length-1;

		while (!found) {
			int index = Math.round((start + stop) / 2);										// calculates the middle index
			int currentReferencePosition = getReferencePosition(index);						// gets the associated reference position
			int currentGenomePosition = getGenomePosition(currentReferencePosition);		// gets the associated meta genome position

			if (genomePosition > currentGenomePosition) {									// if the genome position is above the current genome position
				start = index;																// start is sets with the index
				int nextReferencePosition = getReferencePosition(index + 1);				// gets the next reference position
				int nextGenomePosition = getGenomePosition(nextReferencePosition);			// gets the next meta genome position

				if (genomePosition == nextGenomePosition) {									// if the genome position is equal to the next genome position
					finishDichotomicScan(nextReferencePosition);							// scan is over, next reference position is retained
				} else if (genomePosition < nextGenomePosition) {							// if it is under, it means the genome position is between the current and the next genome position
					finishDichotomicScan(currentReferencePosition);							// scan is over, current reference position is retained
				} else {																	// if it is above
					start++;																// start index is increased

					if (start >= (indexes.length-1)) {										// if start index is out of bound
						finishDichotomicScan(getReferencePosition(indexes.length-1));		// scan is over, last reference position is retained
					}
				}
			} else if (genomePosition < currentGenomePosition) {							// if the genome position is under the current genome position
				stop = index;																// stop is sets with the index
				int previousReferencePosition = getReferencePosition(index - 1);			// gets the previous reference genome position
				int previousGenomePosition = getGenomePosition(previousReferencePosition);	// gets the previous meta genome position

				if (genomePosition >= previousGenomePosition) {								// if the genome position is above or equal to the previous genome position, it means it is between the previous and the current genome position
					finishDichotomicScan(previousReferencePosition);						// scan is over, previous reference position is retained
				} else {																	// if it is under
					stop--;																	// stop index is decreased

					if (stop <= 0) {														// if stop index is out of bound
						finishDichotomicScan(getReferencePosition(0));						// scan is over, first reference position is retained
					}
				}
			} else {																		// if genome position is equal to the current genome position
				finishDichotomicScan(currentReferencePosition);								// scan is over, current reference position is retained
			}
		}
	}


	/**
	 * Gets the genome position of the the reference genome position
	 * @param referenceGenomePosition reference genome position
	 * @return	the genome position
	 */
	private static int getGenomePosition (int referenceGenomePosition) {
		return chromInfo.getPositionInformation(referenceGenomePosition).getGenomePosition();
	}


	/**
	 * Gets the genome position of the the reference genome position
	 * @param referenceGenomePosition reference genome position
	 * @return	the genome position
	 */
	private static int getMetaGenomePosition (int referenceGenomePosition) {
		return chromInfo.getPositionInformation(referenceGenomePosition).getMetaGenomePosition();
	}


	/**
	 * Gets the reference genome position in the index list
	 * @param index	index of the reference genome position
	 * @return		the reference position
	 */
	private static int getReferencePosition (int index) {
		int position;
		if (index < 0) {
			position = indexes[0];
		} else if (index > (indexes.length - 1)) {
			position = indexes[indexes.length - 1];
		} else {
			position = indexes[index];
		}
		return position;
	}


	/**
	 * Finishes the dichotomic scan
	 * It consists on setting the final reference position and setting the while loop boolean
	 * @param referenceGenomePosition	the final reference genome position
	 */
	private static void finishDichotomicScan (int referenceGenomePosition) {
		referencePosition = referenceGenomePosition;
		found = true;
	}


	/**
	 * Finishes the shifting process
	 * The genome position can have a difference with the genome position associated to the reference genome position found
	 * That difference must be taken in account
	 * @param genomePosition	the given genome position
	 */
	private static void finishShifting (int genomePosition) {
		VCFPositionInformation position = chromInfo.getPositionInformation(referencePosition);
		if (genomePosition >= position.getGenomePosition()) {
			if (genomePosition == position.getGenomePosition()) {
				newPosition = position.getMetaGenomePosition();
			} else if (genomePosition > position.getGenomePosition()) {
				newPosition = position.getNextMetaGenomePosition(genomePosition);
			}
		} else {
			newPosition = genomePosition;
		}
	}



	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Calculates the meta genome position according to the given genome position
	 * @param genome			the raw genome name (containing the genome position)
	 * @param chromosome		the chromosome (containing the genome position)
	 * @param genomePosition	the given genome position
	 * @return					the corresponding meta genome position
	 */
	public static int computeReversedShift (String genome, Chromosome chromosome, int metaGenomePosition) {
		// Parameters initialization
		initParameters(genome, chromosome, metaGenomePosition);

		// Dichotomic scan
		if (indexes.length > 0) {
			reversedDichotomicScan(metaGenomePosition);
			finishReversedShifting(metaGenomePosition);
		}

		// Return the final shifted position
		return newPosition;
	}


	/**
	 * Performs the dichotomic scan
	 * @param genomePosition	the given genome position
	 */
	private static void reversedDichotomicScan (int metaGenomePosition) {
		int start = 0;
		int stop = indexes.length-1;

		while (!found) {
			int index = Math.round((start + stop) / 2);										// calculates the middle index
			int currentReferencePosition = getReferencePosition(index);						// gets the associated reference position
			int currentMetaGenomePosition = getMetaGenomePosition(currentReferencePosition);		// gets the associated meta genome position

			if (metaGenomePosition > currentMetaGenomePosition) {									// if the genome position is above the current genome position
				start = index;																// start is sets with the index
				int nextReferencePosition = getReferencePosition(index + 1);				// gets the next reference position
				int nextMetaGenomePosition = getMetaGenomePosition(nextReferencePosition);			// gets the next meta genome position

				if (metaGenomePosition == nextMetaGenomePosition) {									// if the genome position is equal to the next genome position
					finishDichotomicScan(nextReferencePosition);							// scan is over, next reference position is retained
				} else if (metaGenomePosition < nextMetaGenomePosition) {							// if it is under, it means the genome position is between the current and the next genome position
					finishDichotomicScan(currentReferencePosition);							// scan is over, current reference position is retained
				} else {																	// if it is above
					start++;																// start index is increased

					if (start >= (indexes.length-1)) {										// if start index is out of bound
						finishDichotomicScan(getReferencePosition(indexes.length-1));		// scan is over, last reference position is retained
					}
				}
			} else if (metaGenomePosition < currentMetaGenomePosition) {							// if the genome position is under the current genome position
				stop = index;																// stop is sets with the index
				int previousReferencePosition = getReferencePosition(index - 1);			// gets the previous reference genome position
				int previousGenomePosition = getMetaGenomePosition(previousReferencePosition);	// gets the previous meta genome position

				if (metaGenomePosition >= previousGenomePosition) {								// if the genome position is above or equal to the previous genome position, it means it is between the previous and the current genome position
					finishDichotomicScan(previousReferencePosition);						// scan is over, previous reference position is retained
				} else {																	// if it is under
					stop--;																	// stop index is decreased

					if (stop <= 0) {														// if stop index is out of bound
						finishDichotomicScan(getReferencePosition(0));						// scan is over, first reference position is retained
					}
				}
			} else {																		// if genome position is equal to the current genome position
				finishDichotomicScan(currentReferencePosition);								// scan is over, current reference position is retained
			}
		}
	}


	/**
	 * Finishes the shifting process
	 * The genome position can have a difference with the genome position associated to the reference genome position found
	 * That difference must be taken in account
	 * @param metaGenomePosition	the given genome position
	 */
	private static void finishReversedShifting (int metaGenomePosition) {
		VCFPositionInformation position = chromInfo.getPositionInformation(referencePosition);
		if (metaGenomePosition >= position.getMetaGenomePosition()) {
			if (metaGenomePosition >= position.getMetaGenomePosition()) {
				if (metaGenomePosition < position.getNextMetaGenomePosition()) {
					newPosition = -1;
				} else if (metaGenomePosition == position.getNextMetaGenomePosition()) {
					newPosition = position.getNextGenomePosition();
				} else {
					newPosition = position.getNextGenomePosition() + (metaGenomePosition - position.getNextMetaGenomePosition());
				}
			}
		} else {
			newPosition = metaGenomePosition;
		}
	}

}