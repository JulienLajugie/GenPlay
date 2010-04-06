/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenuItem;

import yu.einstein.gdp2.gui.action.curveTrack.AppearanceAction;
import yu.einstein.gdp2.gui.track.CurveTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * Abstract class. Popup menus for a {@link CurveTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveTrackMenu extends ScoredTrackMenu {

	private static final long serialVersionUID = -767811267010609433L; // generated ID
	private final JMenuItem jmiAppearance;	// menu appearance

		
	/**
	 * Creates an instance of {@link CurveTrackMenu}
	 */
	public CurveTrackMenu(TrackList tl) {
		super(tl);		
		jmiAppearance= new JMenuItem(actionMap.get(AppearanceAction.ACTION_KEY));
		addSeparator();
		add(jmiAppearance);
	}
}
