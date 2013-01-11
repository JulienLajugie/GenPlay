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
package edu.yu.einstein.genplay.gui.action.multiGenome.VCFAction;

import java.awt.event.KeyEvent;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFStatistics.VCFFileStatistics;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGAVCFStatistics extends TrackListActionWorker<VCFFileStatistics> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION =
			"Generates statistics for a track."; 									// tooltip
	private static final int 				MNEMONIC = KeyEvent.VK_M; 					// mnemonic key
	private static		 String 			ACTION_NAME = "Generate track statistics";	// action name


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Track Statistics";

	//private MGOVCFStatisticsSingleFile operation;


	/**
	 * Creates an instance of {@link MGAVCFStatistics}.
	 */
	public MGAVCFStatistics() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected VCFFileStatistics processAction() {
		// TODO Layer modif
		VCFFileStatistics result = null;
		/*		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.isMultiGenomeProject()) {

			// Get track information
			Track track = MainFrame.getInstance().getTrackListPanel().getSelectedTrack();
			MultiGenomeDrawer genomeDrawer = track.getMultiGenomeDrawer();

			if (genomeDrawer.getStatistics() == null) {

				// Create the export settings
				ExportSettings settings = new ExportSettings(genomeDrawer);

				// Create the operation
				operation = new MGOVCFStatisticsSingleFile();
				operation.initializeEngine(settings.getFileMap(), settings.getVariationMap(), settings.getFilterList(), MGDisplaySettings.getInstance().includeReferences(), MGDisplaySettings.getInstance().includeNoCall());

				if (operation.isSingleExport()) {
					// Notifies the action
					notifyActionStart(ACTION_NAME, 1, false);

					// Run the operation
					try {
						operation.compute();
					} catch (Exception e) {
						e.printStackTrace();
					}

					// Return the result
					result = new VCFFileMixStatistic(operation.getNativeStatistics(), operation.getNewStatistics());
				} else {
					JOptionPane.showMessageDialog(getRootPane(), "Statistics can be generated with data from only one file.", "Statistics report error", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				result = genomeDrawer.getStatistics();
			}
		}*/
		return result;
	}


	@Override
	protected void doAtTheEnd(VCFFileStatistics actionResult) {
		// TODO Layer modif
		/*if (actionResult != null) {
			actionResult.processStatistics();

			MainFrame.getInstance().getTrackListPanel().getSelectedTrack().getMultiGenomeDrawer().setStatistics(actionResult);

			MGStatisticsDialog dialog = new MGStatisticsDialog(actionResult);
			dialog.show(getRootPane());
		} else {
			System.out.println("No statistic generated");
		}*/

	}

}