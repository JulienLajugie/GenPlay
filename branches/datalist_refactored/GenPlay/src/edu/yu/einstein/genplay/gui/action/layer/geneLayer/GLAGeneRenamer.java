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
package edu.yu.einstein.genplay.gui.action.layer.geneLayer;

import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;

import edu.yu.einstein.genplay.core.gene.Gene;
import edu.yu.einstein.genplay.core.list.GenomicDataList;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.list.geneList.operation.GLOGeneRenamer;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.track.layer.geneLayer.GeneLayer;


/**
 * Class to Rename Genes
 * @author Chirag Gorasia
 * @version 0.1
 */

public class GLAGeneRenamer extends TrackListActionOperationWorker<GeneList>{

	private static final long serialVersionUID = -2210215854202609520L;
	private static final String 	ACTION_NAME = "Rename Genes"; // action name
	private static final String 	DESCRIPTION = "Rename Genes";
	private GeneLayer 				selectedLayer;


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = GLAGeneRenamer.class.getName();


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
		selectedLayer = (GeneLayer) getValue("Layer");
		if (selectedLayer != null) {
			GenomicDataList<Gene> geneList = selectedLayer.getData();
			String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
			JFileChooser jfc = new JFileChooser(defaultDirectory);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int retVal = jfc.showOpenDialog(getRootPane());
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File fileName = jfc.getSelectedFile();
				operation = new GLOGeneRenamer(geneList, fileName);
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(GeneList actionResult) {
		if (actionResult != null) {
			selectedLayer.setData(actionResult, operation.getDescription());
		}
	}
}
