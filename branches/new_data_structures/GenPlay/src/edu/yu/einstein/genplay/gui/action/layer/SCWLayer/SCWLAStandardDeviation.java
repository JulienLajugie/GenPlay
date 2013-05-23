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
package edu.yu.einstein.genplay.gui.action.layer.SCWLayer;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOStandardDeviation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.track.layer.AbstractSCWLayer;
import edu.yu.einstein.genplay.util.NumberFormats;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Returns the standard deviation on the selected chromosomes of the selected {@link AbstractSCWLayer}.
 * @author Julien Lajugie
 */
public final class SCWLAStandardDeviation extends TrackListActionOperationWorker<Double> {

	private static final long serialVersionUID = -3906549904760962910L;	// generated ID
	private static final String 	ACTION_NAME = "Standard Deviation";	// action name
	private static final String 	DESCRIPTION =
			"Return the standard deviation on the " +
					"selected chromosomes of the selected layer";		// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = SCWLAStandardDeviation.class.getName();


	/**
	 * Creates an instance of {@link SCWLAStandardDeviation}
	 */
	public SCWLAStandardDeviation() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(Double actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), "Standard deviation: \n" + NumberFormats.getScoreFormat().format(actionResult), "Standard Deviation", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	@Override
	public Operation<Double> initializeOperation() {
		AbstractSCWLayer<?> selectedLayer = (AbstractSCWLayer<?>) getValue("Layer");
		if (selectedLayer != null) {
			boolean[] selectedChromo = Utils.chooseChromosomes(getRootPane());
			if (selectedChromo != null) {
				SCWList scwList = selectedLayer.getData();
				Operation<Double> operation = new SCWLOStandardDeviation(scwList, selectedChromo);
				return operation;
			}
		}
		return null;
	}
}
