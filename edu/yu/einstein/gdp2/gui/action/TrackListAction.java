/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action;

import javax.swing.AbstractAction;
import javax.swing.JRootPane;

import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * Abstract class. Represents an action on a TrackList
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class TrackListAction extends AbstractAction {

	private static final long serialVersionUID = 1383058897700926018L; // generated ID
	protected final TrackList trackList; // TrackList	

	
	/**
	 * Constructor
	 * @param trackList a {@link TrackList}
	 */
	public TrackListAction(TrackList trackList) {
		this.trackList = trackList; 


	}
	
	
	/**
	 * @return the {@link JRootPane} of the {@link TrackList}
	 */
	protected JRootPane getRootPane() {
		return trackList.getRootPane();
	}
}
