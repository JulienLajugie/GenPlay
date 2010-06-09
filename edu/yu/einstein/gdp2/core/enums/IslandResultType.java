/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.enums;


/**
 * A type of filter
 * @author Julien Lajugie
 * @version 0.1
 */
public enum IslandResultType {
	
	/**
	 * Value will be fixed
	 */
	CONSTANT ("Constant"),
	/**
	 * Value will be the score of a window
	 */	
	WINDOWSCORE ("Window Score"),
	/**
	 * Value will be the score of a window
	 */
	ISLANDSCORE ("Island Score"),
	/**
	 * Value will be the average of the island window score
	 */
	ISLANDSCOREAVERAGE ("Score Island Average");

	
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
