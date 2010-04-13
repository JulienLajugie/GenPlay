/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.arrayList;

import java.io.IOException;
import java.util.List;


/**
 * Interface defining the method of the compressible {@link List}.
 * @author Julien Lajugie
 * @version 0.1
 */
public interface CompressibleList {

	
	/**
	 * Compresses the data of the list
	 * @throws IOException 
	 */
	public void compress() throws IOException;
	
	
	/**
	 * Uncompresses the data of the list
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void uncompress() throws IOException, ClassNotFoundException;
	
	
	/**
	 * @return true if the list is compressed. False otherwise.
	 */
	public boolean isCompressed();
}
