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

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOChangeBinSize;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Changes the size of the bins of a {@link BinLayer}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLAChangeBinSize extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 4743270937529673599L;		// generated ID
	private static final String 	ACTION_NAME = "Change Bin Size";		// action name
	private static final String 	DESCRIPTION =
			"Change the size of the bins of the selected layer ";				// tooltip

	private BinLayer 				selectedLayer;							// selected layer

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = BLAChangeBinSize.class.getName();


	/**
	 * Creates an instance of {@link BLAChangeBinSize}
	 */
	public BLAChangeBinSize() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedLayer = (BinLayer) getValue("Layer");
		BinList binList = selectedLayer.getData();
		if (selectedLayer != null) {
			Number binSize = NumberOptionPane.getValue(getTrackListPanel().getRootPane(), "Fixed Window Size", "Enter window size", new DecimalFormat("#"), 0, Integer.MAX_VALUE, 1000);
			if (binSize != null) {
				ScoreCalculationMethod method = Utils.chooseScoreCalculation(getTrackListPanel().getRootPane());
				Operation<BinList> operation = new BLOChangeBinSize(binList, binSize.intValue(), method);
				if (method != null) {
					return operation;
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			selectedLayer.setData(actionResult, operation.getDescription());
		}
	}
}
