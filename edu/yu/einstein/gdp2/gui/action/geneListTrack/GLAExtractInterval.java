/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.geneListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.list.geneList.operation.GLOExtractIntervals;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.ExtractGeneIntervalsDialog;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Extract intervals defined relative to genes and generate a new track with 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GLAExtractInterval  extends TrackListActionOperationWorker<GeneList> {

	private static final long serialVersionUID = 2102571378866219218L; // generated ID
	private static final String 	ACTION_NAME = "Extract Intervals"; // action name
	private static final String 	DESCRIPTION = "Extract intervals " +
			"defined relative to genes"; 								// tooltip
	private GeneListTrack 			selectedTrack;						// selected track
	private Track<?> 				resultTrack;						// result track
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "GLAExtractInterval";


	/**
	 * Creates an instance of {@link GLAExtractInterval}
	 */
	public GLAExtractInterval() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<GeneList> initializeOperation() {
		selectedTrack = (GeneListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			GeneList geneList = selectedTrack.getData();
			ExtractGeneIntervalsDialog dialog = new ExtractGeneIntervalsDialog();
			if (dialog.showDialog(getRootPane()) == ExtractGeneIntervalsDialog.APPROVE_OPTION) {
				resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
				if (resultTrack != null) {
					Operation<GeneList> operation = new GLOExtractIntervals(geneList, dialog.getStartDistance(), dialog.getStartFrom(), dialog.getStopDistance(), dialog.getStopFrom()); 
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
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), "intervals extracted from " + selectedTrack.getName(), selectedTrack.getStripes());
		}
	}
}
