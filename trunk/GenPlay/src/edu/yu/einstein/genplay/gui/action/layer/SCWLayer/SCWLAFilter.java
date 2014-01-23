/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.action.layer.SCWLayer;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOFilterBandStop;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOFilterCount;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOFilterPercentage;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOFilterThreshold;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.filterDialog.FilterDialog;
import edu.yu.einstein.genplay.gui.track.layer.AbstractSCWLayer;


/**
 * Filters the {@link AbstractSCWLayer}.
 * Different kind of filters are availables
 * @author Julien Lajugie
 */
public class SCWLAFilter extends TrackListActionOperationWorker<SCWList> {

	private static final long serialVersionUID = 960963269753754801L;	// generated ID
	private static final String 	ACTION_NAME = "Filter";				// action name
	private static final String 	DESCRIPTION =
			"Filter the selected layer";								// tooltip
	private AbstractSCWLayer<SCWList> selectedLayer;						// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = SCWLAFilter.class.getName();


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
	protected void doAtTheEnd(SCWList actionResult) {
		if (actionResult != null) {
			selectedLayer.setData(actionResult, operation.getDescription());
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public Operation<SCWList> initializeOperation() {
		selectedLayer = (AbstractSCWLayer<SCWList>) getValue("Layer");
		if (selectedLayer != null) {
			FilterDialog filterDialog = new FilterDialog();
			if (filterDialog.showFilterDialog(getRootPane()) == FilterDialog.APPROVE_OPTION) {
				SCWList list = selectedLayer.getData();
				Number min = filterDialog.getMinInput();
				Number max = filterDialog.getMaxInput();
				boolean isSaturation = filterDialog.isSaturation();
				switch (filterDialog.getFilterType()) {
				case COUNT:
					return new SCWLOFilterCount(list, min.intValue(), max.intValue(), isSaturation);
				case PERCENTAGE:
					return new SCWLOFilterPercentage(list, min.floatValue(), max.floatValue(), isSaturation);
				case THRESHOLD:
					return new SCWLOFilterThreshold(list, min.floatValue(), max.floatValue(), isSaturation);
				case BANDSTOP:
					return new SCWLOFilterBandStop(list, min.floatValue(), max.floatValue());
				default:
					throw new IllegalArgumentException("Invalid Saturation Type");
				}
			}

		}
		return null;
	}
}
