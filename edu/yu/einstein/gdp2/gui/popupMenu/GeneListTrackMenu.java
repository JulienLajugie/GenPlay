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

import yu.einstein.gdp2.gui.action.allTrack.ATASave;
import yu.einstein.gdp2.gui.action.geneListTrack.GLADistanceCalculator;
import yu.einstein.gdp2.gui.action.geneListTrack.GLAExtractExons;
import yu.einstein.gdp2.gui.action.geneListTrack.GLAExtractInterval;
import yu.einstein.gdp2.gui.action.geneListTrack.GLAGeneRenamer;
import yu.einstein.gdp2.gui.action.geneListTrack.GLAScoreExons;
import yu.einstein.gdp2.gui.action.geneListTrack.GLASearchGene;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * A popup menu for a {@link GeneListTrack}
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public final class GeneListTrackMenu extends TrackMenu {

	private static final long serialVersionUID = -7024046901324869134L; // generated ID
	
	private final JMenu		jmOperation;					// category operation
	
	private final JMenuItem	jmiDistanceCalculator;	// distance Calculator menu
	private final JMenuItem jmiExtractExons;		// extract exons menu
	private final JMenuItem jmiExtractInterval;		// extract interval menu
	private final JMenuItem jmiRenameGenes;			// rename genes menu
	private final JMenuItem jmiSaveGeneTrack;		// save the gene track
	private final JMenuItem jmiScoreExons;			// save the exons of the genelist
	private final JMenuItem jmiSearchGene;			// search gene menu

	
	/**
	 * Creates an instance of a {@link GeneListTrackMenu}
	 * @param tl {@link TrackList}
	 */
	public GeneListTrackMenu(TrackList tl) {
		super(tl);

		jmOperation = new JMenu("Operation");
		
		jmiDistanceCalculator = new JMenuItem(actionMap.get(GLADistanceCalculator.ACTION_KEY));
		jmiExtractExons = new JMenuItem(actionMap.get(GLAExtractExons.ACTION_KEY));
		jmiExtractInterval = new JMenuItem(actionMap.get(GLAExtractInterval.ACTION_KEY));
		jmiRenameGenes = new JMenuItem(actionMap.get(GLAGeneRenamer.ACTION_KEY));
		jmiSaveGeneTrack = new JMenuItem(actionMap.get(ATASave.ACTION_KEY));
		jmiScoreExons = new JMenuItem(actionMap.get(GLAScoreExons.ACTION_KEY));
		jmiSearchGene = new JMenuItem(actionMap.get(GLASearchGene.ACTION_KEY));
			
		jmOperation.add(jmiSearchGene);
		jmOperation.add(jmiExtractInterval);
		jmOperation.add(jmiExtractExons);
		jmOperation.add(jmiScoreExons);
		jmOperation.add(jmiRenameGenes);
		jmOperation.add(jmiDistanceCalculator);
		
		add(jmOperation, 0);
		add(new Separator(), 1);

		add(jmiSaveGeneTrack, 11);
	}
}
