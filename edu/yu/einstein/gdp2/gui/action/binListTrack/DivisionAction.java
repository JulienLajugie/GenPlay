/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLODivide;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.ExceptionManager;
import yu.einstein.gdp2.util.Utils;


/**
 * Divides the selected {@link Track} by another one. Creates a new track from the result
 * @author Julien Lajugie
 * @version 0.1
 */
public final class DivisionAction extends TrackListAction {

	private static final long serialVersionUID = -5871594574432175665L; // generated ID
	private static final String 	ACTION_NAME = "Division";			// action name
	private static final String 	DESCRIPTION = 
		"Divide the selected track by another one";						// tooltip

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "DivisionAction";


	/**
	 * Creates an instance of {@link DivisionAction}
	 * @param trackList a {@link TrackList}
	 */
	public DivisionAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Divides the selected {@link Track} by another one. Creates a new track from the result
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final BinListTrack otherTrack = (BinListTrack) TrackChooser.getTracks(getRootPane(), "Choose A Track", "Divide the selected track by:", trackList.getBinListTracks());
			if(otherTrack != null) {
				final Track resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", trackList.getEmptyTracks());
				if (resultTrack != null) {
					final DataPrecision precision = Utils.choosePrecision(getRootPane());;
					if (precision != null) {
						final BinList binList1 = selectedTrack.getBinList();
						final BinList binList2 = otherTrack.getBinList();
						final BinListOperation<BinList> operation = new BLODivide(binList1, binList2, precision);
						// thread for the action
						new ActionWorker<BinList>(trackList, "Dividing") {
							@Override
							protected BinList doAction() throws Exception {
								try {
									return operation.compute(); 
								} catch (BinListDifferentWindowSizeException e) {
									ExceptionManager.handleException(getRootPane(), e, "Dividing two tracks with different window sizes is not allowed");
									return null;
								}
							}

							@Override
							protected void doAtTheEnd(BinList actionResult) {
								int index = resultTrack.getTrackNumber() - 1;
								BinListTrack newTrack = new BinListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), index + 1, trackList.getChromosomeManager(), actionResult);
								// add info to the history
								newTrack.getHistory().add("Result of the division of " + selectedTrack.getName() + " by " + otherTrack.getName(), Color.GRAY);
								newTrack.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getPrecision(), Color.GRAY);
								trackList.setTrack(index, newTrack, trackList.getConfigurationManager().getTrackHeight(), selectedTrack.getName() + " / " + otherTrack.getName(), null);
							}
						}.execute();
					}
				}
			}
		}
	}		
}
