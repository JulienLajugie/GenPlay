/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.FilterType;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Removes the values that are not selected by the filter
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ThresholdFilterAction extends TrackListAction {

	private static final long serialVersionUID = 8388717083206483317L;	// generated ID
	private static final String 	ACTION_NAME = "Threshold Filter";	// action name
	private static final String 	DESCRIPTION = 
		"Remove the values that are not selected by the filter";		// tooltip


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
	 * Removes the values that are not selected by the filter.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final BinList binList = selectedTrack.getBinList();
			final FilterType filterType = Utils.chooseFilterType(getRootPane());
			final Number threshold = NumberOptionPane.getValue(getRootPane(), "Threshold", "Select a value for the threshold", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if(threshold != null) {
				final Number successiveValues = NumberOptionPane.getValue(getRootPane(), "Bin Count", "Select a minimum number of successive valid bins", new DecimalFormat("0"), 1, 1000, 1); 
				if(successiveValues != null) {
					final String description = filterType + ", Threshold = "  + threshold + ", Successive Values = " + successiveValues;
					// thread for the action
					new ActionWorker<BinList>(trackList) {
						@Override
						protected BinList doAction() {
							return BinListOperations.thresholdFilter(binList, filterType, threshold.doubleValue(), successiveValues.intValue(), binList.getPrecision());
						}
						@Override
						protected void doAtTheEnd(BinList actionResult) {
							selectedTrack.setBinList(actionResult, description);
								
						}
					}.execute();
				}
			}
		}		
	}
}
