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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.action.SCWListTrack;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOFilterBandStop;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOFilterCount;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOFilterPercentage;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOFilterThreshold;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.filterDialog.FilterDialog;
import edu.yu.einstein.genplay.gui.track.SCWListTrack;


/**
 * Filters the {@link ScoredChromosomeWindow} track.
 * Different kind of filters are availables
 * @author Julien Lajugie
 */
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
