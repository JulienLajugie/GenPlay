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
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOMax;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.track.layer.SCWLayer;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Shows the maximum score of the selected {@link SCWLayer}
 * @author Julien Lajugie
 */
public final class SCWLAMax extends TrackListActionOperationWorker<Float> {

	private static final long serialVersionUID = -3864460354387970028L;	// generated ID
	private static final String 	ACTION_NAME = "Maximum";			// action name
	private static final String 	DESCRIPTION =
			"Show the maximum score of the selected layer";				// tooltip
	private SCWLayer 				selectedLayer;						// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = SCWLAMax.class.getName();


	/**
	 * Creates an instance of {@link SCWLAMax}
	 */
	public SCWLAMax() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(Float actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), actionResult, "Maximum of \"" + selectedLayer.getName() +"\":", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	@Override
	public Operation<Float> initializeOperation() {
		selectedLayer = (SCWLayer) getValue("Layer");
		if (selectedLayer != null) {
			boolean[] selectedChromo = Utils.chooseChromosomes(getRootPane());
			if (selectedChromo != null) {
				SCWList scwList = selectedLayer.getData();
				Operation<Float> operation = new SCWLOMax(scwList, selectedChromo);
				return operation;
			}
		}
		return null;
	}
}
