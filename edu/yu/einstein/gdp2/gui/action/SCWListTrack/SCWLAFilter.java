package yu.einstein.gdp2.gui.action.SCWListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOFilterBandStop;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOFilterCount;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOFilterPercentage;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOFilterThreshold;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.filterDialog.FilterDialog;
import yu.einstein.gdp2.gui.track.SCWListTrack;

public class SCWLAFilter extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = 960963269753754801L;	// generated ID
	private static final String 	ACTION_NAME = "Filter";				// action name
	private static final String 	DESCRIPTION = 
		"Filter the selected track";									// tooltip
	private SCWListTrack			selectedTrack ;						// selected track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLAFilter";


	/**
	 * Creates an instance of {@link SCWLAFilter}
	 */
	public SCWLAFilter() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}

	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			FilterDialog filterDialog = new FilterDialog();
			if (filterDialog.showFilterDialog(getRootPane()) == FilterDialog.APPROVE_OPTION) {
				ScoredChromosomeWindowList list = selectedTrack.getData();
				Number min = filterDialog.getMinInput();
				Number max = filterDialog.getMaxInput();
				boolean isSaturation = filterDialog.isSaturation(); 
				switch (filterDialog.getFilterType()) {
				case COUNT:
					return new SCWLOFilterCount(list, min.intValue(), max.intValue(), isSaturation);
				case PERCENTAGE:
					return new SCWLOFilterPercentage(list, min.doubleValue(), max.doubleValue(), isSaturation);
				case THRESHOLD:
					return new SCWLOFilterThreshold(list, min.doubleValue(), max.doubleValue(), isSaturation);
				case BANDSTOP:
					return new SCWLOFilterBandStop(list, min.doubleValue(), max.doubleValue());
				default:
					throw new IllegalArgumentException("Invalid Saturation Type");
				}
			}

		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			selectedTrack.setData(actionResult, operation.getDescription());
		}
	}
}
