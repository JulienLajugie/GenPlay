/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.event;

import yu.einstein.gdp2.core.GenomeWindow;


/**
 * The listener interface for receiving {@link GenomeWindowEvent}.
 * The class that is interested in processing a {@link GenomeWindowEvent} implements this interface.
 * The listener object created from that class is then registered with a component using the component's addGenomeWindowListener method. 
 * A {@link GenomeWindowEvent} is generated when a {@link GenomeWindow} is modified.  
 * @author Julien Lajugie
 * @version 0.1
 */
public interface GenomeWindowListener {

	
	/**
	 * Invoked when the {@link GenomeWindow} is modified 
	 * @param evt {@link GenomeWindowEvent}
	 */
	public abstract void genomeWindowChanged(GenomeWindowEvent evt);
}
