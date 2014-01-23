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
package edu.yu.einstein.genplay.gui.action.layer.geneLayer;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.geneList.GLOCountExons;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.track.layer.GeneLayer;
import edu.yu.einstein.genplay.util.NumberFormats;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Counts the number of exons of the selected {@link GeneLayer}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class GLACountExons extends TrackListActionOperationWorker<Long> {

	private static final long serialVersionUID = -7994020295916154454L;				// generated ID
	private static final String 	ACTION_NAME = "Count Exons";					// action name
	private static final String 	DESCRIPTION = "Count the number of exons";		// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = GLACountExons.class.getName();


	/**
	 * Creates an instance of {@link GLACountExons}
	 */
	public GLACountExons() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(Long actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), "Number of exons: \n" + NumberFormats.getScoreFormat().format(actionResult), "Count Exons", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	@Override
	public Operation<Long> initializeOperation() {
		GeneLayer selectedLayer = (GeneLayer) getValue("Layer");
		if (selectedLayer != null) {
			boolean[] selectedChromo = Utils.chooseChromosomes(getRootPane());
			if (selectedChromo != null) {
				GeneList binList = selectedLayer.getData();
				Operation<Long> operation = new GLOCountExons(binList, selectedChromo);
				return operation;
			}
		}
		return null;
	}
}
