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
	FIVE ("+"),
	THREE ("-");
	
	
	private final String 						symbol;		// symbol representing the strand
	private static final Map<String, Strand> 	lookup = 
		new HashMap<String, Strand>();						// Map associating a Strand symbol to a Strand

	
	/**
	 * Fills the Map with all the different values of Strand
	 */
	static {
		for(Strand s : EnumSet.allOf(Strand.class))
			lookup.put(s.symbol, s);
	}

	
	/**
	 * Private constructor. Creates an instance of {@link Strand}
	 * @param symbol String representing the strand
	 */
	private Strand(String symbol) {
		this.symbol = symbol;
	}


	/**
	 * @return "+" for strand 5', "-" for strand 3'.
	 */
	@Override
	public String toString(){
		return symbol;		
	}


	/**
	 * @param strandString "+" or "-"
	 * @return the {@link Strand} associated to the parameter. Null if none.
	 */
	public static Strand get(String strandString) {
		return lookup.get(strandString);
	}
}
