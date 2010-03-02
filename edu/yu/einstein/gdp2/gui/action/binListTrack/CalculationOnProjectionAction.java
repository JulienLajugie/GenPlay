/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.ExceptionManager;
import yu.einstein.gdp2.util.Utils;


/**
 * Computes the average, sum or max of the selected track on intervals defined by another track
 * @author Julien Lajugie
 * @version 0.1
 */
public class CalculationOnProjectionAction extends TrackListAction {

	private static final long serialVersionUID = -3736735803307616477L;			// generated ID
	private static final String 	ACTION_NAME = "Calculation on Projection";	// action name
	private static final String 	DESCRIPTION = 
		"Compute the average, the sum or the max of the " +
		"selected track on intervals defined by another track";					// tooltip

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "binListCalculationOnProjection";


	/**
	 * Creates an instance of {@link CalculationOnProjectionAction}
	 * @param trackList a {@link TrackList}
	 */
	public CalculationOnProjectionAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Computes an average, a sum or a max of the selected track on intervals defined by another track
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final Track intervalTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Choose the track defining the intervals:", trackList.getBinListTracks());
			if(intervalTrack != null) {
				final Number percentage = NumberOptionPane.getValue(getRootPane(), "Enter a percentage", "Perform the calculation on the x% greatest values of each interval:", new DecimalFormat("0"), 0, 100, 100);
				if (percentage != null) {
					final ScoreCalculationMethod method = Utils.chooseScoreCalculation(getRootPane());
					if (method != null) {
						final Track resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", trackList.getEmptyTracks());
						if (resultTrack != null) {
							final DataPrecision precision = Utils.choosePrecision(getRootPane());;
							if (precision != null) {
								final BinList valueBinList = ((BinListTrack)selectedTrack).getBinList();
								final BinList intervalBinList = ((BinListTrack)intervalTrack).getBinList();
								// thread for the action
								new ActionWorker<BinList>(trackList) {
									@Override
									protected BinList doAction() {
										try {
											return BinListOperations.calculationOnProjection(intervalBinList, valueBinList, percentage.intValue(), method, precision);
										} catch (BinListDifferentWindowSizeException e) {
											ExceptionManager.handleException(getRootPane(), e, "Working on two tracks with different window sizes is not allowed");
											return null;
										} catch (Exception e) {
											ExceptionManager.handleException(getRootPane(), e, "Error while performing the calculation on intervals");
											return null;
										}
									}

									@Override
									protected void doAtTheEnd(BinList actionResult) {
										if (actionResult != null) {
											int index = resultTrack.getTrackNumber() - 1;
											BinListTrack newTrack = new BinListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), index + 1, trackList.getChromosomeManager(), actionResult);
											// add info to the history
											newTrack.getHistory().add("Result of the " + method + " of " + selectedTrack.getName() + " calculated on the intervals defined by " + intervalTrack.getName() + " on the " + percentage + "% greatest values", Color.GRAY);
											newTrack.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getPrecision(), Color.GRAY);
											trackList.setTrack(index, newTrack, trackList.getConfigurationManager().getTrackHeight(), "average of " + selectedTrack.getName() + " from intervals of  " + intervalTrack.getName(), null);
										}

									}
								}.execute();
							}
						}
					}
				}		
			}
		}
	}
}
