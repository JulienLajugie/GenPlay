/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.enums;


/**
 * A type of island result
 * @author Nicolas Fourel
 * @version 0.1
 */
public enum IslandResultType {
	
	/**
	 * Value will be the original value of a window
	 */	
	FILTERED ("Filtered"),
	/**
	 * Value will be the score of an island
	 */
	IFSCORE ("Island Finder Score"),
	/**
	 * Value will be the maximum of the island
	 */	
	SUMMIT ("Island Summit");	
	
	
	private final String name; // String representing the filter 
	
	
	/**
	 * Private constructor. Creates an instance of {@link IslandResultType}
	 * @param name
	 */
	private IslandResultType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
