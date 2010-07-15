/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterBandStop;
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterCount;
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterPercentage;
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterThreshold;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.filterDialog.FilterDialog;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Filters the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLAFilter extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 2935767531786922267L; 	// generated ID
	private static final String 	ACTION_NAME = "Filter";				// action name
	private static final String 	DESCRIPTION = 
		"Filter the selected track";									// tooltip
	private BinListTrack			selectedTrack ;						// selected track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAFilter";


	/**
	 * Creates an instance of {@link BLAFilter}
	 */
	public BLAFilter() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}

	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			if (selectedTrack.getData().getPrecision() == DataPrecision.PRECISION_1BIT) {
				JOptionPane.showMessageDialog(getRootPane(), "Error, not filter available for 1-Bit tracks", "Error", JOptionPane.ERROR_MESSAGE);
			}
			FilterDialog filterDialog = new FilterDialog();
			if (filterDialog.showFilterDialog(getRootPane()) == FilterDialog.APPROVE_OPTION) {
				BinList binList = selectedTrack.getData();
				Number min = filterDialog.getMinInput();
				Number max = filterDialog.getMaxInput();
				boolean isSaturation = filterDialog.isSaturation(); 
				switch (filterDialog.getFilterType()) {
				case COUNT:
					return new BLOFilterCount(binList, min.intValue(), max.intValue(), isSaturation);
				case PERCENTAGE:
					return new BLOFilterPercentage(binList, min.doubleValue(), max.doubleValue(), isSaturation);
				case THRESHOLD:
					return new BLOFilterThreshold(binList, min.doubleValue(), max.doubleValue(), isSaturation);
				case BANDSTOP:
					return new BLOFilterBandStop(binList, min.doubleValue(), max.doubleValue());
				default:
					throw new IllegalArgumentException("Invalid Saturation Type");
				}
			}

		}
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			selectedTrack.setData(actionResult, operation.getDescription());
		}
	}
}
