/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.writer;


/**
 * Interface implemented by the data writers (BinListWriter, GeneListWriter...)
 * @author Julien Lajugie
 * @version 0.1
 */
public interface Writer {
	
	/**
	 * Writes data in an output file
	 * @throws Exception
	 */
	public void write() throws Exception;
}
