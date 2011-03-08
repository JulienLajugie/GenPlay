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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import yu.einstein.gdp2.gui.action.SNPListTrack.SLAFilterRatio;
import yu.einstein.gdp2.gui.action.SNPListTrack.SLAFilterThreshold;
import yu.einstein.gdp2.gui.action.SNPListTrack.SLAFindNext;
import yu.einstein.gdp2.gui.action.SNPListTrack.SLAFindPrevious;
import yu.einstein.gdp2.gui.action.SNPListTrack.SLARemoveSNPsNotInGenes;
import yu.einstein.gdp2.gui.track.SNPListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * A popup menu for a {@link SNPListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SNPListTrackMenu extends TrackMenu {

	private static final long serialVersionUID = -4797259442922136696L; // generated ID
	
	private final JMenu		jmOperation;				// category operation
	
	private final JMenuItem jmiFilterRatio;				// menu item filter SNP list based on the ratio 1st base / 2nd base
	private final JMenuItem jmiFilterThreshold;			// menu item filter SNP list
	private final JMenuItem jmiFindNext;				// menu item find next SNP
	private final JMenuItem jmiFindPrevious;			// menu item find previous SNP
	private final JMenuItem jmiRemoveSNPsNotInGenes;	// menu item remove SNPs not in genes
	
	
	/**
	 * Creates an instance of a {@link SNPListTrackMenu}
	 * @param tl {@link TrackList}
	 */
	public SNPListTrackMenu(TrackList tl) {
		super(tl);
		
		jmOperation = new JMenu("Operation");
		
		jmiFilterRatio = new JMenuItem(actionMap.get(SLAFilterRatio.ACTION_KEY));
		jmiFilterThreshold = new JMenuItem(actionMap.get(SLAFilterThreshold.ACTION_KEY));
		jmiFindNext = new JMenuItem(actionMap.get(SLAFindNext.ACTION_KEY));
		jmiFindPrevious = new JMenuItem(actionMap.get(SLAFindPrevious.ACTION_KEY));
		jmiRemoveSNPsNotInGenes = new JMenuItem(actionMap.get(SLARemoveSNPsNotInGenes.ACTION_KEY));
		
		jmOperation.add(jmiFindNext);
		jmOperation.add(jmiFindPrevious);
		jmOperation.add(jmiFilterThreshold);
		jmOperation.add(jmiFilterRatio);
		jmOperation.add(jmiRemoveSNPsNotInGenes);
		
		add(jmOperation, 0);
		add(new Separator(), 1);
	}
}
