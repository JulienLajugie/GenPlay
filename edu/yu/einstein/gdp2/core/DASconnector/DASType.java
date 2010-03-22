/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.DASconnector;


/**
 * An annotation type as described in the DAS 1.53 specifications:
 * <br/><a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 * @version 0.1
 */
public class DASType {
	private String ID;			// unique id for the annotation type 
	private String category;	// functional grouping to related types
	private String method;		// indicates the method (subtype) for the feature type 
	
	
	/**
	 * @return the iD
	 */
	public final String getID() {
		return ID;
	}
	
	
	/**
	 * @param iD the iD to set
	 */
	public final void setID(String iD) {
		ID = iD;
	}
	
	
	/**
	 * @return the category
	 */
	public final String getCategory() {
		return category;
	}
	
	
	/**
	 * @param category the category to set
	 */
	public final void setCategory(String category) {
		this.category = category;
	}
	
	
	/**
	 * @return the method
	 */
	public final String getMethod() {
		return method;
	}
	
	
	/**
	 * @param method the method to set
	 */
	public final void setMethod(String method) {
		this.method = method;
	}
}
