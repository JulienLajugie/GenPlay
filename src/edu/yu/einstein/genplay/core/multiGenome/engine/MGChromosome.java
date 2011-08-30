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
package edu.yu.einstein.genplay.core.multiGenome.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.enums.VariantType;


/**
 * This class manages the chromosome information in a multi genome project.
 * Those information are mainly the list of variant for a specific chromosome.
 * 
 * This class can be considered as a "sub-class" of {@link MGGenome}.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGChromosome implements Serializable {

	private static final long serialVersionUID = -6878208329536733167L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	
	private MGGenome 					genomeInformation;		// The genome
	private Chromosome 					chromosome;				// The chromosome
	private Map<Integer, Variant>		variantList;			// Variant list, keys are reference genome position and values are variants
	private int[]						positionIndex;			// Mapping table for reference genome position
	private int		 					currentPosition;		// Current position
	private int							previousPosition = -1;	// Previous position accessed

		
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(genomeInformation);
		out.writeObject(chromosome);
		out.writeObject(variantList);
		out.writeObject(positionIndex);
		out.writeInt(currentPosition);
		out.writeInt(previousPosition);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		genomeInformation = (MGGenome) in.readObject();
		chromosome = (Chromosome) in.readObject();
		variantList = (Map<Integer, Variant>) in.readObject();
		positionIndex = (int[]) in.readObject();
		currentPosition = in.readInt();
		previousPosition = in.readInt();
	}
	

	/**
	 * Constructor of {@link MGChromosome}
	 * @param genomeInformation the genome 
	 * @param chromosome 		the chromosome
	 */
	protected MGChromosome (MGGenome genomeInformation, Chromosome chromosome) {
		this.chromosome = chromosome;
		this.genomeInformation = genomeInformation;
		this.variantList = new TreeMap<Integer, Variant>();
	}


	/**
	 * Adds a blank variant to the list
	 * @param position	the position on the reference genome
	 * @param variant	the blank variant
	 */
	protected void addBlank (Integer position, Variant variant) {
		variantList.put(position, variant);
	}


	/**
	 * Add a variant to the list
	 * @param variant	the variant
	 */
	protected void addVariant (Variant variant) {
		variantList.put(variant.getPositionInformation().getPos(), variant);
	}


	/**
	 * Creates the index list.
	 * All reference genome position are indexed by consecutive integer.
	 */
	private void createPositionIndexList () {
		List<Integer> position = new ArrayList<Integer>(variantList.keySet());
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
	 * @return true if the current variant is the first of the list.
	 */
	public boolean isFirstVariant () {
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
		if (isFirstVariant()) {
			position = positionIndex[0];
		} else {
			Variant current = variantList.get(previousPosition);
			int nextGenomePosition = current.getNextGenomePosition();
			int nextReferenceGenomePosition = current.getNextReferenceGenomePosition();
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
		if (variantList.get(position) != null) {
			variantList.get(position).getType();
		}
		return null;
	}


	/**
	 * @return	the current position information
	 */
	public Variant getCurrentVariant () {
		return variantList.get(currentPosition);
	}


	/**
	 * @param position 	the reference genome position
	 * @return			the position information according to the given position
	 */
	public Variant getVariant (int position) {
		return variantList.get(position);
	}


	/**
	 * @param index 	the index of the reference genome position
	 * @return			the position information according to the given index
	 */
	public Variant getVariantFromIndex (int index) {
		return variantList.get(positionIndex[index]);
	}


	/**
	 * @return the position information list
	 */
	public Map<Integer, Variant> getPositionInformationList() {
		return variantList;
	}


	/**
	 * @param currentPosition the currentPosition to set
	 */
	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}


	/**
	 * Updates the previous position accessed
	 * @param position	the previous position
	 */
	public void updatePreviousPosition (int position) {
		if (getVariant(position) != null) {
			previousPosition = position;
		}
	}


	/**
	 * @return the previous position
	 */
	public Variant getPreviousPosition () {
		if (previousPosition != -1) {
			return getVariant(previousPosition);
		} else {
			return getVariant(currentPosition);
		}
	}


	/**
	 * @return the genomeInformation
	 */
	public MGGenome getGenomeInformation() {
		return genomeInformation;
	}

	
	/**
	 * @param position 	reference genome position
	 * @return			the position information according to the position
	 */
	protected MGPosition getMGPosition (int position) {
		Variant current = variantList.get(position);
		if (current != null) {
			return current.getPositionInformation();
		}
		return null;
	}
	

	/**
	 * Shows positions information.
	 */
	public void showData () {
		for (Variant position: variantList.values()) {
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
