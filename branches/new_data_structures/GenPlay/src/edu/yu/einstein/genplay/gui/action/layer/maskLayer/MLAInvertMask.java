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
package edu.yu.einstein.genplay.gui.action.layer.maskLayer;


import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.SCWList.MCWLOInvertMask;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.track.layer.MaskLayer;


/**
 * Reverts a mask reverting mask windows by white spaces and white spaces by mask windows.
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class MLAInvertMask extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long 				serialVersionUID = 4027173438789911860L; 		// generated ID
	private static final String 			ACTION_NAME = "Invert Mask";					// action name
	private static final String 			DESCRIPTION = "Invert the mask layer";			// tooltip
	private MaskLayer 						selectedLayer;									// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = MLAInvertMask.class.getName();


	/**
	 * Creates an instance of {@link MLAInvertMask}
	 */
	public MLAInvertMask() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedLayer = (MaskLayer) getValue("Layer");
		if (selectedLayer != null) {
			ScoredChromosomeWindowList mask = selectedLayer.getData();
			operation = new MCWLOInvertMask(mask);
			return operation;
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			selectedLayer.setData(actionResult, "Mask Inverted");
		}
	}
}
