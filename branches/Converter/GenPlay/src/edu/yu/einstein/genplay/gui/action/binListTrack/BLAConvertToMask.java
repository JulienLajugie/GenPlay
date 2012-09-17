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


import java.util.List;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.list.SCWList.MaskWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOConvertToMask;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes.StripesData;
import edu.yu.einstein.genplay.gui.track.EmptyTrack;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * Converts a {@link BinList} to a {@link MaskWindowList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class BLAConvertToMask extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long 				serialVersionUID = 4027173438789911860L; 		// generated ID
	private static final String 			ACTION_NAME = "Convert to Mask";				// action name
	private static final String 			DESCRIPTION = "Convert the track into a mask";	// tooltip
	private Track<?> 						selectedTrack;									// selected track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLConvertToMask";


	/**
	 * Creates an instance of {@link BLAConvertToMask}
	 */
	public BLAConvertToMask() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			if ((selectedTrack.getData() != null) && (selectedTrack.getData() instanceof BinList)) {
				BinList data = (BinList) selectedTrack.getData();
				operation = new BLOConvertToMask(data);
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			int index = selectedTrack.getTrackNumber() - 1;
			EmptyTrack newTrack = new EmptyTrack(index + 1);

			List<StripesData> stripesList = selectedTrack.getStripesList();
			List<MGFilter> filtersList = selectedTrack.getFiltersList();

			getTrackList().setTrack(index, newTrack, ProjectManager.getInstance().getProjectConfiguration().getTrackHeight(), selectedTrack.getName() + " as mask", actionResult, stripesList, filtersList);
		}
	}

}
