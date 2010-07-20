/**
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenuItem;

import yu.einstein.gdp2.gui.action.allTrack.ATASave;
import yu.einstein.gdp2.gui.action.geneListTrack.GLAExtractInterval;
import yu.einstein.gdp2.gui.action.geneListTrack.GLAGeneRenamer;
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

	private final JMenuItem saveGeneTrack;	// save the gene track
	private final JMenuItem searchGene;		// search gene menu
	private final JMenuItem extractInterval;// extract interval menu
	private final JMenuItem renameGenes;	// rename genes menu
	
	
	/**
	 * Creates an instance of a {@link GeneListTrackMenu}
	 * @param tl {@link TrackList}
	 */
	public GeneListTrackMenu(TrackList tl) {
		super(tl);
	
		saveGeneTrack = new JMenuItem(actionMap.get(ATASave.ACTION_KEY));
		searchGene = new JMenuItem(actionMap.get(GLASearchGene.ACTION_KEY));
		extractInterval = new JMenuItem(actionMap.get(GLAExtractInterval.ACTION_KEY));
		renameGenes = new JMenuItem(actionMap.get(GLAGeneRenamer.ACTION_KEY));
			
		add(saveGeneTrack, 7);
		addSeparator();
		add(searchGene);
		add(extractInterval);
		add(renameGenes);
	}
}
