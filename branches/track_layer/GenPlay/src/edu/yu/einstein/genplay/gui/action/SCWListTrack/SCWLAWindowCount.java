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
package edu.yu.einstein.genplay.gui.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOWindowCount;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.ChromosomeChooser;
import edu.yu.einstein.genplay.gui.old.track.SCWListTrack;


/**
 * Counts the number of windows in a {@link ScoredChromosomeWindowList}
 * @author Nicolas Fourel
 */
public class SCWLAWindowCount extends TrackListActionOperationWorker<Long> {

	private static final long serialVersionUID = -1773399821513504625L;						// generated ID
	private static final String 	ACTION_NAME = "Window Count";					// action name
	private static final String 	DESCRIPTION = "Sum of the total number of windows";		// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLACountWindows";


	/**
	 * Creates an instance of {@link SCWLAWindowCount}
	 */
	public SCWLAWindowCount() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Long> initializeOperation() {
		SCWListTrack selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane());
			if (selectedChromo != null) {
				ScoredChromosomeWindowList scwList = selectedTrack.getData();
				Operation<Long> operation = new SCWLOWindowCount(scwList, selectedChromo);
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Long actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), "Total number of windows: \n" + new DecimalFormat("###,###,###,###").format(actionResult), "Windows number", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
