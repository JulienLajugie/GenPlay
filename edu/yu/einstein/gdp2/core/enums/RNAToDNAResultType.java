/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.enums;


/**
 * Enumeration representing the different results returned by an RNA to DNA operation
 * @author Julien Lajugie
 * @version 0.1
 */
public enum RNAToDNAResultType {
	GDP ("GDP Output File"),
	BGR ("BGR Output File"),
	BGR_WITH_EXTRA_FIELDS ("BGR Output File With Extra Fields");
	
	private final String 	description;	// description of the result type
	
	/**
	 * Private constructor. Creates an instance of {@link RNAToDNAResultType}
	 * @param description description of the result type
	 */
	private RNAToDNAResultType(String description) {
		this.description = description;
	}

	
	/**
	 * @return the description of the result type
	 */
	public String getDescription() {
		return description;
	}

	
	@Override
	public String toString() {
		return description;
	}
}
