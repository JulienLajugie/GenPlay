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
package edu.yu.einstein.genplay.gui.action.geneListTrack;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.list.geneList.operation.GLOFilterBandStop;
import edu.yu.einstein.genplay.core.list.geneList.operation.GLOFilterCount;
import edu.yu.einstein.genplay.core.list.geneList.operation.GLOFilterPercentage;
import edu.yu.einstein.genplay.core.list.geneList.operation.GLOFilterThreshold;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.filterDialog.FilterDialog;
import edu.yu.einstein.genplay.gui.track.GeneListTrack;

/**
 * Filter the selected {@link GeneListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLAFilter extends TrackListActionOperationWorker<GeneList> {

	private static final long serialVersionUID = -5807756062510954560L;	// generated id
	private static final String 	ACTION_NAME = "Filter";		 		// action name
	private static final String 	DESCRIPTION = "Filter the selected track";	// tooltip
	private GeneListTrack 			selectedTrack;						// selected track
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "GLAFilter";


	/**
	 * Creates an instance of {@link GLAFilter}
	 */
	public GLAFilter() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<GeneList> initializeOperation() {
		selectedTrack = (GeneListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			FilterDialog filterDialog = new FilterDialog();
			if (filterDialog.showFilterDialog(getRootPane()) == FilterDialog.APPROVE_OPTION) {
				GeneList geneList = selectedTrack.getData();
				Number min = filterDialog.getMinInput();
				Number max = filterDialog.getMaxInput();
				boolean isSaturation = filterDialog.isSaturation(); 
				switch (filterDialog.getFilterType()) {
				case COUNT:
					return new GLOFilterCount(geneList, min.intValue(), max.intValue(), isSaturation);
				case PERCENTAGE:
					return new GLOFilterPercentage(geneList, min.doubleValue(), max.doubleValue(), isSaturation);
				case THRESHOLD:
					return new GLOFilterThreshold(geneList, min.doubleValue(), max.doubleValue(), isSaturation);
				case BANDSTOP:
					return new GLOFilterBandStop(geneList, min.doubleValue(), max.doubleValue());
				default:
					throw new IllegalArgumentException("Invalid Saturation Type");
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(GeneList actionResult) {
		if (actionResult != null) {
			selectedTrack.setData(actionResult, operation.getDescription());
		}
	}
}
