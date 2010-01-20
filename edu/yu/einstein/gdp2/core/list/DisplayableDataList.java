/** 
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list;

import yu.einstein.gdp2.core.GenomeWindow;

/**
 * Interface to implement to generate a list of data displayable on the screen
 * @author Julien Lajugie
 * @version 0.1
 * @param <T> Type of displayable data
 */
public interface DisplayableDataList<T> {

	/**
	 * @param genomeWindow {@link GenomeWindow} to display
	 * @param xRatio xRatio on the screen (ie ratio between the number of pixel and the number of base to display) 
	 * @return a data list adapted to the screen resolution
	 */
	public T getFittedData(GenomeWindow genomeWindow, double xRatio);
}
