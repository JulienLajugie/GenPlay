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
package edu.yu.einstein.genplay.core.multiGenome.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.enums.VCFType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFBlank;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFIndel;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFSNP;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFSV;


/**
 * This class manages the chromosome information.
 * Those information are the position and its relative information.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGChromosomeInformation {

	private final MGGenomeInformation genomeInformation;
	private Chromosome chromosome;
	private Map<Integer, MGPosition>	variants;	// Positions information
	private int[]									positionIndex;			// Mapping table for reference genome position
	private Integer 								currentPosition;		// Current position
	private Integer 								previousPosition;		// Previous position accessed


	/**
	 * Constructor of {@link MGChromosomeInformation}
	 */
	protected MGChromosomeInformation (Chromosome chromosome, MGGenomeInformation genomeInformation) {
		this.chromosome = chromosome;
		this.genomeInformation = genomeInformation;
		this.variants = new TreeMap<Integer, MGPosition>();
	}


	/**
	 * Adds a position information.
	 * @param position 				position of the variant on the reference genome
	 * @param fullGenomeName 		the full genome name
	 * @param VCFLine 				the VCF line (presents in the file)
	 * @param positionInformation 	the position information associated to the VCF line
	 * @param vcfType 				the VCF file type
	 */
	public void addVariant (Integer position, String fullGenomeName, Map<String, Object> VCFLine, MGPositionInformation positionInformation, VCFType vcfType) {
		if (vcfType == VCFType.INDELS) {
			variants.put(position, new VCFIndel(fullGenomeName, chromosome, VCFLine, positionInformation));
		} else if (vcfType == VCFType.SV) {
			variants.put(position, new VCFSV(fullGenomeName, chromosome, VCFLine, positionInformation));
		} else if (vcfType == VCFType.SNPS) {
			// would probably never happen
			variants.put(position, new VCFSNP(fullGenomeName, chromosome, VCFLine, positionInformation));
		}
	}

	/**
	 * Adds a position information.
	 * @param position	the position
	 * @param variant	the variant
	 */
	public void addBlank (Integer position, MGPosition variant) {
		variants.put(position, variant);
	}


	/**
	 * Creates the index list.
	 * All reference genome position are indexed by consecutive integer.
	 */
	public void createPositionIndexList () {
		List<Integer> position = new ArrayList<Integer>(variants.keySet());
		Collections.sort(position);
		positionIndex = new int[position.size()];
		for (int i = 0; i < position.size(); i++) {
			positionIndex[i] = position.get(i);
		}
	}


	/**
	 * Resets the index list.
	 */
	public void resetIndexList () {
		positionIndex = null;
		createPositionIndexList();
	}


	/**
	 * @return the positionIndex
	 */
	public int[] getPositionIndex() {
		return positionIndex;
	}


	/**
	 * @return true if the current position is the first of the list.
	 */
	public boolean isFirstPosition () {
		if (currentPosition == positionIndex[0]) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Calculates the current genome position.
	 * Uses information from the previous position stored.
	 * @return the current genome position
	 */
	public int getGenomePosition () {
		int position = 0;
		if (isFirstPosition()) {
			position = positionIndex[0];
		} else {
			int nextGenomePosition = 0;
			int nextReferenceGenomePosition = 0;
			MGPosition current = variants.get(previousPosition);
			if (current instanceof VCFIndel) {
				nextGenomePosition = ((VCFIndel)variants.get(previousPosition)).getNextGenomePosition();
				nextReferenceGenomePosition = ((VCFIndel)variants.get(previousPosition)).getNextReferenceGenomePosition();
			} else if (current instanceof VCFBlank) {
				nextGenomePosition = ((VCFBlank)variants.get(previousPosition)).getNextGenomePosition();
				nextReferenceGenomePosition = ((VCFBlank)variants.get(previousPosition)).getNextReferenceGenomePosition();
			} else if (current instanceof VCFSNP) {
				nextGenomePosition = ((VCFSNP)variants.get(previousPosition)).getNextGenomePosition();
				nextReferenceGenomePosition = ((VCFSNP)variants.get(previousPosition)).getNextReferenceGenomePosition();
			}
			
			position = nextGenomePosition + (currentPosition - nextReferenceGenomePosition);
		}
		return position;
	}


	/**
	 * @return the chromosome
	 */
	public Chromosome getChromosome() {
		return chromosome;
	}


	/**
	 * @param position	the position
	 * @return the type of a specified position
	 */
	protected VariantType getType (int position) {
		if (variants.get(position) != null) {
			variants.get(position).getType();
		}
		return null;
	}


	/**
	 * @param position 	the position value
	 * @return			the position information according to the given position
	 */
	public MGPosition getPosition (int position) {
		return variants.get(position);
	}


	/**
	 * @param index 	the position value
	 * @return			the position information according to the given position
	 */
	public MGPosition getPositionInformationFromIndex (int index) {
		return variants.get(positionIndex[index]);
	}


	/**
	 * @return	the current position information
	 */
	public MGPosition getCurrentPositionInformation () {
		return variants.get(currentPosition);
	}


	/**
	 * @return the position information list
	 */
	public Map<Integer, MGPosition> getPositionInformationList() {
		return variants;
	}


	/**
	 * @param currentPosition the currentPosition to set
	 */
	public void setCurrentPosition(Integer currentPosition) {
		this.currentPosition = currentPosition;
	}


	/**
	 * Updates the previous position accessed
	 * @param position	the previous position
	 */
	public void updatePreviousPosition (int position) {
		if (getPosition(position) != null) {
			previousPosition = position;
		}
	}


	/**
	 * @return the previous position
	 */
	public MGPosition getPreviousPosition () {
		if (previousPosition != null) {
			return getPosition(previousPosition);
		} else {
			return getPosition(currentPosition);
		}
	}


	/**
	 * @return the genomeInformation
	 */
	public MGGenomeInformation getGenomeInformation() {
		return genomeInformation;
	}


	/**
	 * @param position position of the variant on the reference genome
	 * @return the associated position information
	 */
	public MGPositionInformation getPositionInformation (int position) {
		if (variants.get(position) != null) {
			return variants.get(position).getPositionInformation();
		}
		return null;
	}


	/**
	 * Shows positions information.
	 */
	public void showData () {
		for (MGPosition position: variants.values()) {
			System.out.println("------------------------------------------------------------");
			System.out.println(position.getType().name() + " (" + position.getLength() + ")");
			System.out.println(position.getGenomePosition() + " -> " + position.getNextGenomePosition());
			System.out.println(position.getReferenceGenomePosition() + " -> " + position.getNextReferenceGenomePosition() + " (" + position.getInitialReferenceOffset() + ", " + position.getNextReferencePositionOffset() + ")");
			System.out.println(position.getMetaGenomePosition() + " -> " + position.getNextMetaGenomePosition() + " (" + position.getInitialMetaGenomeOffset() + ", " + position.getNextMetaGenomePositionOffset() + ")");
			System.out.println(position.getExtraOffset());
		}

		/*for (VCFPositionInformation posInfo: positionInformation) {
			posInfo.showData();
		}*/
	}

}