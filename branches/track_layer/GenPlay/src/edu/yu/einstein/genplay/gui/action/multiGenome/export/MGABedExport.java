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
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.operation.ExportEngine;
import edu.yu.einstein.genplay.core.multiGenome.operation.BED.MGOBedExportSingleFile;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportSettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.export.ExportBEDDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.mainDialog.MultiGenomeTrackActionDialog;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.old.track.Track;
import edu.yu.einstein.genplay.gui.old.track.drawer.multiGenome.MultiGenomeDrawer;


/**
 * TEMPORARY UNUSED
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGABedExport extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION =
			"Export VCF stripes as BED file(s)"; 										// tooltip
	private static final int 				MNEMONIC = KeyEvent.VK_M; 				// mnemonic key
	private static		 String 			ACTION_NAME = "Export as BED";			// action name


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Multi Genome BED Export";

	private ExportEngine exportEngine;
	private boolean hasBeenCancelled = false;

	/**
	 * Creates an instance of {@link MGABedExport}.
	 */
	public MGABedExport() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected Boolean processAction() throws Exception {

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.isMultiGenomeProject()) {

			// Get track information
			Track<?> track = MainFrame.getInstance().getTrackList().getSelectedTrack();
			MultiGenomeDrawer genomeDrawer = track.getMultiGenomeDrawer();

			// Create the export settings
			ExportSettings settings = new ExportSettings(genomeDrawer);

			// Create the dialog
			ExportBEDDialog dialog = new ExportBEDDialog(settings);

			// Show the dialog
			if (dialog.showDialog(null) == MultiGenomeTrackActionDialog.APPROVE_OPTION) {

				// Initialize the engine if the export is about only one VCF file
				int fileNumber = settings.getFileNumber();
				if (fileNumber == 1) {
					String filePath = dialog.getBEDPath();
					if (filePath != null) {
						// Notifies the action
						notifyActionStart(ACTION_NAME, 1, false);

						exportEngine = new MGOBedExportSingleFile(dialog.getGenomeName(), dialog.getSettings().getAlleleType(), dialog.getHeader(), dialog.getCoordinateSystem());
						exportEngine.initializeEngine(settings.getFileMap(), settings.getVariationMap(), settings.getFilterList(), MGDisplaySettings.getInstance().includeReferences(), MGDisplaySettings.getInstance().includeNoCall());
						exportEngine.setPath(filePath);

						try {
							exportEngine.compute();
							return true;
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
					} else {
						JOptionPane.showMessageDialog(getRootPane(), "The output file has not been given.\n", "Export error", JOptionPane.INFORMATION_MESSAGE);
					}
				} else if (fileNumber > 1) {
					JOptionPane.showMessageDialog(getRootPane(), "Cannot export data from more than one VCF.\nMore support coming soon.", "Export error", JOptionPane.INFORMATION_MESSAGE);
				} else {
					System.err.println("PAMultiGenomeExport.processAction(): Number of file required is not valid: " + fileNumber);
				}
			} else {
				hasBeenCancelled = true;
			}
		}
		return false;
	}


	@Override
	protected void doAtTheEnd(Boolean actionResult) {
		if (!hasBeenCancelled) {
			String description = getMessageDescription(actionResult);
			JOptionPane.showMessageDialog(getRootPane(), description, "Export report", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	/**
	 * @return a description to sum up the result of the operation
	 */
	private String getMessageDescription (boolean success) {
		String result = "";
		String action = "";
		String files = "";

		action += "Export as BED: ";

		if (success) {
			action += "success.";
			List<File> list = getExportedFiles();
			for (int i = 0; i < list.size(); i++) {
				files += list.get(i).getName();
				if (i < (list.size() - 1)) {
					files += "\n";
				}
			}
		} else {
			action += "error.";
			files += "no BED file";
		}

		result = "Operation:\n" + action + "\nGenerated files:\n" + files;

		return result;
	}


	/**
	 * @return the list of generated files
	 */
	private List<File> getExportedFiles () {
		if (exportEngine != null) {
			return ((MGOBedExportSingleFile) exportEngine).getExportedFiles();
		}
		return null;
	}

}