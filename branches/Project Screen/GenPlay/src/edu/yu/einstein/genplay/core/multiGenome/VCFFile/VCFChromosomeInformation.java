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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.yu.einstein.genplay.core.enums.VariantType;


/**
 * This class manages the chromosome information.
 * Those information are the position and its relative information.
 * @author Nicolas Fourel
 */
public class VCFChromosomeInformation {

	private Map<Integer, VCFPositionInformation>	positionInformation;	// Positions information
	private int[]									positionIndex;			// Mapping table for reference genome position
	private Integer 								currentPosition;		// Current position
	private Integer 								previousPosition;		// Previous position accessed


	/**
	 * Constructor of {@link VCFChromosomeInformation}
	 */
	protected VCFChromosomeInformation () {
		this.positionInformation = new TreeMap<Integer, VCFPositionInformation>();
	}


	/**
	 * Adds a position information.
	 * @param position	the position
	 * @param type		the information type
	 * @param length	the length
	 * @param offset	the offset position
	 */
	public void addInformation (Integer position, VariantType type, int length, Map<String, String> info) {
		positionInformation.put(position, new VCFPositionInformation(type, length, info));
	}

	
	/**
	 * Creates the index list.
	 * All reference genome position are indexed by consecutive integer.
	 */
	public void createPositionIndexList () {
		List<Integer> position = new ArrayList<Integer>(positionInformation.keySet());
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
			int nextGenomePosition = positionInformation.get(previousPosition).getNextGenomePosition();
			int nextReferenceGenomePosition = positionInformation.get(previousPosition).getNextReferenceGenomePosition();
			position = nextGenomePosition + (currentPosition - nextReferenceGenomePosition);
		}
		return position;
	}
	
	
	/**
	 * @param position	the position information
	 * @return			true if a position information is defined for this chromosome
	 */
	public boolean isInInformation (VCFPositionInformation position) {
		return positionInformation.containsKey(position.getGenomePosition());
	}
	
	
	/**
	 * @param position	the position
	 * @return the type of a specified position
	 */
	protected VariantType getType (int position) {
		if (positionInformation.get(position) != null) {
			positionInformation.get(position).getType();
		}
		return null;
	}
	
	
	/**
	 * @param position 	the position value
	 * @return			the position information according to the given position
	 */
	public VCFPositionInformation getPositionInformation (int position) {
		return positionInformation.get(position);
	}
	
	
	/**
	 * @param position 	the position value
	 * @return			the position information according to the given position
	 */
	public VCFPositionInformation getPositionInformationFromIndex (int index) {
		return positionInformation.get(positionIndex[index]);
	}
	
	
	/**
	 * @return	the current position information
	 */
	public VCFPositionInformation getCurrentPositionInformation () {
		return positionInformation.get(currentPosition);
	}
	
	
	/**
	 * @return the position information list
	 */
	public Map<Integer, VCFPositionInformation> getPositionInformationList() {
		return positionInformation;
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
		if (getPositionInformation(position) != null) {
			previousPosition = position;
		}
	}
	
	
	/**
	 * @return the previous position
	 */
	public VCFPositionInformation getPreviousPosition () {
		if (previousPosition != null) {
			return getPositionInformation(previousPosition);
		} else {
			return getPositionInformation(currentPosition);
		}
	}
	

	/**
	 * Shows positions information.
	 */
	public void showData () {
		for (Integer position: positionInformation.keySet()) {
			positionInformation.get(position).showData();
		}
	}

}