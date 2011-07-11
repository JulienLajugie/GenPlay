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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOCountNonNullBins;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.ChromosomeChooser;
import edu.yu.einstein.genplay.gui.track.BinListTrack;



/**
 * Returns the number of non-null bins.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLACountNonNullBins extends TrackListActionOperationWorker<Long> {

	private static final long serialVersionUID = 384900915791989265L;	// generated ID
	private static final String 	ACTION_NAME = "Bin Count";			// action name
	private static final String 	DESCRIPTION = 
		"Return the number of non-null bins on the " +
		"selected chromosomes of the selected track";					// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLACountNonNullBins";


	/**
	 * Creates an instance of {@link BLACountNonNullBins}
	 */
	public BLACountNonNullBins() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Long> initializeOperation() {
		BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane());
			if (selectedChromo != null) {
				BinList binList = selectedTrack.getData();
				Operation<Long> operation = new BLOCountNonNullBins(binList, selectedChromo);
				return operation;
			}
		}
		return null;
	}
	
	
	@Override
	protected void doAtTheEnd(Long actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), "Number of non-null bins: \n" + new DecimalFormat("###,###,###,###").format(actionResult), "Number of Bins", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
