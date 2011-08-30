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
package edu.yu.einstein.genplay.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOChangeBinSize;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.track.BinListTrack;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Changes the size of the bins of a {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLAChangeBinSize extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 4743270937529673599L;		// generated ID
	private static final String 	ACTION_NAME = "Change Bin Size";		// action name
	private static final String 	DESCRIPTION = 
		"Change the size of the bins of the selected track ";				// tooltip
	private BinListTrack 			selectedTrack;							// selected track

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAChangeBinSize";


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
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		BinList binList = selectedTrack.getData();
		if (selectedTrack != null) {
			Number binSize = NumberOptionPane.getValue(getTrackList().getRootPane(), "Fixed Window Size", "Enter window size", new DecimalFormat("#"), 0, Integer.MAX_VALUE, 1000);
			if (binSize != null) {
				ScoreCalculationMethod method = Utils.chooseScoreCalculation(getTrackList().getRootPane());
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
			selectedTrack.setData(actionResult, operation.getDescription());
		}
	}
}
