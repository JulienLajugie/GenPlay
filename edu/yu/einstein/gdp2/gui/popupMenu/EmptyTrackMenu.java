/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenuItem;

import yu.einstein.gdp2.gui.action.emptyTrack.LoadBinListTrackAction;
import yu.einstein.gdp2.gui.action.emptyTrack.LoadGeneListTrackAction;
import yu.einstein.gdp2.gui.action.emptyTrack.LoadRepeatFamilyListTrackAction;
import yu.einstein.gdp2.gui.action.emptyTrack.LoadSCWListTrackAction;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * A menu for an empty track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class EmptyTrackMenu extends TrackMenu {

	private static final long serialVersionUID = -131776080496747414L; // Generated ID
	private final JMenuItem jmiLoadSCWLtTrack;						// menu load ScoredChromosomeWindowList track
	private final JMenuItem jmiLoadBinListTrack;					// menu load BinList track
	private final JMenuItem	jmiLoadGeneListTrack;					// menu load GeneList track
	private final JMenuItem jmiLoadRepeatFamilyListTrack;			// menu load RepeatFamilyList track
	
	
	/**
	 * Creates an instance of an {@link EmptyTrackMenu}
	 * @param tl {@link TrackList} where the menu popped up
	 */
	public EmptyTrackMenu(TrackList tl) {
		super(tl);
	
		jmiLoadSCWLtTrack = new JMenuItem(actionMap.get(LoadSCWListTrackAction.ACTION_KEY));
		jmiLoadBinListTrack = new JMenuItem(actionMap.get(LoadBinListTrackAction.ACTION_KEY));
		jmiLoadGeneListTrack = new JMenuItem(actionMap.get(LoadGeneListTrackAction.ACTION_KEY));
		jmiLoadRepeatFamilyListTrack = new JMenuItem(actionMap.get(LoadRepeatFamilyListTrackAction.ACTION_KEY));
		
		addSeparator();
		add(jmiLoadSCWLtTrack);
		add(jmiLoadBinListTrack);
		add(jmiLoadGeneListTrack);
		add(jmiLoadRepeatFamilyListTrack);		
	}
}
