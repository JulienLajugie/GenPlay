/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.stat;

/**
 * Poisson
 * This class gather some methods to calculate mathematical functions.
 * These methods are developed for IslandFinder class.
 * @author Nicolas Fourel
 */
public class MathFunctions {
	
	/**
	 * unlogValue method
	 * This method return the decimal value from a logarithm value 
	 * 
	 * @param value	the logarithm value
	 * @return		the decimal value
	 */
	public static double unlogValue (double value) {
		return Math.pow(10.0, value);
	}

}
