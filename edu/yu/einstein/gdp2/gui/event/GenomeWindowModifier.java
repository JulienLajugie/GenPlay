/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.event;


/**
 * Should be Implemented by objects generating {@link GenomeWindowEvent}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface GenomeWindowModifier {
	
	/**
	 * Adds a {@link GenomeWindowListener} to the listener list.
	 * @param genomeWindowListener {@link GenomeWindowListener} to add
	 */
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener);
}
