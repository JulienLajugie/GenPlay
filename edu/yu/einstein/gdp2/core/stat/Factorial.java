/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.stat;

import java.util.HashMap;
import yu.einstein.gdp2.exception.InvalidFactorialParameterException;

/**
 * Factorial
 * This class allow to calculate factorial logarithm. 
 * 
 * @author Nicolas Fourel
 */
public class Factorial {

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
	 */
	public static double logFactorial (int n) throws InvalidFactorialParameterException {
		double result = 0.0;
		if (logFactorialStorage.containsKey(n)) {
			result = logFactorialStorage.get(n);
		} else if (n > 0) {
			for (int i=1; i<=n; i++) {
				result += Math.log10(i);
			}
			logFactorialStorage.put(n, result);
		} else if (n == 0) {
			result = 1.0;
		} else {
			throw new InvalidFactorialParameterException();
		}
		return result;
	}
	
}
