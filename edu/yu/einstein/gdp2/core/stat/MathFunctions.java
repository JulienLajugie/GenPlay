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
	
	/**
	 * This method implements the linear interpolation.
	 * 
	 * @param	xa	x coordinate of the point A
	 * @param	ya	y coordinate of the point A
	 * @param	xb	x coordinate of the point B
	 * @param	yb	y coordinate of the point B
	 * @param	x	x coordinate of the sought point
	 * @return	y	y coordinate of the sought point
	 */
	public static double linearInterpolation (double xa, double ya, double xb, double yb, double x) {
		double y;
		y = (ya - yb) / (xa-xb);
		y *= x;
		y += (xa * yb - xb * ya) / (xa - xb);
		return y;
	}
	
	/**
	 * This method says if the value is an integer seeking the 10 first digits.
	 * 
	 * @param value	digital value
	 * @return		true or false
	 */
	public static boolean isInteger (double value) {
		if (getDigits(value, 10) == 0.0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This method find the n first digits of a value
	 * 
	 * @param value	value containing digits (or not)
	 * @param n		number of digits wanted
	 * @return		the n first digits
	 */
	public static Double getDigits (double value, int n) {
		Double digits = Math.round(value * Math.pow(10, n)) - Math.round(value - 0.5d) * Math.pow(10, n);
		digits /= Math.pow(10, n);
		return digits;
	}

}