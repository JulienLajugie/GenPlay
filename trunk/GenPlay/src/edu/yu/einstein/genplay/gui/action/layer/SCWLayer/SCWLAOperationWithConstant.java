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
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOOperationWithConstant;
import edu.yu.einstein.genplay.dataStructure.enums.OperationWithConstant;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.OperationWithConstantDialog;
import edu.yu.einstein.genplay.gui.track.layer.AbstractSCWLayer;


/**
 * Performs an arithmetic operation on the score of the selected {@link AbstractSCWLayer}
 * @author Julien Lajugie
 */
public class SCWLAOperationWithConstant extends TrackListActionOperationWorker<SCWList> {

	private static final long serialVersionUID = 4027173438789911860L; 								// generated ID
	private static final String 		ACTION_NAME = "Constant Operation";							// action name
	private static final String 		DESCRIPTION =
			"Performs an arithmetic operation with a constant on the scores of the selected layer";	// tooltip
	private static final String			HELP_URL = "http://genplay.einstein.yu.edu/wiki/index.php/Documentation#Constant_Operation";
	private AbstractSCWLayer<SCWList>	selectedLayer;												// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = SCWLAOperationWithConstant.class.getName();


	/**
	 * Creates an instance of {@link SCWLAOperationWithConstant}
	 */
	public SCWLAOperationWithConstant() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(HELP_URL_KEY, HELP_URL);
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
			OperationWithConstantDialog owcDialog = new OperationWithConstantDialog();
			if (owcDialog.showDialog(getRootPane()) == OperationWithConstantDialog.APPROVE_OPTION) {
				float constant = owcDialog.getConstant();
				OperationWithConstant operationWithConstant = owcDialog.getOperation();
				boolean applyToNullWindows = owcDialog.getApplyToNullWindows();
				SCWList scwList = selectedLayer.getData();
				return new SCWLOOperationWithConstant(scwList, operationWithConstant, constant, applyToNullWindows);
			}
		}
		return null;
	}
}
