/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList.action.binList;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;
import yu.einstein.gdp2.gui.dialog.BinListPrecisionChooser;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.action.TrackListAction;
import yu.einstein.gdp2.gui.trackList.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.ExceptionManager;


/**
 * Multiplies the selected {@link Track} to another one. Creates a new track from the result
 * @author Julien Lajugie
 * @version 0.1
 */
public final class MultiplyByAction extends TrackListAction {

	private static final long serialVersionUID = -2313977686484948489L; 	// generated ID
	private static final String 	ACTION_NAME = "Multiply By";			// action name
	private static final String 	DESCRIPTION = 
		"Multiply the selected track by another one";						// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "binListMultiply";


	/**
	 * Creates an instance of {@link MultiplyByAction}
	 * @param trackList a {@link TrackList}
	 */
	public MultiplyByAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Multiplies the selected {@link Track} to another one. Creates a new track from the result
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final Track otherTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Choose a track to multiply with the selected track:", trackList.getBinListTracks());
			if(otherTrack != null) {
				final Track resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", trackList.getEmptyTracks());
				if (resultTrack != null) {
					final DataPrecision precision = BinListPrecisionChooser.getPrecision(getRootPane());
					if (precision != null) {
						final BinList binList1 = ((BinListTrack)selectedTrack).getBinList();
						final BinList binList2 = ((BinListTrack)otherTrack).getBinList();
						// thread for the action
						new ActionWorker<BinList>(trackList) {
							@Override
							protected BinList doAction() {
								try {
									return BinListOperations.multiplication(binList1, binList2, precision);
								} catch (BinListDifferentWindowSizeException e) {
									ExceptionManager.handleException(getRootPane(), e, "Multiplying two tracks with different window sizes is not allowed");
									return null;
								}
							}
							@Override
							protected void doAtTheEnd(BinList actionResult) {
								if (actionResult != null) {
									int index = resultTrack.getTrackNumber() - 1;
									BinListTrack newTrack = new BinListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), index + 1, trackList.getChromosomeManager(), actionResult);
									// add info to the history
									newTrack.getHistory().add("Result of the multiplication of " + selectedTrack.getName() + " by " + otherTrack.getName(), Color.GRAY);
									newTrack.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getPrecision(), Color.GRAY);
									trackList.setTrack(index, newTrack, trackList.getConfigurationManager().getTrackHeight(), selectedTrack.getName() + " * " + otherTrack.getName(), null);
								}								
							}
						}.execute();
					}
				}
			}
		}		
	}
}