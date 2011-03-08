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
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.io.File;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.SNPList.SNPList;
import yu.einstein.gdp2.core.generator.SNPListGenerator;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionExtractorWorker;
import yu.einstein.gdp2.gui.track.SNPListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a {@link SNPListTrack} in the {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class ETALoadSNPListTrack extends TrackListActionExtractorWorker<SNPList> {

	private static final long serialVersionUID = -2828875849368222868L; // generated ID
	private static final String 	ACTION_NAME = "Load SNP Track";	// action name
	private static final String 	DESCRIPTION = "Load a track showing the SNPs";	// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ETALoadSNPListTrack";
	
	
	/**
	 * Creates an instance of {@link ETALoadSNPListTrack}
	 */
	public ETALoadSNPListTrack() {
		super(SNPListGenerator.class);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}

	
	@Override
	public void doAtTheEnd(SNPList actionResult) {
		if (actionResult != null) {
			TrackList trackList = getTrackList();
			int selectedTrackIndex = trackList.getSelectedTrackIndex();
			ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
			SNPListTrack newTrack = new SNPListTrack(trackList.getGenomeWindow(), selectedTrackIndex + 1, actionResult);
			trackList.setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), name, stripes);
		}
	}
	
	
	@Override
	protected SNPList generateList() throws Exception {
		return ((SNPListGenerator) extractor).toSNPList();
	}

	
	@Override
	protected File retrieveFileToExtract() {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load SNP Track", defaultDirectory, Utils.getReadableSNPFileFilters());
		if (selectedFile != null) {
			return selectedFile;
		}
		return null;
	}
}
