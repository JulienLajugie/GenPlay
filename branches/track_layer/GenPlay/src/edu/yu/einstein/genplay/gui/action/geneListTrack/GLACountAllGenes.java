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
package edu.yu.einstein.genplay.gui.action.geneListTrack;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.list.geneList.operation.GLOCountAllGenes;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.ChromosomeChooser;
import edu.yu.einstein.genplay.gui.old.track.GeneListTrack;



/**
 * Counts the number of genes of the selected {@link GeneListTrack}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class GLACountAllGenes extends TrackListActionOperationWorker<Long> {

	private static final long serialVersionUID = -7198642565173540167L;	// generated ID
	private static final String 	ACTION_NAME = "Count all genes";				// action name
	private static final String 	DESCRIPTION = "Count the number of genes";		// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "GLACountAllGenes";


	/**
	 * Creates an instance of {@link GLACountAllGenes}
	 */
	public GLACountAllGenes() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Long> initializeOperation() {
		GeneListTrack selectedTrack = (GeneListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane());
			if (selectedChromo != null) {
				GeneList binList = selectedTrack.getData();
				Operation<Long> operation = new GLOCountAllGenes(binList, selectedChromo);
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Long actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), "Number of genes: \n" + actionResult, "Count all genes", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
