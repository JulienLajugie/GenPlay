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

import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.nucleotideList.TwoBitSequenceList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.track.NucleotideListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a {@link NucleotideListTrack} in the {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class ETALoadNucleotideListTrack extends TrackListActionWorker<TwoBitSequenceList> {

	private static final long serialVersionUID = 5998366494409991822L;	// generated ID
	private static final String 	ACTION_NAME = "Load Sequence Track";// action name
	private static final String 	DESCRIPTION = "Load a track showing DNA sequences";	// tooltip
	private File 					selectedFile;		// selected file
	private TwoBitSequenceList 		tbsl = null;		// list of sequence
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ETALoadNucleotideListTrack";


	/**
	 * Creates an instance of {@link ETALoadNucleotideListTrack}
	 */
	public ETALoadNucleotideListTrack() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected TwoBitSequenceList processAction() throws Exception {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Sequence Track", defaultDirectory, Utils.getReadableSequenceFileFilters());
		if (selectedFile != null) {
			notifyActionStart("Loading Sequence File", 1, true);
			tbsl = new TwoBitSequenceList();
			tbsl.extract(selectedFile);
			return tbsl;
		}
		return null;
	}
	
	
	@Override
	protected void doAtTheEnd(TwoBitSequenceList actionResult) {
		if (actionResult != null) {
			int selectedTrackIndex = getTrackList().getSelectedTrackIndex();
			ChromosomeWindowList stripes = getTrackList().getSelectedTrack().getStripes();
			NucleotideListTrack newTrack = new NucleotideListTrack(getTrackList().getGenomeWindow(), selectedTrackIndex + 1, actionResult);
			getTrackList().setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedFile.getName(), stripes);
		}
	}
	
	
	/**
	 * Stops the extraction of the list of sequence
	 */
	@Override
	public void stop() {
		if (tbsl != null) {
			tbsl.stop();
		}
		super.stop();
	}
}
