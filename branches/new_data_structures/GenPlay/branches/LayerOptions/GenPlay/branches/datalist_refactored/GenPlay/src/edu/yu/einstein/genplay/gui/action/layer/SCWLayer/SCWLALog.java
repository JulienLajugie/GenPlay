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

import edu.yu.einstein.genplay.core.enums.LogBase;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOLog;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.track.layer.SCWLayer;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Applies a log function to the scores of the selected {@link SCWLayer}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWLALog extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = -7633526345952471304L; // generated ID
	private static final String 	ACTION_NAME = "Log";				// action name
	private static final String 	DESCRIPTION =
			"Apply a log function to the scores of the selected layer";	// tooltip
	private SCWLayer			selectedLayer;							// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = SCWLALog.class.getName();


	/**
	 * Creates an instance of {@link SCWLALog}
	 */
	public SCWLALog() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedLayer = (SCWLayer) getValue("Layer");
		if (selectedLayer != null) {
			LogBase logBase = Utils.chooseLogBase(getRootPane());
			if (logBase != null) {
				ScoredChromosomeWindowList scwList = selectedLayer.getData();
				Operation<ScoredChromosomeWindowList> operation = new SCWLOLog(scwList, logBase);
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null)	{
			selectedLayer.setData(actionResult, operation.getDescription());
		}
	}
}
