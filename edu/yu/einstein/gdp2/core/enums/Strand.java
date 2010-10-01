/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Julien Lajugie
 * @version 0.1
 * The Strand class represents a strand.
 */
public enum Strand {
	FIVE ('+', 'F'),
	THREE ('-', 'R');
	
	
	private final char 							symbol1;		// symbol representing the strand (+ or -)
	private final char 							symbol2;		// symbol representing the strand (F or R)
	private static final Map<Character, Strand> LOOKUP1 = 
		new HashMap<Character, Strand>();						// Map associating the Strand symbol 1 to a Strand
	private static final Map<Character, Strand> LOOKUP2 = 
		new HashMap<Character, Strand>();						// Map associating the Strand symbol 2 to a Strand
	
	
	/**
	 * Fills the Map with all the different values of Strand
	 */
	static {
		for(Strand s : EnumSet.allOf(Strand.class)) {
			LOOKUP1.put(s.symbol1, s);
			LOOKUP2.put(s.symbol2, s);
		}
	}

	
	/**
	 * Private constructor. Creates an instance of {@link Strand}
	 * @param symbol1 character representing the strand (+ or -)
	 * @param symbol2 character representing the strand (F or R)
	 */
	private Strand(char symbol1, char symbol2) {
		this.symbol1 = symbol1;
		this.symbol2 = symbol2;
	}


	/**
	 * @return "+" for strand 5', "-" for strand 3'.
	 */
	@Override
	public String toString(){
		return Character.toString(symbol1);		
	}


	/**
	 * @param strandSymbol '+' or '-' or 'F' or 'R'  
	 * @return the {@link Strand} associated to the parameter. Null if none.
	 */
	public static Strand get(char strandSymbol) {
		Strand result = LOOKUP1.get(strandSymbol);
		if (result != null) {
			return result;
		} else {
			return LOOKUP2.get(strandSymbol);			
		}
	}
}
