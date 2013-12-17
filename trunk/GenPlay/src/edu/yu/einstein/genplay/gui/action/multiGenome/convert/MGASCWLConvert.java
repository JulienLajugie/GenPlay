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
package edu.yu.einstein.genplay.gui.action.multiGenome.convert;

import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.operation.ExportEngine;
import edu.yu.einstein.genplay.core.multiGenome.operation.BED.MGOBedConvertSingleFile;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportSettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.convert.ConvertSCWDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.mainDialog.MultiGenomeTrackActionDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.SimpleSCWLayer;
import edu.yu.einstein.genplay.gui.track.layer.variantLayer.VariantLayer;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * @author Nicolas Fourel
 */
public class MGASCWLConvert extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 			DESCRIPTION =
			"Converts the stripes on a variable window track."; 										// tooltip
	private static final int 				MNEMONIC = KeyEvent.VK_M; 									// mnemonic key
	private static		 String 			ACTION_NAME = "Convert into variable window track";			// action name
	private 			 VariantLayer 		selectedLayer;												// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Multi Genome variable track Convert";

	private ExportEngine exportEngine;
	private ConvertSCWDialog dialog;
	private boolean 			success;


	/**
	 * Creates an instance of {@link MGASCWLConvert}.
	 */
	public MGASCWLConvert() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected void doAtTheEnd(Boolean actionResult) {
		success = actionResult;

		if (success) {
			try {
				SCWList list = ((MGOBedConvertSingleFile) exportEngine).getFirstList();
				setTrack(dialog.getFirstAlleleTrack(), list);
			} catch (Exception e) {
				ExceptionManager.getInstance().caughtException(e);
			}

			try {
				SCWList list = ((MGOBedConvertSingleFile) exportEngine).getSecondList();
				setTrack(dialog.getSecondAlleleTrack(), list);
			} catch (Exception e) {
				ExceptionManager.getInstance().caughtException(e);
			}
		}
	}


	@Override
	protected Boolean processAction() throws Exception {

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.isMultiGenomeProject()) {

			// Get layer information
			selectedLayer = (VariantLayer) getValue("Layer");

			// Create the export settings
			ExportSettings settings = new ExportSettings(selectedLayer);

			// Create the dialog
			dialog = new ConvertSCWDialog(settings, selectedLayer);

			// Show the dialog
			if (dialog.showDialog(null) == MultiGenomeTrackActionDialog.APPROVE_OPTION) {

				// Initialize the engine if the export is about only one VCF file
				int fileNumber = settings.getFileNumber();
				if (fileNumber == 1) {
					// Notifies the action
					notifyActionStart(ACTION_NAME, 1, false);

					exportEngine = new MGOBedConvertSingleFile(dialog.getGenomeName(), dialog.getFirstAlleleTrack(), dialog.getSecondAlleleTrack(), dialog.getDotValue(), dialog.getHeader());
					exportEngine.initializeEngine(settings.getFileMap(), settings.getVariationMap(), settings.getFilterList(), MGDisplaySettings.getInstance().includeReferences(), MGDisplaySettings.getInstance().includeNoCall());

					try {
						exportEngine.compute();
						return true;
					} catch (Exception e) {
						ExceptionManager.getInstance().caughtException(e);
					}

					return true;
				} else if (fileNumber > 1) {
					JOptionPane.showMessageDialog(getRootPane(), "Cannot export data from more than one VCF.\nMore support coming soon.", "Export error", JOptionPane.INFORMATION_MESSAGE);
				} else {
					System.err.println("PAMultiGenomeExport.processAction(): Number of file required is not valid: " + fileNumber);
				}
			}
		}
		return false;
	}


	private void setTrack (Track currentTrack, SCWList list) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		if ((list!= null) && (currentTrack != null)) {
			SimpleSCWLayer newLayer = new SimpleSCWLayer(currentTrack, list, selectedLayer.getName());
			newLayer.getHistory().add("Apply mask", Colors.GREY);
			newLayer.getHistory().add("Track: " + currentTrack.getName(), Colors.GREY);
			currentTrack.getLayers().add(newLayer);
		}
	}

}