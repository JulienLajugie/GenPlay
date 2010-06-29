/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.Color;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLODivide;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.util.Utils;


/**
 * Divides the selected {@link Track} by another one. Creates a new track from the result
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLADivide extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = -5871594574432175665L; // generated ID
	private static final String 	ACTION_NAME = "Division";			// action name
	private static final String 	DESCRIPTION = 
		"Divide the selected track by another one";						// tooltip
	private BinListTrack 			selectedTrack;						// selected track
	private BinListTrack 			otherTrack;							// other track
	private Track 					resultTrack;						// result track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLADivide";


	/**
	 * Creates an instance of {@link BLADivide}
	 */
	public BLADivide() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			otherTrack = (BinListTrack) TrackChooser.getTracks(getRootPane(), "Choose A Track", "Divide the selected track by:", getTrackList().getBinListTracks());
			if(otherTrack != null) {
				resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
				if (resultTrack != null) {
					DataPrecision precision = Utils.choosePrecision(getRootPane());;
					if (precision != null) {
						BinList binList1 = selectedTrack.getData();
						BinList binList2 = otherTrack.getData();
						Operation<BinList> operation = new BLODivide(binList1, binList2, precision);
						return operation; 
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
			BinListTrack newTrack = new BinListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult);
			// add info to the history
			newTrack.getHistory().add("Result of the division of " + selectedTrack.getName() + " by " + otherTrack.getName(), Color.GRAY);
			newTrack.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getPrecision(), Color.GRAY);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedTrack.getName() + " / " + otherTrack.getName(), null);
		}
	}	
}
