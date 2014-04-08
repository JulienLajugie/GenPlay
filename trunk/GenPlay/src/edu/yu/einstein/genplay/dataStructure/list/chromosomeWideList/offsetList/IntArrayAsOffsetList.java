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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.offsetList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.data.synchronization.MGSOffset;


/**
 * This class implements the List of MGOffset interface but internally
 * it contains an array of int that is dynamically resized in order to
 * be more memory efficient.
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class IntArrayAsOffsetList extends AbstractList<MGSOffset> implements Serializable, List<MGSOffset> {

	private static final long serialVersionUID = -8787392051503707843L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private static final int 	RESIZE_MIN = 1000;		// minimum length added every time the array is resized
	private static final int 	RESIZE_MAX = 10000000;	// maximum length added every time the array is resized
	private static final int 	RESIZE_FACTOR = 2;		// multiplication factor of the length of the array every time it's resized
	private int[] 				position;				// int position array
	private int[] 				value;					// int value array
	private int 				size;					// size of the list


	/**
	 * Creates an instance of {@link IntArrayAsOffsetList}
	 */
	public IntArrayAsOffsetList() {
		position = new int[0];
		value = new int[0];
		size = 0;
	}


	/**
	 * Creates an instance of {@link IntArrayAsOffsetList}
	 * @param size size of the array
	 */
	public IntArrayAsOffsetList(int size) {
		position = new int[size];
		value = new int[size];
		this.size = size;
	}


	@Override
	public boolean add(MGSOffset offset) {
		// if the array is to small we resize it before adding the data
		if (size >= position.length) {
			// we multiply the current size by the resize multiplication factor
			int newLength = position.length * RESIZE_FACTOR;
			// we make sure we don't add less than RESIZE_MIN elements
			newLength = Math.max(newLength, position.length + RESIZE_MIN);
			// we make sure we don't add more than RESIZE_MAX elements
			newLength = Math.min(newLength, position.length + RESIZE_MAX);
			int[] newPosition = new int[newLength];
			int[] newValue = new int[newLength];
			for (int i = 0; i < position.length; i++) {
				newPosition[i] = position[i];
				newValue[i] = value[i];
			}
			position = newPosition;
			value = newValue;
		}
		// true if e is not zero
		position[size] = offset.getPosition();
		value[size] = offset.getValue();
		size++;
		return true;
	}


	/**
	 * Recreates the arrays with the right size in order to optimize the memory usage.
	 */
	public void compact() {
		int[] positionTmp = new int[size];
		int[] valueTmp = new int[size];
		for (int i = 0; i < size; i++) {
			positionTmp[i] = position[i];
			valueTmp[i] = value[i];
		}
		position = positionTmp;
		value = valueTmp;
	}


	/**
	 * Recursive function. Returns the index where the genome position is found
	 * or the index right after if the exact value is not found.
	 * @param value			value of a genome position
	 * @param indexStart
	 * @param indexStop
	 * @return the index where the start value of the window is found or the index right after if the exact value is not found
	 */
	private int findGenomeIndex (int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == position[indexStart + middle]) {
			return indexStart + middle;
		} else if (value > position[indexStart + middle]) {
			return findGenomeIndex(value, indexStart + middle + 1, indexStop);
		} else {
			return findGenomeIndex(value, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive function. Returns the index where the meta genome position is found
	 * or the index right after if the exact value is not found.
	 * @param value			value of a meta genome position
	 * @param indexStart
	 * @param indexStop
	 * @return the index where the start value of the window is found or the index right after if the exact value is not find
	 */
	private int findMetaGenomeIndex (int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else {
			int currentMetaGenomePosition = position[indexStart + middle] + this.value[indexStart + middle];
			if (value == currentMetaGenomePosition) {
				return indexStart + middle;
			} else if (value > currentMetaGenomePosition) {
				return findMetaGenomeIndex(value, indexStart + middle + 1, indexStop);
			}
			return findMetaGenomeIndex(value, indexStart, indexStart + middle);
		}
	}


	@Override
	public MGSOffset get(int index) {
		return new MGSOffset(position[index], value[index]);
	}


	/**
	 * @param metaGenomePosition the position on the meta genome
	 * @return the genome position associated to the given meta genome position
	 */
	public int getGenomePosition (int metaGenomePosition) {
		if (size == 0) {
			return metaGenomePosition;
		}
		int genomePositionIndex = findMetaGenomeIndex(metaGenomePosition, 0, size - 1);	// get the index of the position (or the one right after)

		int metaGenomePositionFound = position[genomePositionIndex] + value[genomePositionIndex];	// get the meta genome position found (genome position + its meta genome offset)

		if (metaGenomePosition == metaGenomePositionFound) {					// if both position are equal
			return position[genomePositionIndex];								// the genome position is the one found
		} else if (metaGenomePosition > metaGenomePositionFound) {				// if the meta genome position is higher than the one found
			int difference = metaGenomePosition - metaGenomePositionFound;		// calculation of the difference of both position: meta genome seek and meta genome found
			return (position[genomePositionIndex] + difference);				// the new meta genome position must be the one found plus the calculated difference
		} else {																// if the meta genome position is lower than the one found
			int difference = metaGenomePositionFound - metaGenomePosition;		// we want to know the difference between both position in order to compare with the variation length
			int variationLength = value[genomePositionIndex];					// by default the variation length is the current offset (with the MG) of the index found

			if (genomePositionIndex > 0) {										// if the current index is not the first one, the variation length just instantiated is the sum of all offset of the previous variations
				variationLength -= value[genomePositionIndex - 1];				// in order to have the current variation length, we subtract with the offset of the previous index.
			}

			if (difference <= variationLength) {								// if the seek meta genome position is included in the variation length,
				return MGSOffset.MISSING_POSITION_CODE;							// this meta genome position does not match with a position of the genome
			} else {															// if the seek meta genome position is not included in the variation length,
				difference -= variationLength;									// we calculate the difference between the current genome position and the one we are looking for
				return position[genomePositionIndex] - difference;				// we subtract this difference to the genome position found.
			}
		}
	}


	/**
	 * @param genomePosition a position on the genome
	 * @return the index of the {@link MGSOffset} at the given genome position, or the index before if no exact match
	 */
	public int getIndex (int genomePosition) {
		if (size == 0) {
			return -1;
		}
		int genomePositionIndex = findGenomeIndex(genomePosition, 0, size - 1);	// get the index of the position (or the one right after)
		if (genomePosition < position[genomePositionIndex]) {					// if the position is lower than the one found (ie we want the index right before)
			if (genomePositionIndex > 0) {										// if the index found is not the first one in the list
				genomePositionIndex--;											// it is the one we are looking for
			}
		}
		return genomePositionIndex;
	}


	/**
	 * @param genomePosition the position on the genome
	 * @return the meta genome position associated to the given genome position
	 */
	public int getMetaGenomePosition (int genomePosition) {
		if (size == 0) {
			return genomePosition;
		}
		int genomePositionIndex = findGenomeIndex(genomePosition, 0, size - 1);	// get the index of the position (or the one right after)
		if (genomePosition < position[genomePositionIndex]) {					// if the position is lower than the one found (ie we want the index right before)
			if (genomePositionIndex == 0) {										// if the index found is the first one in the list
				return genomePosition;											// there is no offset yet therefore genome position and meta genome position are similar
			} else {															// if there is at least one index before
				genomePositionIndex--;											// it is the one we are looking for
			}
		}
		int difference  = genomePosition - position[genomePositionIndex];
		int metaGenomePosition = position[genomePositionIndex] + value[genomePositionIndex] + difference; // the meta genome position is the genome position plus its offset

		return metaGenomePosition;												// we return the meta genome position
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		position = (int[]) in.readObject();
		value = (int[]) in.readObject();
		size = in.readInt();
	}

	/**
	 * @return null in order to accelerate the operation
	 */
	@Override
	public MGSOffset set(int index, MGSOffset offset) {
		position[index] = offset.getPosition();
		value[index] = offset.getValue();
		return null;
	}


	/**
	 * Shows the content of this object
	 */
	public void show () {
		String info = "size = " + size + " -> ";
		for (int i = 0; i < position.length; i++) {
			info += "[" + position[i] + "; " + value[i] + "] ";
		}
		System.out.println(info);
	}


	@Override
	public int size() {
		return size;
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(position);
		out.writeObject(value);
		out.writeInt(size);
	}
}
