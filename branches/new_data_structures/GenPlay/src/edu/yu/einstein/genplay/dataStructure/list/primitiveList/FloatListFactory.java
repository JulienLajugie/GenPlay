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
package edu.yu.einstein.genplay.dataStructure.list.primitiveList;

import java.security.InvalidParameterException;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;

/**
 * Factory class for vending List<Float> objects.
 * @author Julien Lajugie
 */
public class FloatListFactory {

	/** Precision of the data of the project */
	private static ScorePrecision scorePrecision = ScorePrecision.PRECISION_16BIT;

	/**
	 * @return a List of Float
	 */
	public static List<Float> createFloatList() {
		if (scorePrecision == ScorePrecision.PRECISION_16BIT) {
			return new ListOfHalfArraysAsFloatList();
		} else if (scorePrecision == ScorePrecision.PRECISION_32BIT) {
			return new ListOfFloatArraysAsFloatList();
		} else {
			throw new InvalidParameterException("Invalid Score Precision");
		}
	}


	/**
	 * Sets the score precision of the project
	 * @param scorePrecision
	 */
	public static void setScorePrecision(ScorePrecision scorePrecision) {
		FloatListFactory.scorePrecision = scorePrecision;
	}
}
