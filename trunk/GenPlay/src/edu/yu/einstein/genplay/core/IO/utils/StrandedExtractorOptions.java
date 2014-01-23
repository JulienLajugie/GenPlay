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
package edu.yu.einstein.genplay.core.IO.utils;

import edu.yu.einstein.genplay.core.IO.extractor.StrandedExtractor;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;

/**
 * Options for {@link StrandedExtractor}.
 * Allows user to choose which strand to extract and to define a fragment size
 * @author Julien Lajugie
 */
public class StrandedExtractorOptions {

	private final Strand 	selectedStrand;	// strand to extract. Both if null
	private final int 		fragmentLength;	// length of the fragments
	private final int 		readLength;		// length of the reads


	/**
	 * Creates an instance of {@link StrandedExtractorOptions}
	 * @param strandToExtract strand to extract. Both strand will be extracted if this parameter is null
	 * @param fragmentLength length of the fragments
	 * @param readLength length of the reads
	 */
	public StrandedExtractorOptions(Strand strandToExtract, int fragmentLength, int readLength) {
		super();
		selectedStrand = strandToExtract;
		this.fragmentLength = fragmentLength;
		this.readLength = readLength;
	}


	/**
	 * Computes the new start and stop positions of a specified read after applying the read length options.
	 * @param chromo chromosome of the read
	 * @param start start position of the read
	 * @param stop stop position of the read
	 * @param strand strand of the read
	 * @return a {@link SimpleChromosomeWindow} with the result start and stop positions
	 */
	public SimpleChromosomeWindow computeStartStop(Chromosome chromo, int start, int stop, Strand strand) {
		if ((strand == null) || (chromo == null) || (readLength == 0) || (fragmentLength == 0)) {
			// we return the value without modifying them
			return new SimpleChromosomeWindow(start, stop);
		}
		if (strand == Strand.THREE) {
			// case where the read is on the 3' strand
			// we want to make sure that the result positions are not smaller than 1
			start = Math.max(1, (start + readLength) - fragmentLength);
		}
		// we want to make sure that the result positions are not greater than the chromosome length
		stop = Math.min(chromo.getLength(), start + fragmentLength);
		return new SimpleChromosomeWindow(start, stop);
	}


	/**
	 * @return the fragment length value
	 */
	public int getFragmentLength() {
		return fragmentLength;
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
