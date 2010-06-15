/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenuItem;

import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAGenerateBinList;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * A popup menu for a {@link SCWListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWListTrackMenu extends CurveTrackMenu {

	private static final long serialVersionUID = 3249783097749893180L; // generated ID
	
	private final JMenuItem jmiGenerateBinList;			// menu generate a BinList track
	
	
	/**
	 * Creates an instance of a {@link SCWListTrackMenu}
	 * @param tl {@link TrackList}
	 */
	public SCWListTrackMenu(TrackList tl) {
		super(tl);		
		jmiGenerateBinList = new JMenuItem(actionMap.get(SCWLAGenerateBinList.ACTION_KEY));		
		add(jmiGenerateBinList);
	}
}
