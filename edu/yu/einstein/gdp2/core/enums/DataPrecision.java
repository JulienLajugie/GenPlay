/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.enums;

import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * Enumeration representing a precision of data for a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public enum DataPrecision {
	
	PRECISION_1BIT ("1-Bit"),
	PRECISION_8BIT ("8-Bit"),
	PRECISION_16BIT ("16-Bit"),
	PRECISION_32BIT ("32-Bit"),
	PRECISION_64BIT ("64-Bit");
	
	private final String name; // name of the precision
	
	
	/**
	 * Private constructor. Creates an instance of {@link DataPrecision}
	 * @param name name of the precision
	 */
	private DataPrecision(String name) {
		this.name = name;
	}
	
	
	public String toString() {
		return name;
	}
}
