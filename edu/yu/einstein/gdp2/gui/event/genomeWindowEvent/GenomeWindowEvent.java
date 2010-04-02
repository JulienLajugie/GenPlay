/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.event.genomeWindowEvent;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.ChromosomeWindow;
import yu.einstein.gdp2.core.GenomeWindow;


/**
 * The {@link GenomeWindow} event emitted by a {@link GenomeWindowEventsGenerator} object.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GenomeWindowEvent {
	
	private final GenomeWindowEventsGenerator 	source;		// GenomeWindowEventsGenerator that emitted the event
	private final GenomeWindow 			oldWindow;	// old GenomeWindow
	private final GenomeWindow 			newWindow;	// new GenomeWindow
	
	
	/**
	 * Creates an instance of {@link GenomeWindowEvent}
	 * @param source {@link GenomeWindowEventsGenerator} that emitted this event
	 * @param oldWindow value of the {@link GenomeWindow} before changes
	 * @param newWindow value of the {@link GenomeWindow} after changes
	 */
	public GenomeWindowEvent(GenomeWindowEventsGenerator source, GenomeWindow oldWindow, GenomeWindow newWindow) {
		this.source = source;
		this.oldWindow = oldWindow;
		this.newWindow = newWindow;
	}


	/**
	 * @return the source
	 */
	public final GenomeWindowEventsGenerator getSource() {
		return source;
	}


	/**
	 * @return the oldWindow
	 */
	public final GenomeWindow getOldWindow() {
		return oldWindow;
	}


	/**
	 * @return the newWindow
	 */
	public final GenomeWindow getNewWindow() {
		return newWindow;
	}
	
	
	/**
	 * @return true if the size of the {@link GenomeWindow} changed
	 */
	public boolean didZoomChanged() {
		return oldWindow.getSize() != newWindow.getSize();
	}
	
	
	/**
	 * @return true if the {@link Chromosome} of the {@link GenomeWindow} changed
	 */
	public boolean didChromosomeChanged() {
		return !oldWindow.getChromosome().equals(newWindow.getChromosome());
	}
	
	
	/**
	 * @return true if the {@link ChromosomeWindow} of the {@link GenomeWindow} changed
	 */
	public boolean didChromosomeWindowChanged() {
		return !((ChromosomeWindow)oldWindow).equals((ChromosomeWindow)newWindow);
	}
	
}
