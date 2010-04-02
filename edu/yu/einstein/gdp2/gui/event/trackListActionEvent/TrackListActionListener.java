/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.event.trackListActionEvent;

import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * The listener interface for receiving {@link TrackListActionEvent}.
 * The class that is interested in processing an {@link TrackListActionEvent} implements this interface.
 * The listener object created from that class is then registered with a component using the component's add listener method. 
 * A {@link TrackListActionEventsGenerator} is generated when an action on a {@link TrackList} starts or ends.
 * @author Julien Lajugie
 * @version 0.1
 */
public interface TrackListActionListener {

	/**
	 * Invoked when an action starts 
	 * @param evt {@link TrackListActionEvent}
	 */
	public abstract void actionStarts(TrackListActionEvent evt);
	

	/**
	 * Invoked when an action ends 
	 * @param evt {@link TrackListActionEvent}
	 */
	public abstract void actionEnds(TrackListActionEvent evt);
}

