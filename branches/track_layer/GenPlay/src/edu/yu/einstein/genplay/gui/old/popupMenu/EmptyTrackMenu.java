/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.old.popupMenu;

import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuEvent;

import edu.yu.einstein.genplay.gui.old.action.emptyTrack.ETAGenerateMultiCurvesTrack;
import edu.yu.einstein.genplay.gui.old.action.emptyTrack.ETALoadBinListTrack;
import edu.yu.einstein.genplay.gui.old.action.emptyTrack.ETALoadFromDAS;
import edu.yu.einstein.genplay.gui.old.action.emptyTrack.ETALoadGeneListTrack;
import edu.yu.einstein.genplay.gui.old.action.emptyTrack.ETALoadNucleotideListTrack;
import edu.yu.einstein.genplay.gui.old.action.emptyTrack.ETALoadRepeatFamilyListTrack;
import edu.yu.einstein.genplay.gui.old.action.emptyTrack.ETALoadSCWListTrack;
import edu.yu.einstein.genplay.gui.old.action.emptyTrack.ETALoadSNPListTrack;
import edu.yu.einstein.genplay.gui.old.trackList.TrackList;



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
	private final JMenuItem jmiLoadNucleotideListTrack;				// menu load Sequence track
	private final JMenuItem jmiLoadSNPListTrack;					// menu load SNPList track
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
		jmiLoadSNPListTrack = new JMenuItem(actionMap.get(ETALoadSNPListTrack.ACTION_KEY));
		jmiLoadRepeatFamilyListTrack = new JMenuItem(actionMap.get(ETALoadRepeatFamilyListTrack.ACTION_KEY));
		jmiLoadFromDAS = new JMenuItem(actionMap.get(ETALoadFromDAS.ACTION_KEY));
		jmiGenerateMultiTrack = new JMenuItem(actionMap.get(ETAGenerateMultiCurvesTrack.ACTION_KEY));
		
		addSeparator();
		add(jmiLoadSCWLtTrack);
		add(jmiLoadBinListTrack);
		add(jmiLoadGeneListTrack);
		add(jmiLoadNucleotideListTrack);
		add(jmiLoadSNPListTrack);
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
