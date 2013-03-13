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
package edu.yu.einstein.genplay.dataStructure.enums;

import edu.yu.einstein.genplay.dataStructure.genomeList.SCWList.ScoredChromosomeWindowList;


/**
 * Enumeration of the different types of {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 */
public enum SCWListType {

	/** A generic, multi-purpose {@link ScoredChromosomeWindowList} */
	GENERIC ("Generic List"),

	/** A mask {@link ScoredChromosomeWindowList} with every windows having a score of one */
	MASK ("Mask List"),

	/**  A bin {@link ScoredChromosomeWindowList} having windows of a fixed lengths */
	BIN ("Bin List");


	/**  description of the enumeration element */
	private final String description;


	/**
	 * Creates an instance of {@link SCWListType}.
	 * @param description description of the {@link SCWListType} element
	 */
	private SCWListType(String description) {
		this.description = description;
	}


	@Override
	public String toString() {
		return description;
	}


	/**
	 * @return the description of the {@link SCWListType} element
	 */
	public String getDescription() {
		return description;
	}
}
