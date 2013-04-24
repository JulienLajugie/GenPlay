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

import java.text.NumberFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOAverage;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.track.layer.AbstractVersionedLayer;
import edu.yu.einstein.genplay.gui.track.layer.GenericSCWLayer;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Computes the average of the scores of the selected {@link GenericSCWLayer}.
 * @author Julien Lajugie
 */
public class SCWLAAverage extends TrackListActionOperationWorker<Double> {

	private static final long serialVersionUID = 4662911501034876210L;
	private static final String 	ACTION_NAME = "Average";			// action name
	private static final String 	DESCRIPTION =
			"Compute the average of the scores of the selected layer";	// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = SCWLAAverage.class.getName();


	/**
	 * Creates an instance of {@link SCWLAAverage}
	 */
	public SCWLAAverage() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(Double actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), "Average: \n" + NumberFormat.getInstance().format(actionResult), "Average", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	@Override
	public Operation<Double> initializeOperation() {
		AbstractVersionedLayer<?> selectedLayer = (AbstractVersionedLayer<?>) getValue("Layer");
		if (selectedLayer != null) {
			boolean[] selectedChromo = Utils.chooseChromosomes(getRootPane());
			if (selectedChromo != null) {
				SCWList scwList = (SCWList) selectedLayer.getData();
				operation = new SCWLOAverage(scwList, selectedChromo);
				return operation;
			}
		}
		return null;
	}
}
