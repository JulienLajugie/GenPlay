/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.DAS;


/**
 * A DAS server
 * @author Julien Lajugie
 * @version 0.1
 */
public class DASServer {
	
	private String URL;			// URL of the server
	private String name;		// name of the server
	
	
	@Override
	public String toString() {
		return name;
	}
	
	
	/**
	 * Creates an instance of {@link DASServer}
	 */
	public DASServer() {
		super();
	}

	
	/**
	 * @param uRL the uRL to set
	 */
	public void setURL(String uRL) {
		URL = uRL;
	}

	
	/**
	 * @return the uRL
	 */
	public String getURL() {
		return URL;
	}

	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
