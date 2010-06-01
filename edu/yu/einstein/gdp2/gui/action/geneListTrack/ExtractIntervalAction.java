/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.geneListTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.list.geneList.GeneListOperations;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.ExtractGeneIntervalsDialog;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Extract intervals defined relative to genes and generate a new track with 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ExtractIntervalAction  extends TrackListAction {

	private static final long serialVersionUID = 2102571378866219218L; // generated ID
	private static final String 	ACTION_NAME = "Extract Intervals";		// action name
	private static final String 	DESCRIPTION = "Extract intervals defined relative to genes"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "extractIntervals";



	public ExtractIntervalAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Searches a gene on the selected track
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		final GeneListTrack selectedTrack = (GeneListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final GeneList geneList = selectedTrack.getData();
			final ExtractGeneIntervalsDialog dialog = new ExtractGeneIntervalsDialog();
			if (dialog.showDialog(getRootPane()) == ExtractGeneIntervalsDialog.APPROVE_OPTION) {
				final Track resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", trackList.getEmptyTracks());
				if (resultTrack != null) {
					final int index = resultTrack.getTrackNumber() - 1;
					// thread for the action
					new ActionWorker<GeneList>(trackList, "Extracting Intervals") {
						@Override
						protected GeneList doAction() {
							return GeneListOperations.extractIntevals(geneList, dialog.getStartDistance(), dialog.getStartFrom(), dialog.getStopDistance(), dialog.getStopFrom());
						}
						@Override
						protected void doAtTheEnd(GeneList actionResult) {
							Track newTrack = new GeneListTrack(trackList.getGenomeWindow(), index + 1, actionResult);
							trackList.setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), "intervals extracted from " + selectedTrack.getName(), selectedTrack.getStripes());							
						}
					}.execute();
				}
			}
		}
	}
}
