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

import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.operation.ExportEngine;
import edu.yu.einstein.genplay.core.multiGenome.operation.BED.MGOBedConvertSingleFile;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportSettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.convert.ConvertSCWDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.mainDialog.MultiGenomeTrackActionDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.variantLayer.MultiGenomeDrawer;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGASCWLConvert extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION =
			"Converts the stripes on a variable window layer."; 										// tooltip
	private static final int 				MNEMONIC = KeyEvent.VK_M; 									// mnemonic key
	private static		 String 			ACTION_NAME = "Convert in variable window layer";			// action name


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Multi Genome variable layer Convert";

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
	protected Boolean processAction() throws Exception {

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.isMultiGenomeProject()) {

			// TODO Layer modif
			// Get track information
			//Track track = MainFrame.getInstance().getTrackListPanel().getSelectedTrack();

			MultiGenomeDrawer genomeDrawer = null; //track.getMultiGenomeDrawer();

			// Create the export settings
			ExportSettings settings = new ExportSettings(genomeDrawer);

			// Create the dialog
			dialog = new ConvertSCWDialog(settings);

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
						e.printStackTrace();
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


	@Override
	protected void doAtTheEnd(Boolean actionResult) {
		success = actionResult;

		if (success) {
			try {
				ScoredChromosomeWindowList list = ((MGOBedConvertSingleFile) exportEngine).getFirstList();
				setTrack(dialog.getFirstAlleleTrack(), list);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				ScoredChromosomeWindowList list = ((MGOBedConvertSingleFile) exportEngine).getSecondList();
				setTrack(dialog.getSecondAlleleTrack(), list);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	private void setTrack (Track currentTrack, ScoredChromosomeWindowList list) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		// TODO Layer modif
		/*if ((list!= null) && (currentTrack != null)) {
			int index = currentTrack.getTrackNumber() - 1;
			CurveTrack newTrack = new SCWListTrack(index + 1, list);
			newTrack.setTrackColor(LayerColor.getLayerColor());
			newTrack.getHistory().add("Apply mask", Colors.GREY);
			newTrack.getHistory().add("Track: " + currentTrack.getName(), Colors.GREY);
			getTrackList().setTrack(index, newTrack, ProjectManager.getInstance().getProjectConfiguration().getTrackHeight(), currentTrack.getName() + " converted to BED", currentTrack.getMask(), currentTrack.getStripesList(), currentTrack.getFiltersList());
		}*/
	}

}