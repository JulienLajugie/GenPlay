/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.geneListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.list.geneList.operation.GLOScoreFromBinList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.util.Utils;


/**
 * Gives a score to the exons of the genes from a scored track
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLAScoreExons  extends TrackListActionOperationWorker<GeneList> {

	private static final long serialVersionUID = 2102571378866219218L; 	// generated ID
	private static final String 	ACTION_NAME = "Score Exons";		// action name
	private static final String 	DESCRIPTION = 
		"Give a score to the exons of the genes from a scored track";	// tooltip
	private GeneListTrack 			selectedTrack;						// selected track
	private BinListTrack 			binListTrack;						// binlist track
	private Track<?>				resultTrack; 						// result track

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "GLAScoreExons";


	/**
	 * Creates an instance of {@link GLAScoreExons}
	 */
	public GLAScoreExons() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<GeneList> initializeOperation() throws Exception {
		if ((getTrackList().getSelectedTrack() != null) && (getTrackList().getSelectedTrack() instanceof GeneListTrack)) {
			selectedTrack = (GeneListTrack) getTrackList().getSelectedTrack();
			if (selectedTrack != null) {
				binListTrack = (BinListTrack) TrackChooser.getTracks(getRootPane(), "Choose A Track", "Select the track with the scores:", getTrackList().getBinListTracks());
				if (binListTrack != null) {
					ScoreCalculationMethod method = Utils.chooseScoreCalculation(getRootPane());
					if (method != null) {
						resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
						if (resultTrack != null) {
							Operation<GeneList> operation = new GLOScoreFromBinList(selectedTrack.getData(), binListTrack.getData(), method);
							return operation;
						}
					}
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(GeneList actionResult) {
		if (actionResult != null) {
			int index = resultTrack.getTrackNumber();
			GeneListTrack newTrack = new GeneListTrack(resultTrack.getGenomeWindow(), index, actionResult);
			getTrackList().setTrack(index - 1, newTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedTrack.getName() + " with score from " + binListTrack.getName(), null);
		}		
	}
}
