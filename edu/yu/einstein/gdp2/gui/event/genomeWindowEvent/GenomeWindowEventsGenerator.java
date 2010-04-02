/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.event.genomeWindowEvent;


/**
 * Should be Implemented by objects generating {@link GenomeWindowEvent}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface GenomeWindowEventsGenerator {
	
	/**
	 * Adds a {@link GenomeWindowListener} to the listener list
	 * @param genomeWindowListener {@link GenomeWindowListener} to add
	 */
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener);
	

	/**
	 * @return an array containing all the {@link GenomeWindowListener} of the current instance
	 */
	public GenomeWindowListener[] getGenomeWindowListeners();
	
	
	/**
	 * Removes a {@link GenomeWindowListener} from the listener list
	 * @param genomeWindowListener {@link GenomeWindowListener} to remove
	 */
	public void removeGenomeWindowListener(GenomeWindowListener genomeWindowListener);
}
