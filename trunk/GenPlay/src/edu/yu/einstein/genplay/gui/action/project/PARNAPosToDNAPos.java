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
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.Component;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.yu.einstein.genplay.core.RNAPosToDNAPos.GeneRelativeToGenomePosition;
import edu.yu.einstein.genplay.dataStructure.enums.RNAToDNAResultType;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.RNAPosToDNAPosOutputFileTypeDialog;
import edu.yu.einstein.genplay.gui.fileFilter.BedFilter;
import edu.yu.einstein.genplay.gui.fileFilter.BedGraphFilter;
import edu.yu.einstein.genplay.util.FileChooser;

/**
 * Replaces positions relative to a reference (RNA) to DNA positions
 * @author Julien Lajugie
 */
public final class PARNAPosToDNAPos extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 8927411528919859767L; // generated ID
	private static final String DESCRIPTION = "Replace positions relative to a reference (RNA) to DNA positions"; // tooltip
	private static final String ACTION_NAME = "RNA To DNA Reference"; // action name
	private final Component 	parent; 			// parent component
	private RNAToDNAResultType	outputFileType; 	// output file type

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = PARNAPosToDNAPos.class.getName();


	/**
	 * Creates an instance of {@link PARNAPosToDNAPos}
	 * 
	 * @param parent
	 *            parent component
	 */
	public PARNAPosToDNAPos(Component parent) {
		super();
		this.parent = parent;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected Void processAction() throws Exception {
		File fileRef;
		File fileData;
		File fileOutput = null;

		FileNameExtensionFilter textFileFilter = new FileNameExtensionFilter("Text file (*.txt)", "txt");
		FileFilter[] fileFilters1 = { textFileFilter, new BedGraphFilter() };
		fileData = FileChooser.chooseFile(parent, FileChooser.OPEN_FILE_MODE, "Select Coverage File", fileFilters1, true);
		if (fileData != null) {
			FileFilter[] fileFilters2 = { textFileFilter, new BedFilter() };
			fileRef = FileChooser.chooseFile(parent, FileChooser.OPEN_FILE_MODE, "Select Reference File", fileFilters2, true);
			if (fileRef != null) {
				RNAPosToDNAPosOutputFileTypeDialog rnaToDnaDialog = new RNAPosToDNAPosOutputFileTypeDialog();
				int rtddResult = rnaToDnaDialog.showDialog(getRootPane());
				outputFileType = rnaToDnaDialog.getSelectedOutputFileType();
				if (rtddResult == RNAPosToDNAPosOutputFileTypeDialog.APPROVE_OPTION) {
					fileOutput = FileChooser.chooseFile(parent, FileChooser.SAVE_FILE_MODE, "Select Output BGR File", new FileFilter[] {new BedGraphFilter()}, false);
					if (fileOutput != null) {
						final GeneRelativeToGenomePosition grtgp = new GeneRelativeToGenomePosition(fileData, fileRef, fileOutput, outputFileType);
						notifyActionStart("Generating Output Files", 1, false);
						grtgp.rePosition();
					}
				}
			}
		}
		return null;
	}
}
