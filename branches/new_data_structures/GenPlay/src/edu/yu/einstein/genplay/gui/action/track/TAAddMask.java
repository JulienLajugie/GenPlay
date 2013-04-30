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

import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWListFactory;
import edu.yu.einstein.genplay.gui.action.TrackListActionExtractorWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeSelection.GenomeSelectionDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.MaskLayer;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Adds a mask layer to the selected track
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public final class TAAddMask extends TrackListActionExtractorWorker<SCWList> {

	private static final long serialVersionUID = -900140642202561851L; 					// generated ID
	private static final String ACTION_NAME = "Add Mask Layer"; 						// action name
	private static final String DESCRIPTION = "Add a mask layer to the selected track"; // tooltip
	private Track 				selectedTrack; 											// selected track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TAAddMask.class.getName();


	/**
	 * Creates an instance of {@link TAAddMask}
	 */
	public TAAddMask() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(SCWList actionResult) {
		if (actionResult != null) {
			MaskLayer newLayer = new MaskLayer(selectedTrack, actionResult, fileToExtract.getName());
			newLayer.getHistory().add("Load " + fileToExtract.getAbsolutePath(), Colors.GREY);
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
	public SCWList generateList() throws Exception {
		SCWList maskList = SimpleSCWListFactory.createMaskSCWList((SCWReader) extractor, null);
		return maskList;
	}


	@Override
	protected File retrieveFileToExtract() {
		selectedTrack = getTrackListPanel().getSelectedTrack();
		if (selectedTrack != null) {
			String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
			File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Mask File", defaultDirectory, Utils.getReadableStripeFileFilters(), true);
			if (selectedFile != null) {
				return selectedFile;
			}
		}
		return null;
	}
}
