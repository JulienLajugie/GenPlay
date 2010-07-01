/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.enums;


/**
 * Enumeration representing the different base available for the logarithm operations
 * @author Julien Lajugie
 * @version 0.1
 */
public enum LogBase {
	
	BASE_2 ("Binary Logarithm (base 2)", 2d),
	BASE_E ("Natural Logarithm (base e)", Math.E),
	BASE_10 ("Common Logaritm (base 10)", 10d);
	
	
	private final String name;		// name of the base
	private final double baseValue;	// value of the base
	
	
	/**
	 * Privates constructor. Creates an instance of a {@link LogBase}
	 * @param name name of the logarithm
	 * @param baseValue value of the base
	 */
	private LogBase(String name, Double baseValue) {
		this.name = name;
		this.baseValue = baseValue;
	}
	
	
	/**
	 * @return the name of the logarithm
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * @return the value of the base
	 */
	public double getValue() {
		return baseValue;
	}
	
	
	@Override
	public String toString() {
		return name;
	}
}
