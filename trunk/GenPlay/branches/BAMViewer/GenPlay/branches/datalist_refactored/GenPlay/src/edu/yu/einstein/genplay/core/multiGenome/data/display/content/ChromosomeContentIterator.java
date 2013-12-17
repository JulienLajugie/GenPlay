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
package edu.yu.einstein.genplay.core.multiGenome.data.display.content;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * The {@link ChromosomeContentIterator} iterates through a {@link MGChromosomeContent} returning {@link MGLineContent}.
 * It contains a "SmartIterator" feature, in this case only one {@link MGLineContent} will be created and initialized before being returned.
 * It has the advantage to avoid memory peaks.
 * If the returned {@link MGLineContent} has to be stored, it probably must be used as a different object, in this case, do not use the "SmartIterator" feature.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ChromosomeContentIterator implements Iterator<MGLineContent> {

	private final MGChromosomeContent 	chromosomeContent;			// The chromosome content.
	private int 						currentIndex = 0;			// The current index of the iterator.
	private MGLineContent				smartPosition;				// The object used when "SmartIterator" feature is enabled.
	private boolean 					smartIterator;				// True if the "SmartIterator" feature is enabled, false otherwise.


	/**
	 * Constructor of {@link ChromosomeContentIterator}
	 */
	protected ChromosomeContentIterator (MGChromosomeContent fileList) {
		this.chromosomeContent = fileList;
		currentIndex = 0;
		smartPosition = null;
		smartIterator = false;
	}


	@Override
	public boolean hasNext() {
		return isValidIndex();
	}


	@Override
	public MGLineContent next() throws NoSuchElementException {
		if (isValidIndex()) {
			if (smartIterator) {										// If the "SmartIterator" feature is enabled, the position object must be initialized and returned.
				if (smartPosition == null) {
					smartPosition = new MGLineContent();
				}
				smartPosition = chromosomeContent.getPosition(smartPosition, currentIndex);
				currentIndex++;
				return smartPosition;
			}
			MGLineContent currentPosition = chromosomeContent.getPosition(currentIndex);		// A new position object is returned otherwise
			currentIndex++;
			return currentPosition;
		}
		throw new NoSuchElementException();
	}


	@Override
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}


	/**
	 * @return true if the current index is valid, false otherwise
	 */
	private boolean isValidIndex () {
		if (currentIndex < chromosomeContent.getSize()) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * @return the current index, if called right after the next() method, it will return the next index.
	 */
	public int getCurrentIndex () {
		return currentIndex;
	}


	/**
	 * @return the smartIterator
	 */
	public boolean isSmartIterator() {
		return smartIterator;
	}


	/**
	 * @param smartIterator the smartIterator to set
	 */
	public void setSmartIterator(boolean smartIterator) {
		this.smartIterator = smartIterator;
	}
}