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

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOCorrelate;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.CorrelationReportDialog;
import edu.yu.einstein.genplay.gui.dialog.TrackChooser;
import edu.yu.einstein.genplay.gui.track.BinListTrack;



/**
 * Computes the coefficient of correlation for every chromosome between 
 * the selected {@link BinListTrack} and another {@link BinListTrack}.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLACorrelate extends TrackListActionOperationWorker<Double[]> {

	private static final long serialVersionUID = -3513622153829181945L; // generated ID
	private static final String 	ACTION_NAME = "Correlation";		// action name
	private static final String 	DESCRIPTION = 
		"Compute the coefficient of correlation between " +
		"the selected track and another track";							// tooltip
	private BinListTrack 			selectedTrack;						// 1st selected track  	
	private BinListTrack 			otherTrack;							// 2nd selected track
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLACorrelate";


	/**
	 * Creates an instance of {@link BLACorrelate}
	 */
	public BLACorrelate() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Double[]> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			otherTrack = (BinListTrack) TrackChooser.getTracks(getRootPane(), "Choose A Track", "Calculate the correlation with:", getTrackList().getBinListTracks());
			if (otherTrack != null) {
				BinList binList1 = selectedTrack.getData();
				BinList binList2 = otherTrack.getData();
				Operation<Double[]> operation = new BLOCorrelate(binList1, binList2);
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Double[] actionResult) {
		if (actionResult != null) {
			CorrelationReportDialog.showDialog(getRootPane(), actionResult, selectedTrack.getName(), otherTrack.getName());
		}
	}
}
