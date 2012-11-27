/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.action.binListTrack;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOFilterBandStop;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOFilterCount;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOFilterPercentage;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOFilterThreshold;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.filterDialog.FilterDialog;
import edu.yu.einstein.genplay.gui.old.track.BinListTrack;



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
