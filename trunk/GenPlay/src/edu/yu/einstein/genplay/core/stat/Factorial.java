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
package edu.yu.einstein.genplay.core.stat;

import java.io.Serializable;
import java.util.HashMap;

import edu.yu.einstein.genplay.exception.exceptions.InvalidFactorialParameterException;

/**
 * Factorial
 * This class allow to calculate factorial logarithm.
 * 
 * @author Nicolas Fourel
 */
public class Factorial implements Serializable {

	private static final long serialVersionUID = -5327997963998890691L;
	//Attribute to store factorial values with parameter k as index and the factorial value on the cell
	private static HashMap<Integer, Double> logFactorialStorage = new HashMap<Integer, Double>();

	/**
	 * logFactorial method
	 * This method calculate the parameter factorial logarithm.
	 * If the parameter factorial logarithm already exists, method return it directly.
	 * If it doesn't exists it is calculate.
	 * If it is equal to 0 the method return 1.
	 * If it is negative the method throw to InvalidFactorialParameterException.
	 * 
	 * @param n to calculate its factorial
	 * @return the factorial logarithm of the input parameter
	 * @throws InvalidFactorialParameterException
	 */
	public static double logFactorial (int n) throws InvalidFactorialParameterException {
		double result = 0.0;
		if (n == 0) {
			result = 1.0;
		} else if (logFactorialStorage.containsKey(n)) {
			result = logFactorialStorage.get(n);
		} else if (n > 0) {
			for (int i=1; i<=n; i++) {
				result += Math.log10(i);
			}
			logFactorialStorage.put(n, result);
		} else {
			throw new InvalidFactorialParameterException();
		}
		return result;
	}

}
