/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenuItem;

import yu.einstein.gdp2.gui.action.curveTrack.AppearanceAction;
import yu.einstein.gdp2.gui.action.curveTrack.SetYAxisAction;
import yu.einstein.gdp2.gui.track.CurveTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;

/**
 * Abstract class of the popup menus for a {@link CurveTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveTrackMenu extends TrackMenu {

	private static final long serialVersionUID = -767811267010609433L; // generated ID

	private final JMenuItem 	jmiAppearance;			// menu appearance
	private final JMenuItem		jmiSetYAxis;			// menu set maximum and minimum score
	
	
	/**
	 * Constructor
	 */
	public CurveTrackMenu(TrackList tl) {
		super(tl);		
		jmiAppearance= new JMenuItem(actionMap.get(AppearanceAction.ACTION_KEY));
		jmiSetYAxis= new JMenuItem(actionMap.get(SetYAxisAction.ACTION_KEY));		
		addSeparator();
		add(jmiAppearance);
		addSeparator();
		add(jmiSetYAxis);
		addSeparator();		
	}
}
