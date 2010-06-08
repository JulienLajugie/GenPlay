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
 * This class gather some methods to calculate Poisson values.
 * These methods are developed for IslandFinder class.
 * @author Nicolas Fourel 
 */
public class Poisson {
	
	//Attribute to store Poisson values with parameter k as index, lambda on first double array position and the value on the second position
	private static HashMap<Integer, HashMap<Double, Double>> poissonStorage = new HashMap<Integer, HashMap<Double, Double>>();
	
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
				result = res1 + res2 - res3;
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
	
}