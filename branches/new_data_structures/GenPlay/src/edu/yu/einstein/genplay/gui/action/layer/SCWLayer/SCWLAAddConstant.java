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
package edu.yu.einstein.genplay.gui.action.layer.SCWLayer;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOAddConstant;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.track.layer.AbstractSCWLayer;


/**
 * Adds a constant to the scores of the selected {@link AbstractSCWLayer}
 * @author Julien Lajugie
 */
public final class SCWLAAddConstant extends TrackListActionOperationWorker<SCWList> {

	private static final long serialVersionUID = 4027173438789911860L; 	// generated ID
	private static final String 	ACTION_NAME = "Addition (Constant)";// action name
	private static final String 	DESCRIPTION =
			"Add a constant to the scores of the selected layer";		// tooltip
	private AbstractSCWLayer<SCWList>				selectedLayer;						// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = SCWLAAddConstant.class.getName();


	/**
	 * Creates an instance of {@link SCWLAAddConstant}
	 */
	public SCWLAAddConstant() {
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
		selectedLayer =  (AbstractSCWLayer<SCWList>) getValue("Layer");
		if (selectedLayer != null) {
			Number constant = NumberOptionPane.getValue(getRootPane(), "Constant", "Enter a value C to add: f(x)=x + C", Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0);
			if ((constant != null) && (constant.floatValue() != 0)) {
				SCWList scwList = selectedLayer.getData();
				operation = new SCWLOAddConstant(scwList, constant.floatValue());
				return operation;
			}
		}
		return null;
	}
}
