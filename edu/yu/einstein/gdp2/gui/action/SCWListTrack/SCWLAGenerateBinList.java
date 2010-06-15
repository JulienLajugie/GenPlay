/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOGenerateBinList;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.util.Utils;


/**
 * Creates a {@link BinListTrack} from a {@link SCWListTrack} 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWLAGenerateBinList  extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 2102571378866219218L; // generated ID
	private static final String 	ACTION_NAME = "Generate " +
			"Fixed Window Track";									// action name
	private static final String 	DESCRIPTION = "Generate a " +
			"fixed window track from the selected track"; 			// tooltip
	private SCWListTrack 			selectedTrack;					// selected track
	private Track 					resultTrack;					// result track
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "generateBinList";


	/**
	 * Creates an instance of {@link SCWLAGenerateBinList}
	 */
	public SCWLAGenerateBinList() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}
	

	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			Number binSize = NumberOptionPane.getValue(getRootPane(), "Fixed Window Size", "Enter window size", new DecimalFormat("#"), 0, Integer.MAX_VALUE, 1000);
			if (binSize != null) {
				ScoreCalculationMethod scoreCalculation = Utils.chooseScoreCalculation(getRootPane());
				if (scoreCalculation != null) {
					resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
					if (resultTrack != null) {
						DataPrecision precision = Utils.choosePrecision(getRootPane());
						if (precision != null) {
							Operation<BinList> operation = new SCWLOGenerateBinList(selectedTrack.getData(), binSize.intValue(), precision, scoreCalculation);
							return operation;
						}
					}
				}						
			}	
		}
		return null;
	}

	
	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			int index = resultTrack.getTrackNumber() - 1;
			Track newTrack = new BinListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedTrack.getName(), selectedTrack.getStripes());
		}
	}	
}
