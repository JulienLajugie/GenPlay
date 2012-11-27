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
package edu.yu.einstein.genplay.gui.action.maskTrack;

import java.io.File;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.generator.ScoredChromosomeWindowListGenerator;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.action.TrackListActionExtractorWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeSelection.GenomeSelectionDialog;
import edu.yu.einstein.genplay.gui.old.track.Track;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Sets the mask on the selected track
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class MTALoadMask extends TrackListActionExtractorWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = -900140642202561851L; // generated ID
	private static final String ACTION_NAME = "Load Mask"; // action name
	private static final String DESCRIPTION = "Load mask on the selected track"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATALoadMask";


	/**
	 * Creates an instance of {@link MTALoadMask}
	 */
	public MTALoadMask() {
		super(ScoredChromosomeWindowListGenerator.class);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doBeforeExtraction() throws InterruptedException {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			GenomeSelectionDialog genomeDialog = new GenomeSelectionDialog();
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
		Track<?> selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
			File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Mask File", defaultDirectory, Utils.getReadableStripeFileFilters(), true);
			if (selectedFile != null) {
				return selectedFile;
			}
		}
		return null;
	}


	@Override
	public ScoredChromosomeWindowList generateList() throws Exception {
		//return ((ChromosomeWindowListGenerator)extractor).toChromosomeWindowList();
		return ((ScoredChromosomeWindowListGenerator)extractor).toMaskChromosomeWindowList();
	}


	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			getTrackList().getSelectedTrack().setMask(actionResult);
		}
	}

}
