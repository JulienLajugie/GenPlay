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

import edu.yu.einstein.genplay.core.generator.GeneListGenerator;
import edu.yu.einstein.genplay.core.list.chromosomeWindowList.ChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.action.TrackListActionExtractorWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeSelection.GenomeSelectionDialog;
import edu.yu.einstein.genplay.gui.track.GeneListTrack;
import edu.yu.einstein.genplay.gui.trackList.TrackList;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Loads a {@link GeneListTrack} in the {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ETALoadGeneListTrack extends TrackListActionExtractorWorker<GeneList> {

	private static final long serialVersionUID = -6264760599336397028L;	// generated ID
	private static final String 	ACTION_NAME = "Load Gene Track";	// action name
	private static final String 	DESCRIPTION = "Load a track showing the genes";	// tooltip
	

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ETALoadGeneListTrack";


	/**
	 * Creates an instance of {@link ETALoadGeneListTrack}
	 */
	public ETALoadGeneListTrack() {
		super(GeneListGenerator.class);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}
	
	
	@Override
	protected void doBeforeExtraction() throws InterruptedException {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			GenomeSelectionDialog genomeDialog = new GenomeSelectionDialog(ProjectManager.getInstance().getMultiGenome().getFormattedGenomeArray());
			if (genomeDialog.showDialog(getRootPane()) == GenomeSelectionDialog.APPROVE_OPTION) {
				genomeName = genomeDialog.getGenomeName();
				alleleType = genomeDialog.getAlleleType();
			} else {
				throw new InterruptedException();
			}
		}
	}


	@Override
	protected File retrieveFileToExtract() {
		String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Gene Track", defaultDirectory, Utils.getReadableGeneFileFilters());
		if (selectedFile != null) {
			return selectedFile;
		}
		return null;
	}

	
	@Override
	protected GeneList generateList() throws Exception {
		return ((GeneListGenerator)extractor).toGeneList();
	}
	

	@Override
	public void doAtTheEnd(GeneList actionResult) {
		boolean valid = true;
		if (ProjectManager.getInstance().isMultiGenomeProject() && genomeName == null) {
			valid = false;
		}
		if (actionResult != null && valid) {
			TrackList trackList = getTrackList();
			int selectedTrackIndex = trackList.getSelectedTrackIndex();
			ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
			GeneListTrack newTrack;
			newTrack = new GeneListTrack(trackList.getGenomeWindow(), selectedTrackIndex + 1, actionResult);
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				newTrack.setGenomeName(genomeName);
			}
			trackList.setTrack(selectedTrackIndex, newTrack, ProjectManager.getInstance().getProjectConfiguration().getTrackHeight(), name, stripes, getTrackList().getSelectedTrack().getStripesList(), getTrackList().getSelectedTrack().getFiltersList());
		}
	}
}
