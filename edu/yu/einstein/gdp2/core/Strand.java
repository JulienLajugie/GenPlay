/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core;

/**
 * @author Julien Lajugie
 * @version 0.1
 * The Strand class represents a strand.
 */
public enum Strand {
	five,
	three;

	
	/**
	 * @return "+" for strand 5', "-" for strand 3'.
	 */
	@Override
	public String toString(){
		switch (this) {
			case five:
				return "+";
			case three:
				return "-";
			default:
				return null;				
		}
	}
	
	
	/**
	 * @param strandString "+" or "-"
	 * @return the {@link Strand} associated to the parameter. Null if none.
	 */
	public static Strand getStrand(String strandString) {
		if (strandString.equals("+")) {
			return Strand.five;
		} else if (strandString.equals("-")) {
			return Strand.three;
		}
		else return null;
	}
}
