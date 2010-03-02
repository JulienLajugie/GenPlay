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
import yu.einstein.gdp2.core.list.binList.BinListOperations;
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
 * Adds the selected {@link Track} to another one. Creates a new track from the result
 * @author Julien Lajugie
 * @version 0.1
 */
public final class AdditionAction extends TrackListAction {

	private static final long serialVersionUID = -2313977686484948489L; // generated ID
	private static final String 	ACTION_NAME = "Addition";			// action name
	private static final String 	DESCRIPTION = 
		"Add the selected track to another one";						// tooltip

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "AdditionAction";


	/**
	 * Creates an instance of {@link AdditionAction}
	 * @param trackList a {@link TrackList}
	 */
	public AdditionAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Adds the selected {@link Track} to another one. Creates a new track from the result
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final Track otherTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Choose a track to add to the selected track:", trackList.getBinListTracks());
			if(otherTrack != null) {
				final Track resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", trackList.getEmptyTracks());
				if (resultTrack != null) {
					final DataPrecision precision = Utils.choosePrecision(getRootPane());
					if (precision != null) {
						final BinList binList1 = ((BinListTrack)selectedTrack).getBinList();
						final BinList binList2 = ((BinListTrack)otherTrack).getBinList();						
						// thread for the action
						new ActionWorker<BinList>(trackList) {
							@Override
							protected BinList doAction() {
								try {
									return BinListOperations.addition(binList1, binList2, precision);
								} catch (BinListDifferentWindowSizeException e) {
									ExceptionManager.handleException(getRootPane(), e, "Adding two tracks with different window sizes is not allowed");
									return null;
								} catch (Exception e) {
									ExceptionManager.handleException(getRootPane(), e, "Error while summing the tracks");
									return null;
								}
							}
							@Override
							protected void doAtTheEnd(BinList resultList) {
								if (resultList != null) {
									int index = resultTrack.getTrackNumber() - 1;
									BinListTrack newTrack = new BinListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), index + 1, trackList.getChromosomeManager(), resultList);
									// add info to the history
									newTrack.getHistory().add("Result of the addition of " + selectedTrack.getName() + " and " + otherTrack.getName(), Color.GRAY);
									newTrack.getHistory().add("Window Size = " + resultList.getBinSize() + "bp, Precision = " + resultList.getPrecision(), Color.GRAY);
									trackList.setTrack(index, newTrack, trackList.getConfigurationManager().getTrackHeight(), selectedTrack.getName() + " + " + otherTrack.getName(), null);
								}

							}
						}.execute();
					}
				}
			}
		}		
	}
}