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
package edu.yu.einstein.genplay.gui.action.multiGenome.export;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.concurrent.CountDownLatch;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.operation.ExportEngine;
import edu.yu.einstein.genplay.core.multiGenome.operation.VCF.MGOVCFExportSingleFile;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportSettings;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGAVCFExport extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION =
			"Performs the multi genome algorithm"; 										// tooltip
	private static final int 				MNEMONIC = KeyEvent.VK_M; 				// mnemonic key
	private static		 String 			ACTION_NAME = "Export as VCF";			// action name


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Multi Genome VCF Export";

	private final File file;
	private final ExportSettings settings;
	private boolean success;


	/**
	 * Creates an instance of {@link MGAVCFExport}.
	 * @param file output file to export
	 * @param settings the settings for the export
	 */
	public MGAVCFExport(File file, ExportSettings settings) {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		this.file = file;
		this.settings = settings;
	}


	@Override
	protected Boolean processAction() throws Exception {
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.isMultiGenomeProject()) {

			// Declare the export engine
			ExportEngine exportEngine = null;

			// Initialize the engine if the export is about only one VCF file
			int fileNumber = settings.getFileNumber();
			if (fileNumber == 1) {
				exportEngine = new MGOVCFExportSingleFile();
			} else if (fileNumber > 1) {
				JOptionPane.showMessageDialog(getRootPane(), "Cannot export data from more than one VCF.\nMore support coming soon.", "Export error", JOptionPane.INFORMATION_MESSAGE);
			} else {
				System.err.println("PAMultiGenomeExport.processAction(): Number of file required is not valid: " + fileNumber);
			}

			// Runs the export process
			if ((file != null) && (exportEngine != null)) {
				// Notifies the action
				notifyActionStart(ACTION_NAME, 1, false);
				exportEngine.initializeEngine(settings.getFileMap(), settings.getVariationMap(), settings.getFilterList(), MGDisplaySettings.getInstance().includeReferences(), MGDisplaySettings.getInstance().includeNoCall());
				exportEngine.setPath(file.getPath());

				try {
					exportEngine.compute();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		return false;
	}


	@Override
	protected void doAtTheEnd(Boolean actionResult) {
		success = actionResult;

		if (latch != null) {
			latch.countDown();
		}
	}


	/**
	 * @return true if the action has been correctly finish, false otherwise
	 */
	public boolean hasBeenDone () {
		return success;
	}


	/**
	 * @param latch the latch to set
	 */
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

}