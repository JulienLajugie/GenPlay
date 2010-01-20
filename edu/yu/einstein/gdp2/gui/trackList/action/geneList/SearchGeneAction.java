/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList.action.geneList;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.action.TrackListAction;
import yu.einstein.gdp2.gui.trackList.worker.actionWorker.ActionWorker;


/**
 * Searches a gene on a {@link GeneListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SearchGeneAction  extends TrackListAction {

	private static final long serialVersionUID = 2102571378866219218L; 	// generated ID
	private static final String 	ACTION_NAME = "Seach Gene";			// action name
	private static final String 	DESCRIPTION = 
		"Search a gene on the selected track"; 							// tooltip


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "searchGene";



	public SearchGeneAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	/**
	 * Searches a gene on the selected track
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ((trackList.getSelectedTrack() != null) && (trackList.getSelectedTrack() instanceof GeneListTrack)) {
			final GeneListTrack selectedTrack = (GeneListTrack) trackList.getSelectedTrack();
			if (selectedTrack != null) {
				String lastSearchedName = null;
				if (selectedTrack.getData().getLastSearchedGene() != null) {
					lastSearchedName = selectedTrack.getData().getLastSearchedGene().getName(); 
				}
				final String geneName = (String) JOptionPane.showInputDialog(getRootPane(), "Enter the name of a gene", "Gene Search", JOptionPane.QUESTION_MESSAGE, null, null, lastSearchedName);
				if (geneName != null) {
					// thread for the action
					new ActionWorker<Gene>(trackList) {
						@Override
						protected Gene doAction() {
							return selectedTrack.getData().search(geneName);
						}
						@Override
						protected void doAtTheEnd(Gene actionResult) {
							if (actionResult != null) {
								GenomeWindow newWindow = new GenomeWindow(actionResult.getChromo(), actionResult.getTxStart(), actionResult.getTxStop());
								trackList.setGenomeWindow(newWindow);
							}

						}
					}.execute();
				}
			}
		}
	}
}