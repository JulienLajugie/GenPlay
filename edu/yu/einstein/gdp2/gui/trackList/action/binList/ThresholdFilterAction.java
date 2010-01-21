/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList.action.binList;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.gui.dialog.BinListPrecisionChooser;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.action.TrackListAction;
import yu.einstein.gdp2.gui.trackList.worker.actionWorker.ActionWorker;


/**
 * Creates a new track containing only the values of the selected track above a threshold.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ThresholdFilterAction extends TrackListAction {

	private static final long serialVersionUID = 8388717083206483317L;	// generated ID
	private static final String 	ACTION_NAME = "Threshold Filter";	// action name
	private static final String 	DESCRIPTION = 
		"Creates a new track containing only the values " +
		"of the selected track above a threshold";						// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "thresholdFilter";


	/**
	 * Creates an instance of {@link ThresholdFilterAction}
	 * @param trackList a {@link TrackList}
	 */
	public ThresholdFilterAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Creates a new track containing only the values of the selected track above a threshold.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final BinList binList = selectedTrack.getBinList();
			// we create the input dialog that retrieves the filter type
			final String[] filterTypeOptions = {"High Pass Filter", "Low Pass Filter"};
			final String filterTypeStr = (String)JOptionPane.showInputDialog(getRootPane(), "Choose a type of filter", "Filter Type", JOptionPane.QUESTION_MESSAGE, null, filterTypeOptions, filterTypeOptions[0]);
			final int filterType;
			if (filterTypeStr.equalsIgnoreCase("High Pass Filter")) {
				filterType = BinListOperations.HIGH_PASS_FILTER;
			} else if (filterTypeStr.equalsIgnoreCase("Low Pass Filter")) {
				filterType = BinListOperations.LOW_PASS_FILTER;
			} else {
				filterType = -1;
			}
			final Number threshold = NumberOptionPane.getValue(getRootPane(), "Threshold", "Select a value for the threshold", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if(threshold != null) {
				final Number successiveValues = NumberOptionPane.getValue(getRootPane(), "Threshold", "Select a minimum number of successive valid windows", new DecimalFormat("0"), 1, 1000, 1); 
				if(successiveValues != null) {
					final Track resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", trackList.getEmptyTracks());
					if (resultTrack != null) {
						final DataPrecision precision = BinListPrecisionChooser.getPrecision(getRootPane());
						// thread for the action
						new ActionWorker<BinList>(trackList) {
							@Override
							protected BinList doAction() {
								return BinListOperations.thresholdFilter(binList, filterType, threshold.doubleValue(), successiveValues.intValue(), precision);
							}
							@Override
							protected void doAtTheEnd(BinList actionResult) {
								int index = resultTrack.getTrackNumber() - 1;
								BinListTrack newTrack = new BinListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), index + 1, trackList.getChromosomeManager(), actionResult);
								// add info to the history
								newTrack.getHistory().add("Result of the filter applied on " + selectedTrack.getName() + ", Filter Type = " + filterTypeStr + ", Threshold = " + threshold + ", Successive Values = " + successiveValues);
								newTrack.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getPrecision(), Color.GRAY);
								trackList.setTrack(index, newTrack, trackList.getConfigurationManager().getTrackHeight(), selectedTrack.getName() + " filtered with a " + filterTypeStr, selectedTrack.getStripes());								
							}
						}.execute();
					}
				}
			}
		}		
	}
}