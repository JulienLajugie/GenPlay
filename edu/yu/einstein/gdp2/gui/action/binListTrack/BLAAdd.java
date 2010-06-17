/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.Color;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOAdd;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.util.Utils;


/**
 * Adds the selected {@link Track} to another one. Creates a new track from the result
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLAAdd extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = -2313977686484948489L; // generated ID
	private static final String 	ACTION_NAME = "Addition";	// action name
	private static final String 	DESCRIPTION = 
		"Add the selected track to another one";				// tooltip
	private BinListTrack 				selectedTrack = null;	// selected track
	private Track 						otherTrack = null;		// 2nd selected track
	private Track 						resultTrack = null;		// result track
	private Operation<BinList> 	operation;				// operation


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAAdd";


	/**
	 * Creates an instance of {@link BLAAdd}
	 */
	public BLAAdd() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			otherTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Choose a track to add to the selected track:", getTrackList().getBinListTracks());
			if(otherTrack != null) {
				resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
				if (resultTrack != null) {
					DataPrecision precision = Utils.choosePrecision(getRootPane());
					if (precision != null) {
						BinList binList1 = ((BinListTrack)selectedTrack).getBinList();
						BinList binList2 = ((BinListTrack)otherTrack).getBinList();						
						operation = new BLOAdd(binList1, binList2, precision);
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
			newTrack.getHistory().add("Result of the addition of " + selectedTrack.getName() + " and " + otherTrack.getName(), Color.GRAY);
			newTrack.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getPrecision(), Color.GRAY);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedTrack.getName() + " + " + otherTrack.getName(), null);
		}
	}
}