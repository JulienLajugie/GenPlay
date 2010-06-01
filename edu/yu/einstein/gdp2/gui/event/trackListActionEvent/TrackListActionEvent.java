/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.event.trackListActionEvent;

import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * The {@link TrackListActionEvent} event emitted when an operation on a {@link TrackList} is processed
 * @author Julien Lajugie
 * @version 0.1
 */
public class TrackListActionEvent {
	private final TrackList trackList;			// track list
	private final String operationDescription;	// description of the operation
	
	
	/**
	 * Creates an instance of {@link TrackListActionEvent} 
	 * @param trackList trackList
	 * @param operationDescription description of the operation
	 */
	public TrackListActionEvent(TrackList trackList, String operationDescription) {
		this.trackList = trackList;
		this.operationDescription = operationDescription;
	}


	/**
	 * @return the track list
	 */
	public TrackList getTrackList() {
		return trackList;
	}


	/**
	 * @return the description of the action
	 */
	public String getActionDescription() {
		return operationDescription;
	}
}
