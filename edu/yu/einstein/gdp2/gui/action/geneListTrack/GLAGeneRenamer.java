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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package yu.einstein.gdp2.gui.action.geneListTrack;

import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;

import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.list.geneList.operation.GLOGeneRenamer;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.track.GeneListTrack;

/**
 * Class to Rename Genes 
 * @author Chirag Gorasia
 * @version 0.1
 */

public class GLAGeneRenamer extends TrackListActionOperationWorker<GeneList>{

	private static final long serialVersionUID = -2210215854202609520L;
	private static final String 	ACTION_NAME = "Rename Genes"; // action name
	private static final String 	DESCRIPTION = "Rename Genes";
	private GeneListTrack selectedTrack;
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "GLAGeneRenamer";
	
	/**
	 * Creates an instance of {@link GLAGeneRenamer}
	 */
	public GLAGeneRenamer() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}

	@Override
	public Operation<GeneList> initializeOperation() throws Exception {
		selectedTrack = (GeneListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			GeneList geneList = selectedTrack.getData();
			String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
			JFileChooser jfc = new JFileChooser(defaultDirectory);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int retVal = jfc.showOpenDialog(getRootPane());
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File fileName = jfc.getSelectedFile();
				Operation<GeneList> operation = new GLOGeneRenamer(geneList, fileName); 
				return operation;
			}
		}
		return null;
	}
	
	
	@Override
	protected void doAtTheEnd(GeneList actionResult) {
		if (actionResult != null) {
			int selectedIndex = getTrackList().getSelectedTrackIndex();
			GeneListTrack glt = new GeneListTrack(getTrackList().getGenomeWindow(), selectedIndex + 1, actionResult);
			getTrackList().setTrack(selectedIndex, glt, selectedTrack.getPreferredHeight(), selectedTrack.getName(), selectedTrack.getStripes());
		}
	}
}
