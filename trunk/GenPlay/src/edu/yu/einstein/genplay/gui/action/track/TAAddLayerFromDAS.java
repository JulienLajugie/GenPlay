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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.core.DAS.DASConnector;
import edu.yu.einstein.genplay.core.DAS.DASType;
import edu.yu.einstein.genplay.core.DAS.DataSource;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.dialog.DASDialog.DASDialog;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * Loads a layer from data retrieve from a DAS server
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public class TAAddLayerFromDAS extends TrackListAction {

	private static final long serialVersionUID = -4045220235804063954L;		// generated ID
	private static final String ACTION_NAME = "Add Layer From DAS Server"; 	// action name
	private static final String DESCRIPTION = "Add a layer from " +
			"data retrieved from a DAS server"; 							// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_D; 					// mnemonic key


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TAAddLayerFromDAS.class.getName();


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK);


	/**
	 * Creates an instance of {@link TAAddLayerFromDAS}
	 */
	public TAAddLayerFromDAS() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			final Track selectedTrack = getTrackListPanel().getSelectedTrack();
			if (selectedTrack != null) {
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
						new TAAddGeneLayerFromDAS(dataSource, dasConnector, dasType, dataRange, genomeWindow, currentWindow, selectedTrack).actionPerformed(arg0);
					} else if (resType == DASDialog.GENERATE_SCW_LIST) {
						// case where the result type is a SCWList
						new TAAddSCWLayerFromDAS(dataSource, dasConnector, dasType, dataRange, genomeWindow, currentWindow, selectedTrack).actionPerformed(arg0);
					}
				}
			}
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(Thread.currentThread(), e, "Error While Loading the Server List");
		}
	}
}
