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
 * @author Nicolas Fourel
 * @version 0.1
 */
class ChromosomeContentIterator implements Iterator<MGLineContent> {

	private final MGChromosomeContent fileList;
	private int currentIndex = 0;
	private MGLineContent smartPosition;
	private boolean smartIterator;


	/**
	 * Constructor of {@link ChromosomeContentIterator}
	 */
	protected ChromosomeContentIterator (MGChromosomeContent fileList) {
		this.fileList = fileList;
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
			if (smartIterator) {
				if (smartPosition == null) {
					smartPosition = new MGLineContent();
				}
				smartPosition = fileList.getPosition(smartPosition, currentIndex);
				currentIndex++;
				return smartPosition;
			}
			MGLineContent currentPosition = fileList.getPosition(currentIndex);
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
		if (currentIndex < fileList.getSize()) {
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