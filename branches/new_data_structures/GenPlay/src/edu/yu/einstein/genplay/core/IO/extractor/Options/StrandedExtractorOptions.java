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
package edu.yu.einstein.genplay.core.IO.extractor.Options;

import edu.yu.einstein.genplay.core.IO.extractor.StrandedExtractor;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;

/**
 * Options for {@link StrandedExtractor}.
 * Allows user to choose which strand to extract, to define a fixed read length and to shift the reads.
 * @author Julien Lajugie
 */
public class StrandedExtractorOptions {

	private final Strand 	selectedStrand;	// strand to extract. Both if null
	private final int 		strandShift;	// value of the shift to perform on the selected strand
	private final int 		readLength;		// length of the reads. 0 to keep the values from the input data file


	/**
	 * Creates an instance of {@link StrandedExtractorOptions}
	 * @param strandToExtract strand to extract. Both strand will be extracted if this parameter is null
	 * @param strandShift value of the shift to perform on the selected strand
	 * @param readLength length of the reads. 0 to keep the values from the input data file
	 */
	public StrandedExtractorOptions(Strand strandToExtract, int strandShift, int readLength) {
		super();
		selectedStrand = strandToExtract;
		this.strandShift = strandShift;
		this.readLength = readLength;
	}


	/**
	 * Computes the new start and stop positions of a specified read after applying
	 * the shift and the read length options.
	 * @param chromo chromosome of the read
	 * @param start start positions of the read
	 * @param stop stop position of the read
	 * @param strand strand of the read
	 * @return a {@link SimpleChromosomeWindow} with the result start and stop positions
	 */
	public SimpleChromosomeWindow computeStartStop(Chromosome chromo, int start, int stop, Strand strand) {
		if ((strand == null) || (chromo == null)) {
			// if the strand or chromosome parameter is null we return the value without modifying them
			return new SimpleChromosomeWindow(start, stop);
		}
		if (strand == Strand.FIVE) {
			// case where the read is on the 5' strand
			// we want to make sure that the result positions are not greater than the chromosome length
			start = Math.min(chromo.getLength(), start + strandShift);
			if (readLength == 0) {
				// case where we keep the original read length
				stop = Math.min(chromo.getLength(), stop + strandShift);
			} else {
				// case where we use the read length specified by the user
				stop = Math.min(chromo.getLength(), start + readLength);
			}
		} else {
			// case where the read is on the 3' strand
			// we want to make sure that the result positions are not smaller than zero
			stop = Math.max(0, stop - strandShift);
			if (readLength == 0) {
				// case where we keep the original read length
				start = Math.max(0, start - strandShift);

			} else {
				// case where we use the read length specified by the user
				start = Math.max(0, stop - readLength);
			}
		}
		return new SimpleChromosomeWindow(start, stop);
	}


	/**
	 * @return the read length value
	 */
	public int getReadLength() {
		return readLength;
	}


	/**
	 * @return the {@link Strand} selected to be extracted. Null means that both strands are selected to be extracted.
	 */
	public Strand getSelectedStrand() {
		return selectedStrand;
	}


	/**
	 * @return the strand shift value
	 */
	public int getStrandShift() {
		return strandShift;
	}


	/**
	 * @param strand a {@link Strand}
	 * @return true if the specified strand is selected to be extracted. False otherwise.
	 */
	public boolean isSelected(Strand strand) {
		if (selectedStrand == null) {
			return true;
		}
		return selectedStrand == strand;
	}
}
