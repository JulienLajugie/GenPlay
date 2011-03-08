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
package yu.einstein.gdp2.gui.action.allTrack;

import java.io.File;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.generator.ChromosomeWindowListGenerator;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionExtractorWorker;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.util.Utils;


/**
 * Sets the stripes on the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ATALoadStripes extends TrackListActionExtractorWorker<ChromosomeWindowList> {

	private static final long serialVersionUID = -900140642202561851L; // generated ID
	private static final String ACTION_NAME = "Load Stripes"; // action name
	private static final String DESCRIPTION = "Load stripes on the selected track"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATALoadStripes";


	/**
	 * Creates an instance of {@link ATALoadStripes}
	 */
	public ATALoadStripes() {
		super(ChromosomeWindowListGenerator.class);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected File retrieveFileToExtract() {
		Track<?> selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
			File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Stripe File", defaultDirectory, Utils.getReadableStripeFileFilters());
			if (selectedFile != null) {
				return selectedFile;
			}
		}
		return null;
	}

	
	@Override
	public ChromosomeWindowList generateList() throws Exception {
		return ((ChromosomeWindowListGenerator)extractor).toChromosomeWindowList();
	}
	
	
	@Override
	protected void doAtTheEnd(ChromosomeWindowList actionResult) {
		if (actionResult != null) {
			getTrackList().getSelectedTrack().setStripes(actionResult);
		}
	}
}
