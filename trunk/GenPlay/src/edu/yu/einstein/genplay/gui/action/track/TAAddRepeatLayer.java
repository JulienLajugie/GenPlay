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

import java.io.File;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.IO.dataReader.RepeatReader;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList.SimpleRepeatFamilyListFactory;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.action.TrackListActionExtractorWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeSelection.GenomeSelectionDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.RepeatLayer;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Adds a {@link RepeatLayer} to the selected {@link Track}
 * @author Julien Lajugie
 */
public final class TAAddRepeatLayer extends TrackListActionExtractorWorker<RepeatFamilyList> {

	private static final long serialVersionUID = -6264760599336397028L;	// generated ID
	private static final String ACTION_NAME = "Add Repeat Layer";		// action name
	private static final String DESCRIPTION = "Add a layer displaying families repeats";	// tooltip

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TAAddRepeatLayer.class.getName();


	/**
	 * Creates an instance of {@link TAAddRepeatLayer}
	 */
	public TAAddRepeatLayer() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void doAtTheEnd(RepeatFamilyList actionResult) {
		boolean valid = true;
		if (ProjectManager.getInstance().isMultiGenomeProject() && (genomeName == null)) {
			valid = false;
		}
		if ((actionResult != null) && valid) {
			Track selectedTrack = getTrackListPanel().getSelectedTrack();
			RepeatLayer newLayer = new RepeatLayer(selectedTrack, actionResult, name);
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
	protected RepeatFamilyList generateList() throws Exception {
		try {
			RepeatFamilyList repeatList = null;
			notifyActionStart("Generating Repeat Layer", 1, true);
			repeatList = SimpleRepeatFamilyListFactory.createRepeatList((RepeatReader) extractor);
			return repeatList;
		} catch (ClassCastException e) {
			throw new InvalidFileTypeException();
		}
	}


	@Override
	protected File retrieveFileToExtract() {
		String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Repeat Layer", defaultDirectory, Utils.getReadableRepeatFileFilters(), true);
		if (selectedFile != null) {
			return selectedFile;
		}
		return null;
	}
}
