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
package edu.yu.einstein.genplay.gui.action.track;

import edu.yu.einstein.genplay.core.DAS.DASConnector;
import edu.yu.einstein.genplay.core.DAS.DASType;
import edu.yu.einstein.genplay.core.DAS.DataSource;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.SimpleGenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.DASDialog.DASDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.SimpleSCWLayer;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Adds a {@link SimpleSCWLayer} from data retrieve from a DAS server
 * @author Julien Lajugie
 * @author Chirag Gorasia
 */
public class TAAddSCWLayerFromDAS extends TrackListActionWorker<SCWList> {

	private static final long serialVersionUID = 8520156015849830140L; // generated ID
	private final DataSource 			dataSource;		// DAS data source
	private final DASConnector 			dasConnector;	// DAS connector
	private final DASType 				dasType;		// DAS type
	private final int 					dataRange;		// enum representing the type of range (genome wide / current range / user defined)
	private final SimpleGenomeWindow 	genomeWindow;	// genome window defined by the user
	private final SimpleGenomeWindow 	currentWindow;	// current genome window
	private final Track 				selectedTrack;	// selected track


	/**
	 * Creates an instance of {@link TAAddGeneLayerFromDAS}
	 * @param dataSource DAS data source
	 * @param dasConnector DAS connector
	 * @param dasType DAS type
	 * @param dataRange enum representing the type of range (genome wide / current range / user defined)
	 * @param genomeWindow genome window defined by the user
	 * @param currentWindow current genome window
	 * @param selectedTrack selected track
	 */
	public TAAddSCWLayerFromDAS(DataSource dataSource, DASConnector dasConnector, DASType dasType, int dataRange,
			SimpleGenomeWindow genomeWindow, SimpleGenomeWindow currentWindow, Track selectedTrack) {
		this.dataSource = dataSource;
		this.dasConnector = dasConnector;
		this.dasType = dasType;
		this.dataRange	= 	dataRange;
		this.genomeWindow = genomeWindow;
		this.currentWindow = currentWindow;
		this.selectedTrack = selectedTrack;
	}


	@Override
	protected void doAtTheEnd(SCWList actionResult) {
		if (actionResult != null) {
			SimpleSCWLayer newLayer = new SimpleSCWLayer(selectedTrack, actionResult, dataSource.getName());
			newLayer.getHistory().add("Load " + dataSource.getName() + " From DAS Server", Colors.GREY);
			selectedTrack.getLayers().add(newLayer);
			selectedTrack.setActiveLayer(newLayer);
		}
	}


	@Override
	protected SCWList processAction() throws Exception {
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
}
