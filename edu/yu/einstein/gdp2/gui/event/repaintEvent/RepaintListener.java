/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.event.repaintEvent;



/**
 * The listener interface for receiving {@link RepaintEvent}.
 * The class that is interested in processing a {@link RepaintEvent} implements this interface.
 * The listener object created from that class is then registered with a component using the component's addRepaintListener method. 
 * A {@link RepaintEvent} is generated when a component is repainted  
 * @author Julien Lajugie
 * @version 0.1
 */
public interface RepaintListener {

	
	/**
	 * Invoked when the component is repainted
	 * @param evt {@link RepaintEvent}
	 */
	public abstract void componentRepainted(RepaintEvent evt);
}
