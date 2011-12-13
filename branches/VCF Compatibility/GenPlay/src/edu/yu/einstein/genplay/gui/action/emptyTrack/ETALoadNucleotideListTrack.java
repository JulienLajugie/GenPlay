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
package edu.yu.einstein.genplay.gui.action.emptyTrack;

import java.io.File;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.list.chromosomeWindowList.ChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.nucleotideList.TwoBitSequenceList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeSelection.GenomeSelectionDialog;
import edu.yu.einstein.genplay.gui.track.NucleotideListTrack;
import edu.yu.einstein.genplay.gui.trackList.TrackList;
import edu.yu.einstein.genplay.util.Utils;



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
		String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
		selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Sequence Track", defaultDirectory, Utils.getReadableSequenceFileFilters());
		if (selectedFile != null) {
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				GenomeSelectionDialog genomeDialog = new GenomeSelectionDialog(ProjectManager.getInstance().getGenomeSynchronizer().getFormattedGenomeArray());
				if (genomeDialog.showDialog(getRootPane()) == GenomeSelectionDialog.APPROVE_OPTION) {
					genomeName = genomeDialog.getGenomeName();
				} else {
					throw new InterruptedException();
				}
			}
			notifyActionStart("Loading Sequence File", 1, true);
			tbsl = new TwoBitSequenceList(genomeName);
			tbsl.extract(selectedFile);
			return tbsl;
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(TwoBitSequenceList actionResult) {
		boolean valid = true;
		if (ProjectManager.getInstance().isMultiGenomeProject() && genomeName == null) {
			valid = false;
		}
		if (actionResult != null && valid) {
			int selectedTrackIndex = getTrackList().getSelectedTrackIndex();
			ChromosomeWindowList stripes = getTrackList().getSelectedTrack().getStripes();
			NucleotideListTrack newTrack = new NucleotideListTrack(getTrackList().getGenomeWindow(), selectedTrackIndex + 1, actionResult);
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				newTrack.setGenomeName(genomeName);
			}
			getTrackList().setTrack(selectedTrackIndex, newTrack, ProjectManager.getInstance().getProjectConfiguration().getTrackHeight(), selectedFile.getName(), stripes, getTrackList().getSelectedTrack().getStripesList(), getTrackList().getSelectedTrack().getFiltersList());
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
