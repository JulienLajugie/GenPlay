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
package edu.yu.einstein.genplay.gui.action.layer.binlayer;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.binList.BLOChangeDataPrecision;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Changes the {@link ScorePrecision} of a {@link BinLayer}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLAChangeDataPrecision extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 259517972989514480L;		// generated ID
	private static final String 	ACTION_NAME = "Change Precision";		// action name
	private static final String 	DESCRIPTION =
			"Change the precision of the data of the selected layer";			// tooltip

	private BinLayer 				selectedLayer;							// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = BLAChangeDataPrecision.class.getName();


	/**
	 * Creates an instance of {@link BLAChangeDataPrecision}
	 */
	public BLAChangeDataPrecision() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			selectedLayer.setData(actionResult, operation.getDescription());
		}
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedLayer = (BinLayer) getValue("Layer");
		if (selectedLayer != null) {
			BinList binList = selectedLayer.getData();
			ScorePrecision precision = Utils.choosePrecision(getRootPane(), binList.getScorePrecision());
			if (precision != null) {
				Operation<BinList> operation = new BLOChangeDataPrecision(binList, precision);
				return operation;
			}
		}
		return null;
	}
}
