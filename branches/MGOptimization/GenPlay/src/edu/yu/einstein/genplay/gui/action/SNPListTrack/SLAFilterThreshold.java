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
package edu.yu.einstein.genplay.gui.action.SNPListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.SNPList.SNPList;
import edu.yu.einstein.genplay.core.SNPList.operation.SLOFilterThresholds;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.TwoNumbersOptionPane;
import edu.yu.einstein.genplay.gui.track.SNPListTrack;
import edu.yu.einstein.genplay.gui.track.Track;



/**
 * Removes the SNPs where the first and second base counts are smaller than specified thresholds
 * @author Julien Lajugie
 * @version 0.1
 */
public class SLAFilterThreshold extends TrackListActionOperationWorker<SNPList> {

	private static final long serialVersionUID = 3111075603423951805L;	// generated ID
	private static final String 	ACTION_NAME = "Threshold Filter";		// action name
	private static final String 	DESCRIPTION = 
		"Removes the SNPs where the first and second base counts " +
		"are smaller than specified thresholds";						// tooltip
	private Track<?> 				selectedTrack;						// selected track
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SLAFilterThreshold";


	/**
	 * Creates an instance of {@link SLAFilterThreshold}
	 */
	public SLAFilterThreshold() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}

	
	@Override
	public Operation<SNPList> initializeOperation() throws Exception {
		selectedTrack = getTrackList().getSelectedTrack();
		if ((selectedTrack != null) && (selectedTrack.getData() instanceof SNPList)) {
			SNPList inputList = (SNPList) selectedTrack.getData();
			Number[] thresholds = TwoNumbersOptionPane.getValue(getRootPane(), "Thresholds", "Remove SNPs with a first base count smaller than", 
					"And the second base count smaller than", new DecimalFormat("###,###,###"), 0, Integer.MAX_VALUE, 0, 0);
			if (thresholds != null) {
				Operation<SNPList> operation = new SLOFilterThresholds(inputList, thresholds[0].intValue(), thresholds[1].intValue());
				return operation;
			}
		}
		return null;
	}

	
	@Override
	protected void doAtTheEnd(SNPList actionResult) {
		if (actionResult != null) {
			int index = selectedTrack.getTrackNumber() - 1;
			Track<?> newTrack = new SNPListTrack(index + 1, actionResult);
			getTrackList().setTrack(index, newTrack, ProjectManager.getInstance().getProjectConfiguration().getTrackHeight(), selectedTrack.getName() + " filtered", selectedTrack.getMask(), selectedTrack.getStripesList(), selectedTrack.getFiltersList());
		}		
	}
}
