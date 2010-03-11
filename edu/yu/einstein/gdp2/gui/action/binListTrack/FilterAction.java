/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.FilterType;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BinListFilter;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Filters the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class FilterAction extends TrackListAction {

	private static final long serialVersionUID = 2935767531786922267L; 	// generated ID
	private static final String 	ACTION_NAME = "Filter";				// action name
	private static final String 	DESCRIPTION = 
		"Filter the selected track";									// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "FilterAction";


	/**
	 * Creates an instance of {@link FilterAction}
	 * @param trackList a {@link TrackList}
	 */
	public FilterAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Filters the selected {@link BinListTrack}.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			if (selectedTrack.getBinList().getPrecision() == DataPrecision.PRECISION_1BIT) {
				JOptionPane.showMessageDialog(getRootPane(), "Error, not filter available for 1-Bit tracks", "Error", JOptionPane.ERROR_MESSAGE);
			}
			final FilterType filterType = Utils.chooseFilterType(getRootPane());
			switch (filterType) {
			case COUNT:
				countFilter(selectedTrack);
				break;
			case PERCENTAGE:
				percentageFilter(selectedTrack);
				break;
			case THRESHOLD:
				thresholdFilter(selectedTrack);
				break;
			case DENSITY:
				densityFilter(selectedTrack);
			}
		}
	}		


	/**
	 * Filters a fixed number of values
	 * @param selectedTrack {@link BinListTrack} selected by the user
	 */
	private void countFilter(final BinListTrack selectedTrack) {
		final BinList binList = selectedTrack.getBinList();
		final Number countLow = NumberOptionPane.getValue(getRootPane(), "Low Values", "Select the number of low values to filter", new DecimalFormat("0"), Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		if(countLow != null) {
			final Number countHigh = NumberOptionPane.getValue(getRootPane(), "High Values", "Select the number of high values to filter", new DecimalFormat("0"), Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
			if(countHigh != null) {
				final String description = "Count Filter, Low Count = "  + countLow.intValue()+ ", High Count = " + countHigh;
				// thread for the action
				new ActionWorker<BinList>(trackList) {
					@Override
					protected BinList doAction() {
						return BinListFilter.countFilter(binList, countLow.intValue(), countHigh.intValue());
					}
					@Override
					protected void doAtTheEnd(BinList actionResult) {
						selectedTrack.setBinList(actionResult, description);
					}
				}.execute();
			}
		}		
	}
	
	
	/**
	 * Filters values where the density of not null values on a region is too low
	 * @param selectedTrack {@link BinListTrack} selected by the user
	 */
	private void densityFilter(final BinListTrack selectedTrack) {
		final BinList binList = selectedTrack.getBinList();
		final Number thresholdLow = NumberOptionPane.getValue(getRootPane(), "Low Threshold", "Remove values smaller than:", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		if(thresholdLow != null) {
			final Number thresholdHigh = NumberOptionPane.getValue(getRootPane(), "High Threshold", "Remove values greater than:", new DecimalFormat("0.0"), thresholdLow.doubleValue(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
			if(thresholdHigh != null) {
				if (thresholdHigh.doubleValue() <= thresholdLow.doubleValue()) {
					JOptionPane.showMessageDialog(getRootPane(), "The high threshold must be greater than the low one", "Error", JOptionPane.ERROR_MESSAGE, null);
				} else {
					final Number regionSize = NumberOptionPane.getValue(getRootPane(), "Size", "<html>Select the size of the region filtered<br/><center>(in number of bins)</center></html>", new DecimalFormat("0"), 1, 1000, 1); 
					if(regionSize != null) {
						final Number density = NumberOptionPane.getValue(getRootPane(), "Density", "Enter the percentage of value above the filter", new DecimalFormat("###.###%"), 0, 1, 1);
						if (density != null) {
							final String description = "Density filter, Low Threshold = "  + thresholdLow.doubleValue()+ ", High Threshold = " + thresholdHigh + ", Region Size = " + regionSize.intValue() + ", Density = " + density;
							// thread for the action
							new ActionWorker<BinList>(trackList) {
								@Override
								protected BinList doAction() {
									return BinListFilter.densityFilter(binList, thresholdLow.doubleValue(), thresholdHigh.doubleValue(), density.doubleValue(), regionSize.intValue());
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


	/**
	 * Filters a percentage of extreme values
	 * @param selectedTrack {@link BinListTrack} selected by the user
	 */
	private void percentageFilter(final BinListTrack selectedTrack) {
		final BinList binList = selectedTrack.getBinList();
		final Number percentageLow = NumberOptionPane.getValue(getRootPane(), "Low Percentage", "Select the percentage of low values to filter", new DecimalFormat("0%"), 0, 1, 0.01);
		if(percentageLow != null) {
			final Number percentageHigh = NumberOptionPane.getValue(getRootPane(), "High Percentage", "Select the percentage of high values to filter", new DecimalFormat("0%"), 0, 1, 0.01);
			if(percentageHigh != null) {
				if (percentageHigh.doubleValue() + percentageLow.doubleValue() > 1) {
					JOptionPane.showMessageDialog(getRootPane(), "The sum of the two percentages must be smaller than 1", "Error", JOptionPane.ERROR_MESSAGE, null);
				}

				final String description = "Percentage Filter, Low Percentage = "  + percentageLow.doubleValue()+ ", High Percentage = " + percentageHigh.doubleValue();
				// thread for the action
				new ActionWorker<BinList>(trackList) {
					@Override
					protected BinList doAction() {
						return BinListFilter.percentageFilter(binList, percentageLow.doubleValue(), percentageHigh.doubleValue());
					}
					@Override
					protected void doAtTheEnd(BinList actionResult) {
						selectedTrack.setBinList(actionResult, description);
					}
				}.execute();
			}
		}
	}


	/**
	 * Filters values above or under a specified threshold
	 * @param selectedTrack {@link BinListTrack} selected by the user
	 */
	private void thresholdFilter(final BinListTrack selectedTrack) {
		final BinList binList = selectedTrack.getBinList();
		final Number thresholdLow = NumberOptionPane.getValue(getRootPane(), "Low Threshold", "Remove values smaller than:", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		if(thresholdLow != null) {
			final Number thresholdHigh = NumberOptionPane.getValue(getRootPane(), "High Threshold", "Remove values greater than:", new DecimalFormat("0.0"), thresholdLow.doubleValue(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
			if(thresholdHigh != null) {
				if (thresholdHigh.doubleValue() <= thresholdLow.doubleValue()) {
					JOptionPane.showMessageDialog(getRootPane(), "The high threshold must be greater than the low one", "Error", JOptionPane.ERROR_MESSAGE, null);
				} else {
					final Number successiveValues = NumberOptionPane.getValue(getRootPane(), "Bin Count", "Select a minimum number of successive valid bins", new DecimalFormat("0"), 1, 1000, 1);
					if(successiveValues != null) {
						final String description = "Threshold Filter, Low Threshold = "  + thresholdLow.doubleValue()+ ", High Threshold = " + thresholdHigh + ", Successive Values = " + successiveValues;
						// thread for the action
						new ActionWorker<BinList>(trackList) {
							@Override
							protected BinList doAction() {
								return BinListFilter.thresholdFilter(binList, thresholdLow.doubleValue(), thresholdHigh.doubleValue(), successiveValues.intValue());
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
