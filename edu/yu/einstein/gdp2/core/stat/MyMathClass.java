/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.stat;

import java.util.HashMap;
import yu.einstein.gdp2.exception.InvalidFactorialParameterException;
import yu.einstein.gdp2.exception.InvalidLambdaPoissonParameterException;

/**
 * MyMathClass
 * This class gather some methods to calculate mathematical functions.
 * These methods are developed for IslandFinder class.
 * @author Nicolas Fourel 
 */
public class MyMathClass {
	
	//Attribute to store Poisson values with parameter k as index, lambda on first double array position and the value on the second position
	private static HashMap<Integer, HashMap<Double, Double>> poissonStorage = new HashMap<Integer, HashMap<Double, Double>>();
	//Attribute to store factorial values with parameter k as index and the factorial value on the cell
	private static HashMap<Integer, Integer> factorialStorage = new HashMap<Integer, Integer>();
	
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
		double result;
		if (lambda <= 0){
			throw new InvalidLambdaPoissonParameterException();
		} else { 
			double exist = getPoissonValue(lambda, k);
			if (exist != -1.0){
				result = exist;
			} else {
				result = Math.exp(-lambda);
				result *= Math.pow(lambda, k);
				result /= factorial(k);
				setPoissonValue(lambda, k, result);
			}
		}
		return result;
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
				result = poissonStorage.get(k).get(lambda);
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
		if (!poissonStorage.containsKey(k)){
			poissonStorage.put(k, new HashMap<Double, Double>());
		}
		poissonStorage.get(k).put(lambda , value);
	}

	/**
	 * Factorial method
	 * This method calculate the parameter factorial.
	 * If the parameter factorial is already exists, method return it directly.
	 * If it doesn't exists it is calculate.
	 * If it is negative or equal to 0 the method throw to InvalidFactorialParameterException.
	 * 
	 * @param n to calculate its factorial 
	 */
	private static int factorial (int n) throws InvalidFactorialParameterException {
		int result;
		if (factorialStorage.containsKey(n)) {
			result = factorialStorage.get(n);
		}
		else if (n > 0) {
			result = 1;
			for (int i=1; i<=n; i++) {
				result *= i;
			}
			factorialStorage.put(n, result);
		} else {
			throw new InvalidFactorialParameterException();
		}
		return result;
	}
}