/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenuItem;

import yu.einstein.gdp2.gui.action.scoredTrack.STASetYAxis;
import yu.einstein.gdp2.gui.track.ScoredTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * Abstract class. Popup menus for a {@link ScoredTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class ScoredTrackMenu extends TrackMenu {
	
	private static final long serialVersionUID = -1426149345513389079L; // generated ID
	private final JMenuItem		jmiSetYAxis; // menu set maximum and minimum score
	
	
	/**
	 * Creates an instance of {@link ScoredTrackMenu}
	 */
	public ScoredTrackMenu(TrackList tl) {
		super(tl);		
		jmiSetYAxis= new JMenuItem(actionMap.get(STASetYAxis.ACTION_KEY));		
		addSeparator();	
		add(jmiSetYAxis);
	}
}
