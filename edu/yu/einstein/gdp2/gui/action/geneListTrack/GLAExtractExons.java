/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.geneListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.list.geneList.operation.GLOExtractExons;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.ExtractExonsDialog;
import yu.einstein.gdp2.gui.dialog.ExtractGeneIntervalsDialog;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.Track;

/**
 * Extract Exons
 * @author Chirag Gorasia
 * @version 0.1
 */
public class GLAExtractExons extends TrackListActionOperationWorker<GeneList> {

	private static final long serialVersionUID = 4450568171298987897L;
	private static final String 	ACTION_NAME = "Extract Exons"; // action name
	private static final String 	DESCRIPTION = "Extract Exons " +
			"defined relative to genes"; 								// tooltip
	private GeneListTrack 			selectedTrack;						// selected track
	private Track<?> 				resultTrack;
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "GLAExtractExons";

	/**
	 * Creates an instance of {@link GLAExtractExons}
	 */
	public GLAExtractExons() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}

	@Override
	public Operation<GeneList> initializeOperation() throws Exception {
		selectedTrack = (GeneListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			GeneList geneList = selectedTrack.getData();
			ExtractExonsDialog dialog = new ExtractExonsDialog();
			if (dialog.showDialog(getRootPane()) == ExtractGeneIntervalsDialog.APPROVE_OPTION) {
				resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
				if (resultTrack != null) {
					Operation<GeneList> operation = new GLOExtractExons(geneList, dialog.getSelectedExonOption()); 
					return operation;
				}
			}
		}
		return null;
	}

	@Override
	protected void doAtTheEnd(GeneList actionResult) {
		if (actionResult != null) {
			int index = resultTrack.getTrackNumber() - 1;
			Track<?> newTrack = new GeneListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), "exons extracted from " + selectedTrack.getName(), selectedTrack.getStripes());
		}
	}
}