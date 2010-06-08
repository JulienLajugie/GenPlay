/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action;

import javax.swing.AbstractAction;
import javax.swing.JRootPane;

import yu.einstein.gdp2.gui.mainFrame.MainFrame;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * Abstract class. Represents an action on a TrackList
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class TrackListAction extends AbstractAction {

	private static final long serialVersionUID = 1383058897700926018L; 				// generated ID

	
	/**
	 * Constructor
	 */
	public TrackListAction() {
		super(); 
	}
	
	
	/**
	 * @return the {@link JRootPane} of the {@link TrackList}
	 */
	protected JRootPane getRootPane() {
		return MainFrame.getInstance().getTrackList().getRootPane();
	}
	
	
	/**
	 * Shortcut for MainFrame.getInstance().getTrackList()
	 * @return the track list of the project
	 */
	protected TrackList getTrackList() {
		return MainFrame.getInstance().getTrackList();
	}
}
