/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuEvent;

import yu.einstein.gdp2.gui.action.curveTrack.CTAAppearance;
import yu.einstein.gdp2.gui.action.curveTrack.CTAHistory;
import yu.einstein.gdp2.gui.action.curveTrack.CTARedo;
import yu.einstein.gdp2.gui.action.curveTrack.CTAReset;
import yu.einstein.gdp2.gui.action.curveTrack.CTAUndo;
import yu.einstein.gdp2.gui.track.CurveTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * Abstract class. Popup menus for a {@link CurveTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class CurveTrackMenu extends ScoredTrackMenu {

	private static final long serialVersionUID = -767811267010609433L; // generated ID
	private final JMenuItem 	jmiAppearance;	// menu appearance
	private final JMenuItem		jmiHistory;		// menu show history
	private final JMenuItem		jmiRedo;		// menu redo last action
	private final JMenuItem		jmiReset;		// menu reset track
	private final JMenuItem		jmiUndo;		// menu undo last action
	
		
	/**
	 * Creates an instance of {@link CurveTrackMenu}
	 */
	public CurveTrackMenu(TrackList tl) {
		super(tl);		
		jmiAppearance= new JMenuItem(actionMap.get(CTAAppearance.ACTION_KEY));
		jmiHistory = new JMenuItem(actionMap.get(CTAHistory.ACTION_KEY));
		jmiRedo = new JMenuItem(actionMap.get(CTARedo.ACTION_KEY));
		jmiReset = new JMenuItem(actionMap.get(CTAReset.ACTION_KEY));
		jmiUndo = new JMenuItem(actionMap.get(CTAUndo.ACTION_KEY));
		add(jmiAppearance);
		addSeparator();
		add(jmiUndo);
		add(jmiRedo);
		add(jmiReset);
		add(jmiHistory);
	}
	
	
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
		super.popupMenuWillBecomeVisible(arg0);
		jmiUndo.setEnabled(((CurveTrack<?>)trackList.getSelectedTrack()).isUndoable());
		jmiRedo.setEnabled(((CurveTrack<?>)trackList.getSelectedTrack()).isRedoable());
		jmiReset.setEnabled(((CurveTrack<?>)trackList.getSelectedTrack()).isResetable());
	}
}
