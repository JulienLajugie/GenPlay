/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuEvent;

import yu.einstein.gdp2.gui.action.emptyTrack.ETAGenerateMultiCurvesTrack;
import yu.einstein.gdp2.gui.action.emptyTrack.ETALoadBinListTrack;
import yu.einstein.gdp2.gui.action.emptyTrack.ETALoadFromDAS;
import yu.einstein.gdp2.gui.action.emptyTrack.ETALoadGeneListTrack;
import yu.einstein.gdp2.gui.action.emptyTrack.ETALoadNucleotideListTrack;
import yu.einstein.gdp2.gui.action.emptyTrack.ETALoadRepeatFamilyListTrack;
import yu.einstein.gdp2.gui.action.emptyTrack.ETALoadSCWListTrack;
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
	private final JMenuItem jmiLoadNucleotideListTrack;				// menu load Sequence track track
	private final JMenuItem jmiLoadRepeatFamilyListTrack;			// menu load RepeatFamilyList track
	private final JMenuItem jmiLoadFromDAS;							// menu load from DAS server
	private final JMenuItem jmiGenerateMultiTrack;					// menu generate multitrack
	
	/**
	 * Creates an instance of an {@link EmptyTrackMenu}
	 * @param tl {@link TrackList} where the menu popped up
	 */
	public EmptyTrackMenu(TrackList tl) {
		super(tl);
	
		jmiLoadSCWLtTrack = new JMenuItem(actionMap.get(ETALoadSCWListTrack.ACTION_KEY));
		jmiLoadBinListTrack = new JMenuItem(actionMap.get(ETALoadBinListTrack.ACTION_KEY));
		jmiLoadGeneListTrack = new JMenuItem(actionMap.get(ETALoadGeneListTrack.ACTION_KEY));
		jmiLoadNucleotideListTrack = new JMenuItem(actionMap.get(ETALoadNucleotideListTrack.ACTION_KEY));
		jmiLoadRepeatFamilyListTrack = new JMenuItem(actionMap.get(ETALoadRepeatFamilyListTrack.ACTION_KEY));
		jmiLoadFromDAS = new JMenuItem(actionMap.get(ETALoadFromDAS.ACTION_KEY));
		jmiGenerateMultiTrack = new JMenuItem(actionMap.get(ETAGenerateMultiCurvesTrack.ACTION_KEY));
		
		addSeparator();
		add(jmiLoadSCWLtTrack);
		add(jmiLoadBinListTrack);
		add(jmiLoadGeneListTrack);
		add(jmiLoadNucleotideListTrack);
		add(jmiLoadRepeatFamilyListTrack);
		add(jmiLoadFromDAS);
		add(jmiGenerateMultiTrack);
	}
	
	
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
		super.popupMenuWillBecomeVisible(arg0);
		// the generate multicurves track is enable only if there is more than one curve track loaded
		boolean moreThan1CurveTrackLoaded = (trackList.getCurveTracks() != null) && (trackList.getCurveTracks().length > 1);
		jmiGenerateMultiTrack.setEnabled(moreThan1CurveTrackLoaded);
	}
}
