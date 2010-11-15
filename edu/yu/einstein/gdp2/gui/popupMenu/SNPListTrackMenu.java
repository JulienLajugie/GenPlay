/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenuItem;

import yu.einstein.gdp2.gui.action.SNPListTrack.SLAFindNext;
import yu.einstein.gdp2.gui.action.SNPListTrack.SLAFindPrevious;
import yu.einstein.gdp2.gui.track.SNPListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * A popup menu for a {@link SNPListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SNPListTrackMenu extends TrackMenu {

	private static final long serialVersionUID = -4797259442922136696L; // generated ID
	private final JMenuItem jmiFindNext;		// menu item find next SNP
	private final JMenuItem jmiFindPrevious;	// menu item find previous SNP
	
	/**
	 * Creates an instance of a {@link SNPListTrackMenu}
	 * @param tl {@link TrackList}
	 */
	public SNPListTrackMenu(TrackList tl) {
		super(tl);
		jmiFindNext = new JMenuItem(actionMap.get(SLAFindNext.ACTION_KEY));
		jmiFindPrevious = new JMenuItem(actionMap.get(SLAFindPrevious.ACTION_KEY));
		
		addSeparator();
		add(jmiFindNext);
		add(jmiFindPrevious);		
	}
}
