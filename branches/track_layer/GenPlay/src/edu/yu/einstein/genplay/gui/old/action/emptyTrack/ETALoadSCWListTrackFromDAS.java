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
package edu.yu.einstein.genplay.gui.old.action.emptyTrack;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.DAS.DASConnector;
import edu.yu.einstein.genplay.core.DAS.DASType;
import edu.yu.einstein.genplay.core.DAS.DataSource;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.dialog.DASDialog.DASDialog;
import edu.yu.einstein.genplay.gui.old.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.old.track.SCWListTrack;
import edu.yu.einstein.genplay.util.colors.Colors;
import edu.yu.einstein.genplay.util.colors.TrackColor;


/**
 * Loads a SCWList track from data retrieve from a DAS server
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public class ETALoadSCWListTrackFromDAS extends TrackListActionWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = 8520156015849830140L; // generated ID
	private final DataSource 	dataSource;			// DAS data source
	private final DASConnector 	dasConnector;		// DAS connector
	private final DASType 		dasType;			// DAS type
	private final int 			dataRange;			// enum representing the type of range (genome wide / current range / user defined)
	private final GenomeWindow 	genomeWindow;		// genome window defined by the user
	private final GenomeWindow 	currentWindow;		// current genome window
	private final int 			selectedTrackIndex;	// index of the selected track


	/**
	 * Creates an instance of {@link ETALoadGeneListTrackFromDAS}
	 * @param dataSource DAS data source
	 * @param dasConnector DAS connector
	 * @param dasType DAS type
	 * @param dataRange enum representing the type of range (genome wide / current range / user defined)
	 * @param genomeWindow genome window defined by the user
	 * @param currentWindow current genome window
	 * @param selectedTrackIndex index of the selected track
	 */
	public ETALoadSCWListTrackFromDAS(DataSource dataSource, DASConnector dasConnector, DASType dasType, int dataRange,
			GenomeWindow genomeWindow, GenomeWindow currentWindow, int selectedTrackIndex) {
		this.dataSource = dataSource;
		this.dasConnector = dasConnector;
		this.dasType = dasType;
		this.dataRange	= 	dataRange;
		this.genomeWindow = genomeWindow;
		this.currentWindow = currentWindow;
		this.selectedTrackIndex = selectedTrackIndex;
	}


	@Override
	protected ScoredChromosomeWindowList processAction() throws Exception {
		notifyActionStart("Loading From DAS Server", 1, false);
		if(dataRange == DASDialog.GENERATE_GENOMEWIDE_LIST) {
			return dasConnector.getSCWList(dataSource, dasType);
		}
		else if(dataRange == DASDialog.GENERATE_USER_SPECIFIED_LIST) {
			if(genomeWindow.getStop() < genomeWindow.getStart()) {
				throw new Exception("Invalid Start Stop Range");
			}
			return dasConnector.getSCWList(dataSource, dasType, genomeWindow);
		}
		else if(dataRange == DASDialog.GENERATE_CURRENT_LIST) {
			return dasConnector.getSCWList(dataSource, dasType, currentWindow);
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
		    	ScoredChromosomeWindowList stripes = getTrackList().getSelectedTrack().getMask();
			SCWListTrack newTrack = new SCWListTrack(selectedTrackIndex + 1, actionResult);
			newTrack.getHistory().add("Load From DAS Server", Colors.GREY);
			newTrack.setTrackColor(TrackColor.getTrackColor());
			getTrackList().setTrack(selectedTrackIndex, newTrack, ProjectManager.getInstance().getProjectConfiguration().getTrackHeight(), dasType.getID(), stripes, getTrackList().getSelectedTrack().getStripesList(), getTrackList().getSelectedTrack().getFiltersList());
		}
	}
}
