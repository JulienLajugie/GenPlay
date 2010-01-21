/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.enums;

/**
 * Type of a graph
 * @author Julien Lajugie
 * @version 0.1
 */
public enum GraphicsType {
	CURVE ("curve"),
	POINTS ("points"),
	BAR		("bar"),
	DENSE	("dense");


	private final String name; // String representing the type of graphics 


	/**
	 * Private constructor. Creates an instance of {@link GraphicsType}
	 * @param name
	 */
	private GraphicsType(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return name;
	}
}
