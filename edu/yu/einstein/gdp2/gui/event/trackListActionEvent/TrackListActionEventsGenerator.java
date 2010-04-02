/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.event.trackListActionEvent;


/**
 * Should be Implemented by objects generating {@link TrackListActionEvent}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface TrackListActionEventsGenerator {

	
	/**
	 * Adds a {@link TrackListActionListener} to the listener list
	 * @param trackListActionListener {@link TrackListActionListener} to add
	 */
	public void addTrackListActionListener(TrackListActionListener trackListActionListener);
	

	/**
	 * @return an array containing all the {@link TrackListActionListener} of the current instance
	 */
	public TrackListActionListener[] getOperationOnTrackListener();
	
	
	/**
	 * Removes a {@link TrackListActionListener} from the listener list
	 * @param trackListActionListener {@link TrackListActionListener} to remove
	 */
	public void removeTrackListActionListener(TrackListActionListener trackListActionListener);
}
