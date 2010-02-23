/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList.action.binList;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.FilterType;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.action.TrackListAction;
import yu.einstein.gdp2.gui.trackList.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Removes the values that are not selected by the filter
 * @author Julien Lajugie
 * @version 0.1
 */
public class DensityFilterAction extends TrackListAction{

	private static final long serialVersionUID = -4411096954459612638L;	// generated ID
	private static final String 	ACTION_NAME = "Density Filter";		// action name
	private static final String 	DESCRIPTION = 
		"Removes the values that are not selected by the filter";		// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "densityFilter";


	/**
	 * Creates an instance of {@link DensityFilterAction}
	 * @param trackList a {@link TrackList}
	 */
	public DensityFilterAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Removes the values that are not selected by the filter
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final BinList binList = selectedTrack.getBinList();
			final FilterType filterType = Utils.chooseFilterType(getRootPane());
			final Number threshold = NumberOptionPane.getValue(getRootPane(), "Threshold", "Select a value for the threshold", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if(threshold != null) {
				final Number regionSize = NumberOptionPane.getValue(getRootPane(), "Size", "<html>Select the size of the region filtered<br/><center>(in number of bins)</center></html>", new DecimalFormat("0"), 1, 1000, 1); 
				if(regionSize != null) {
					final Number density = NumberOptionPane.getValue(getRootPane(), "Density", "Enter the percentage of value above the filter", new DecimalFormat("###.###%"), 0, 1, 1);
					if (density != null) {
						final String description = "Density " + filterType + ", Threshold = "  + threshold + ", Region Size = " + regionSize.intValue() + ", Density = " + density;
						// thread for the action
						new ActionWorker<BinList>(trackList) {
							@Override
							protected BinList doAction() {
								return BinListOperations.densityFilter(binList, filterType, threshold.doubleValue(), density.doubleValue(), regionSize.intValue(), binList.getPrecision());
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
}
