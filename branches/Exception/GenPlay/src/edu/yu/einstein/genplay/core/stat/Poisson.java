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
package edu.yu.einstein.genplay.core.stat;

import java.io.Serializable;
import java.util.HashMap;

import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFactorialParameterException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidLambdaPoissonParameterException;

/**
 * MyMathClass
 * This class gather some methods to calculate Poisson values.
 * These methods are developed for IslandFinder class.
 * @author Nicolas Fourel
 */
public class Poisson implements Serializable {

	private static final long serialVersionUID = 256395258875515443L;
	//Attribute to store Poisson values with parameter k as index, lambda on first double array position and the value on the second position
	private volatile static HashMap<Integer, HashMap<Double, Double>> poissonStorage = new HashMap<Integer, HashMap<Double, Double>>();

	/**
	 * logPoisson method
	 * Calculate the Poisson value logarithm with Poisson parameters
	 * 
	 * @param lambda	lambda Poisson parameter
	 * @param k			k Poisson parameter
	 * @return			Poisson value
	 * @throws InvalidLambdaPoissonParameterException
	 * @throws InvalidFactorialParameterException
	 */
	public static double logPoisson (double lambda, int k) throws InvalidLambdaPoissonParameterException, InvalidFactorialParameterException {
		double result;
		if (lambda <= 0){
			throw new InvalidLambdaPoissonParameterException();
		} else {
			double exist = getPoissonValue(lambda, k);
			if (exist != -1.0){
				result = exist;
			} else {
				double res1 = Math.log10(Math.exp(-lambda));
				double res2 = k * Math.log10(lambda);
				double res3 = Factorial.logFactorial(k);
				result = (res1 + res2) - res3;
				setPoissonValue(lambda, k, result);
			}
		}
		return result;
	}

	/**
	 * poisson method
	 * Calculate the Poisson value with Poisson parameters
	 * 
	 * @param lambda	lambda Poisson parameter
	 * @param k			k Poisson parameter
	 * @return			Poisson value
	 * @throws InvalidLambdaPoissonParameterException
	 * @throws InvalidFactorialParameterException
	 */
	public static double poisson (double lambda, int k) throws InvalidLambdaPoissonParameterException, InvalidFactorialParameterException {
		return MathFunctions.unlogValue(logPoisson(lambda, k));
	}


	/**
	 * getPoissonValue method
	 * This method allow to get a Poisson value if it is stored
	 * It need to know the lambda and k Poisson parameters
	 * 
	 * @param lambda	lambda Poisson parameter
	 * @param k			k Poisson parameter
	 * @return			Poisson value if it is stored or -1.0
	 */
	private static double getPoissonValue (double lambda, int k) {
		double result = -1.0;
		if (poissonStorage.containsKey(k)){
			if (poissonStorage.get(k).containsKey(lambda)){
				try {
					result = poissonStorage.get(k).get(lambda);
				} catch (Exception e) {
					result = -1.0;
					System.out.println("getPoissonValue error; lambda: " + lambda + "; k: " + k);

					ExceptionManager.getInstance().handleException(e);
				}
			}
		}
		return result;
	}

	/**
	 * setPoissonValue method
	 * This method allow to store a Poisson value in the HashMap
	 * 
	 * @param lambda	lambda Poisson parameter
	 * @param k			k Poisson parameter
	 * @param value		the Poisson value calculated with lambda and k values
	 */
	private static void setPoissonValue (double lambda, int k, double value) {
		if (!poissonStorage.containsKey(k) | (poissonStorage.get(k)==null)){
			poissonStorage.put(k, new HashMap<Double, Double>());
		}
		try{
			poissonStorage.get(k);
			poissonStorage.get(k).put(lambda , value);
		} catch (Exception e) {
			ExceptionManager.getInstance().handleException(e);
		}
	}

}
