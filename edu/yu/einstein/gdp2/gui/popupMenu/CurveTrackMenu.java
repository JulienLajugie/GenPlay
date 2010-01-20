/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenuItem;

import yu.einstein.gdp2.gui.track.CurveTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.action.curve.AppearanceAction;
import yu.einstein.gdp2.gui.trackList.action.curve.ScoreMaxAction;
import yu.einstein.gdp2.gui.trackList.action.curve.ScoreMinAction;

/**
 * Abstract class of the popup menus for a {@link CurveTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveTrackMenu extends TrackMenu {

	private static final long serialVersionUID = -767811267010609433L; // generated ID

	private final JMenuItem 	jmiAppearance;			// menu appearance
	private final JMenuItem		jmiScoreMin;			// menu set minimum score
	private final JMenuItem		jmiScoreMax;			// menu set maximum score
	
	
	/**
	 * Constructor
	 */
	public CurveTrackMenu(TrackList tl) {
		super(tl);
		
		jmiAppearance= new JMenuItem(actionMap.get(AppearanceAction.ACTION_KEY));
		jmiScoreMin= new JMenuItem(actionMap.get(ScoreMinAction.ACTION_KEY));		
		jmiScoreMax= new JMenuItem(actionMap.get(ScoreMaxAction.ACTION_KEY));
		
		addSeparator();
		add(jmiAppearance);
		addSeparator();
		add(jmiScoreMin);
		add(jmiScoreMax);
		addSeparator();		
	}
}
