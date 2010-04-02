/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.event.repaintEvent;


/**
 * Should be Implemented by objects generating {@link RepaintEvent}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface RepaintEventsGenerator {
	
	/**
	 * Adds a {@link RepaintListener} to the listener list
	 * @param repaintListener {@link RepaintListener} to add
	 */
	public void addRepaintListener(RepaintListener repaintListener);
	

	/**
	 * @return an array containing all the {@link RepaintListener} of the current instance
	 */
	public RepaintListener[] getRepaintListeners();
	
	
	/**
	 * Removes a {@link RepaintListener} from the listener list
	 * @param repaintListener {@link RepaintListener} to remove
	 */
	public void removeRepaintListener(RepaintListener repaintListener);
}
