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
package edu.yu.einstein.genplay.gui.action.emptyTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.DAS.DASConnector;
import edu.yu.einstein.genplay.core.DAS.DASType;
import edu.yu.einstein.genplay.core.DAS.DataSource;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.dialog.DASDialog.DASDialog;



/**
 * Loads a track from data retrieve from a DAS server
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public class ETALoadFromDAS extends TrackListAction {

	private static final long serialVersionUID = -4045220235804063954L;	// generated ID
	private static final String ACTION_NAME = "Load from DAS Server"; 	// action name
	private static final String DESCRIPTION = "Load a track from " +
			"data retrieved from a DAS server"; 						// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ETALoadFromDAS";


	/**
	 * Creates an instance of {@link ETALoadFromDAS}
	 */
	public ETALoadFromDAS() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			final int selectedTrackIndex = getTrackList().getSelectedTrackIndex();
			if (selectedTrackIndex != -1) {
				DASDialog dasDialog = new DASDialog();
				int res = dasDialog.showDASDialog(getRootPane());
				if (res == DASDialog.APPROVE_OPTION) {
					DataSource dataSource = dasDialog.getSelectedDataSource();
					DASConnector dasConnector = dasDialog.getSelectedDasConnector();
					// if we working on a multi genome project we need to
					// specify on which genome the data were mapped
					if (ProjectManager.getInstance().isMultiGenomeProject()) {
						String selectedGenome = dasDialog.getSelectedGenome();
						AlleleType alleleType = dasDialog.getAlleleType();
						dasConnector.setGenomeName(selectedGenome);
						dasConnector.setAlleleType(alleleType);
					}
					DASType dasType = dasDialog.getSelectedDasType();
					int resType = dasDialog.getGenerateType();
					int dataRange = dasDialog.getDataRange();
					GenomeWindow genomeWindow = dasDialog.getUserSpecifiedGenomeWindow();
					GenomeWindow currentWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();					
					if (resType == DASDialog.GENERATE_GENE_LIST) {
						// case where the result type is a GeneList
						new ETALoadGeneListTrackFromDAS(dataSource, dasConnector, dasType, dataRange, genomeWindow, currentWindow, selectedTrackIndex).actionPerformed(arg0);
					} else if (resType == DASDialog.GENERATE_SCW_LIST) {
						// case where the result type is a SCWList 
						new ETALoadSCWListTrackFromDAS(dataSource, dasConnector, dasType, dataRange, genomeWindow, currentWindow, selectedTrackIndex).actionPerformed(arg0);
					}
				}
			}
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error While Loading the Server List");
		}
	}
}
