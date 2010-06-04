/**
 * @author Alexander Golec
 * @version 0.1
 */
package yu.einstein.gdp2.core.stat;

import yu.einstein.gdp2.exception.*;
import java.util.Vector;
import java.lang.Double;

/**
 * @author Alexander Golec
 * @version 0.1
 * The Poisson class represents a Poisson distribution with parameter lamba. 
 */

public class Poisson_old {
	// The threshold probability which determines how many poisson values are calculated. 
	private static double thresholdProb = 0.000001;
	// The Vector of Poisson values. 
	private Vector<Double> values;
	// The lambda value for which this variable was computed. 
	private double lambda;
	
	/**
	 * Creates an instance of {@link Poisson_old} with parameter lambda. L values are calculated 
	 * until the resulting probability shrinks below the static thresholdProb variable. 
	 * @param lambda the lambda value to assign. 
	 */
	public Poisson_old (double lambda) throws PoissonInvalidParameterException {
		if (thresholdProb == (double) 0) {
			throw new PoissonInvalidParameterException ("thresholdProbability is equal to zero. ");
		}
		
		this.lambda = lambda;
		this.values = new Vector<Double>();
		
		// lValue = 0
		double current = Math.exp(-lambda);
		for (int lValue = 1; current > thresholdProb; lValue++) {
			// If this loops continues, then add the current value. 
			this.values.add(Double.valueOf(current));
			
			// This iteratively implements the factorial in the denominator over lValue. 
			current *= (lambda/lValue);
		}
	}
	
	/**
	 * Returns a double representing the value of the Poisson distribution for 
	 * the entered L value. 
	 * @param lValue the lValue at which to calculate the Poisson. 
	 */
	public double evaluate(int lValue) throws PoissonInvalidParameterException {
		if (lValue < 0) {
			throw new PoissonInvalidParameterException ("Entered negative L value");
		} 
		
		// If we already have the value stored, then return it. 
		if (lValue < values.size()) {
			return values.get(lValue).doubleValue();
		}
		// If the value is not stored, then grab the last one, and continue calculating on that. 
		else {
			double current = values.get(values.size() - 1);
			
			for (int i = values.size(); i <= lValue; i++) {
				current *= (lambda/i);
			}
			
			return current;
		}
	}
	
	/**
	 * Returns a double representing the value of the Poisson distribution for 
	 * the entered L value and the entered lambda value. 
	 * @param lValue the lValue at which to calculate the Poisson. 
	 * @param lambda the lambda value. 
	 */
	public static double evaulateQuickly(int lValue, double lambda) {
		// At lValue == 0, the for loop gets skipped, and only this is returned. 
		double current = Math.exp(-lambda);
		
		for (int i = 1; i <= lValue; i++) {
			current *= (lambda/i);
		}
		
		return current;
	}
	
	/**
	 * Calculates the smallest integer l such that the the probability of there being l or more events 
	 * of the Poisson type is greater than a given probability. 
	 * @param prob the probability to which to calculate
	 */
	public int lValueDetermination(double prob) {
		double current = 1;
		
		int i = 0;
		try {
			// Keep subtracting probabilities from the total. At the point where the probability dips
			// below the given threshold value, quit. 
			for (i = 0; current > prob; i++) {
				current -= this.evaluate(i);
			}
		} catch (Exception e) {
			System.out.println(e);
			System.exit(-1);
		}
		
		return i - 1;
	}
	
	/**
	 * Set the threshold probability to which the Poisson is calculated. 
	 * @param threshold the threshold. 
	 */
	public static void setThresholdProb(double threshold) throws PoissonInvalidParameterException {
		if (threshold > 0) {
			thresholdProb = threshold;
		} else  {
			throw new PoissonInvalidParameterException ("Entered invalid threshold probability : " + threshold);
		}
	}
	
	public String toString() {
		return "Poisson : Lambda = " + lambda;
	}
	
	public static void test(int depth) {
		System.out.println("Lambda = 2.4");
		double lambda = 2.4;
		Poisson_old temp = null;
		try {
			temp = new Poisson_old(lambda);
		} catch (Exception e) {System.out.println(e);}
		try {
			double Ptotal = 0;
			double Qtotal = 0;
			for (int l = 0; l <= depth; l++) {
				System.out.println(l + "----------");
				System.out.println("From Poisson : ");
				double Pcur = temp.evaluate(l);
				Ptotal += Pcur;
				System.out.println(Pcur);
				System.out.println("Quickly : ");
				double Qcur = Poisson_old.evaulateQuickly(l, lambda);
				Qtotal += Qcur;
				System.out.println(Qcur);
				System.out.println("Difference : ");
				System.out.println((double)(Qtotal - Ptotal));
			}
			System.out.println("------------------------------------- > ");
			System.out.println("Total from Poisson = " + Ptotal);
			System.out.println("Total from Quick   = " + Qtotal);
			
			System.out.println("lValueDetermination for 1% probability : " + 
					temp.lValueDetermination(.01));
		} catch (Exception e) {System.out.println(e);}
	}
}