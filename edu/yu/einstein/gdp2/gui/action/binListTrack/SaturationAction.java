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
import yu.einstein.gdp2.core.enums.SaturationType;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Saturates the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SaturationAction extends TrackListAction {

	private static final long serialVersionUID = 2935767531786922267L; 	// generated ID
	private static final String 	ACTION_NAME = "Saturation";			// action name
	private static final String 	DESCRIPTION = 
		"Saturate the selected track";									// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SaturationAction";


	/**
	 * Creates an instance of {@link SaturationAction}
	 * @param trackList a {@link TrackList}
	 */
	public SaturationAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Saturates the selected {@link BinListTrack}.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			if (selectedTrack.getBinList().getPrecision() == DataPrecision.PRECISION_1BIT) {
				JOptionPane.showMessageDialog(getRootPane(), "Error, saturation is not available for 1-Bit tracks", "Error", JOptionPane.ERROR_MESSAGE);
			}
			final SaturationType saturationType = Utils.chooseSaturationType(getRootPane());
			switch (saturationType) {
			case COUNT:
				countSaturation(selectedTrack);
				break;
			case PERCENTAGE:
				percentageSaturation(selectedTrack);
				break;
			case THRESHOLD:
				thresholdSaturation(selectedTrack);
				break;
			}
		}
	}		

	
	/**
	 * Saturates a fixed number of values
	 * @param selectedTrack {@link BinListTrack} selected by the user
	 */
	private void countSaturation(final BinListTrack selectedTrack) {
		final BinList binList = selectedTrack.getBinList();
		final Number countLow = NumberOptionPane.getValue(getRootPane(), "Low Values", "Select the number of low values to saturate", new DecimalFormat("0"), Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		if(countLow != null) {
			final Number countHigh = NumberOptionPane.getValue(getRootPane(), "High Values", "Select the number of high values to saturate", new DecimalFormat("0"), Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
			if(countHigh != null) {
				final String description = "Count Saturation, Low Count = "  + countLow.intValue()+ ", High Count = " + countHigh;
				// thread for the action
				new ActionWorker<BinList>(trackList) {
					@Override
					protected BinList doAction() {
						return BinListOperations.saturationCount(binList, countLow.intValue(), countHigh.intValue());
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
	 * Saturates a percentage of extreme values
	 * @param selectedTrack {@link BinListTrack} selected by the user
	 */
	private void percentageSaturation(final BinListTrack selectedTrack) {
		final BinList binList = selectedTrack.getBinList();
		final Number percentageLow = NumberOptionPane.getValue(getRootPane(), "Low Percentage", "Select the percentage of low values to saturate", new DecimalFormat("0%"), 0, 1, 0.01);
		if(percentageLow != null) {
			final Number percentageHigh = NumberOptionPane.getValue(getRootPane(), "High Percentage", "Select the percentage of high values to saturate", new DecimalFormat("0%"), 0, 1, 0.01);
			if(percentageHigh != null) {
				if (percentageHigh.doubleValue() + percentageLow.doubleValue() > 1) {
					JOptionPane.showMessageDialog(getRootPane(), "The sum of the two percentage must be smaller than 1", "Error", JOptionPane.ERROR_MESSAGE, null);
				}
				
				final String description = "Percentage Saturation, Low Percentage = "  + percentageLow.doubleValue()+ ", High Percentage = " + percentageHigh.doubleValue();
				// thread for the action
				new ActionWorker<BinList>(trackList) {
					@Override
					protected BinList doAction() {
						return BinListOperations.saturationPercentage(binList, percentageLow.doubleValue(), percentageHigh.doubleValue());
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
	 * Saturates values above or under a specified threshold
	 * @param selectedTrack {@link BinListTrack} selected by the user
	 */
	private void thresholdSaturation(final BinListTrack selectedTrack) {
		final BinList binList = selectedTrack.getBinList();
		final Number thresholdLow = NumberOptionPane.getValue(getRootPane(), "Low Threshold", "Saturate values smaller than:", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
		if(thresholdLow != null) {
			final Number thresholdHigh = NumberOptionPane.getValue(getRootPane(), "High Threshold", "Saturate values greater than:", new DecimalFormat("0.0"), thresholdLow.doubleValue(), Double.POSITIVE_INFINITY, thresholdLow.doubleValue());
			if(thresholdHigh != null) {
				final String description = "Threshold Saturation, Low Threshold = "  + thresholdLow.doubleValue()+ ", High Threshold = " + thresholdHigh;
				// thread for the action
				new ActionWorker<BinList>(trackList) {
					@Override
					protected BinList doAction() {
						return BinListOperations.saturationThreshold(binList, thresholdLow.doubleValue(), thresholdHigh.doubleValue());
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
