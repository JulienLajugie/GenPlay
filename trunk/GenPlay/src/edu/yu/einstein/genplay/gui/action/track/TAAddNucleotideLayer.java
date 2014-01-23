/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.action.track;

import java.io.File;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.IO.extractor.TwoBitExtractor;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.nucleotideList.TwoBitSequenceList;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.action.TrackListActionExtractorWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeSelection.GenomeSelectionDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.NucleotideLayer;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Adds a {@link NucleotideLayer} to the selected {@link Track}
 * @author Julien Lajugie
 */
public class TAAddNucleotideLayer extends TrackListActionExtractorWorker<TwoBitSequenceList> {

	private static final long serialVersionUID = 5998366494409991822L;					// generated ID
	private static final String 	ACTION_NAME = "Load Sequence Track";				// action name
	private static final String 	DESCRIPTION = "Load a track showing DNA sequences";	// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TAAddNucleotideLayer.class.getName();


	/**
	 * Creates an instance of {@link TAAddNucleotideLayer}
	 */
	public TAAddNucleotideLayer() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(TwoBitSequenceList actionResult) {
		boolean valid = true;
		if (ProjectManager.getInstance().isMultiGenomeProject() && (genomeName == null)) {
			valid = false;
		}
		if ((actionResult != null) && valid) {
			Track selectedTrack = getTrackListPanel().getSelectedTrack();
			NucleotideLayer newLayer = new NucleotideLayer(selectedTrack, actionResult, name);
			selectedTrack.getLayers().add(newLayer);
			selectedTrack.setActiveLayer(newLayer);
		}
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
	protected TwoBitSequenceList generateList() throws Exception {
		notifyActionStart("Generating DNA Sequence Layer", 1, true);
		try {
			TwoBitExtractor twoBitExtractor = (TwoBitExtractor) extractor;
			twoBitExtractor.extract(genomeName, alleleType);
			return new TwoBitSequenceList(fileToExtract.getAbsolutePath(), twoBitExtractor.needToReverseBytes(), genomeName, alleleType, twoBitExtractor.getExtractedData());
		} catch (ClassCastException e) {
			throw new InvalidFileTypeException();
		}
	}


	@Override
	protected File retrieveFileToExtract() {
		String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load DNA Sequence File", defaultDirectory, Utils.getReadableSequenceFileFilters(), true);
		if (selectedFile != null) {
			return selectedFile;
		}
		return null;
	}


	/**
	 * Stops the extraction of the list of sequence
	 */
	@Override
	public void stop() {
		if (extractor != null) {
			extractor.stop();
		}
		super.stop();
	}
}
