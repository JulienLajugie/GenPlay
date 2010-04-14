/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.arrayList;

import java.util.List;

import yu.einstein.gdp2.exception.CompressionException;


/**
 * Interface defining the method of the compressible {@link List}.
 * @author Julien Lajugie
 * @version 0.1
 */
public interface CompressibleList {

	
	/**
	 * Compresses the data of the list
	 * @throws CompressionException 
	 */
	public void compress() throws  CompressionException ;
	
	
	/**
	 * Uncompresses the data of the list
	 * @throws CompressionException 
	 */
	public void uncompress() throws  CompressionException;
	
	
	/**
	 * @return true if the list is compressed. False otherwise.
	 */
	public boolean isCompressed();
}
