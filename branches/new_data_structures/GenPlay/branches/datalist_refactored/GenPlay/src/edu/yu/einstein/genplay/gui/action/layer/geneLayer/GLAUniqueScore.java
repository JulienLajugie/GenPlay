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

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.gene.Gene;
import edu.yu.einstein.genplay.core.list.GenomicDataList;
import edu.yu.einstein.genplay.core.list.geneList.operation.GLOUniqueScore;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.track.layer.geneLayer.GeneLayer;


/**
 * Sets a unique score for all exons
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLAUniqueScore  extends TrackListActionOperationWorker<GenomicDataList<Gene>> {

	private static final long serialVersionUID = 2102571378866219218L; 		// generated ID
	private static final String 	ACTION_NAME = "Unique Score (Constant)";// action name
	private static final String 	DESCRIPTION =
			"Set a unique score for all exons.";							// tooltip
	private GeneLayer	 			selectedLayer;							// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = GLAUniqueScore.class.getName();


	/**
	 * Creates an instance of {@link GLAUniqueScore}
	 */
	public GLAUniqueScore() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<GenomicDataList<Gene>> initializeOperation() throws Exception {
		selectedLayer = (GeneLayer) getValue("Layer");
		if (selectedLayer != null) {
			Number constant = NumberOptionPane.getValue(getRootPane(), "Unique Score", "Enter a score for all exons", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if ((constant != null) && (constant.doubleValue() != 0)) {
				GenomicDataList<Gene> geneList = selectedLayer.getData();
				operation = new GLOUniqueScore(geneList, constant.doubleValue());
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(GenomicDataList<Gene> actionResult) {
		if (actionResult != null) {
			selectedLayer.setData(actionResult, operation.getDescription());
		}
	}
}