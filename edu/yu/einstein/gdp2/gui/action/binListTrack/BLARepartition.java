/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLORepartition;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.MultiTrackChooser;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Generates a file showing the repartition of the score values of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLARepartition extends TrackListActionOperationWorker<int [][]> {

	private static final long serialVersionUID = -7166030548181210580L; // generated ID
	private static final String 	ACTION_NAME = "Show Repartition";	// action name
	private static final String 	DESCRIPTION = 
		"Generate a csv file showing the repartition of the scores of the selected track";	// tooltip
	private Track[] selectedTracks;

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLARepartition";


	/**
	 * Creates an instance of {@link BLARepartition}
	 */
	public BLARepartition() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<int [][]> initializeOperation() {
		BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			Number scoreBin = NumberOptionPane.getValue(getRootPane(), "Size", "Enter the size of the bin of score:", new DecimalFormat("0.0"), 0, 1000, 1);
			if (scoreBin != null) {	
				selectedTracks = MultiTrackChooser.getSelectedTracks(getRootPane(), getTrackList().getBinListTracks());
				if ((selectedTracks != null)) {
					BinList[] binListArray = new BinList[selectedTracks.length];
					for (int i = 0; i < selectedTracks.length; i++) {
						binListArray[i] = ((BinListTrack)selectedTracks[i]).getBinList();						
					}					
					Operation<int [][]> operation = new BLORepartition(binListArray, scoreBin.doubleValue());
					return operation;
				}
			}
		}
		return null;
	}
	
	
	@Override
	protected void doAtTheEnd(int[][] actionResult) {

	}
}
