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
package edu.yu.einstein.genplay.gui.action.layer.SCWLayer;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLONormalize;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.track.layer.AbstractSCWLayer;


/**
 * Computes a Standard Score normalization on a {@link AbstractSCWLayer}
 * @author Julien Lajugie
 */
public class SCWLANormalize extends TrackListActionOperationWorker<SCWList> {

	private static final long serialVersionUID = 3820923997838773226L;	// generated ID
	private static final String 		ACTION_NAME = "Normalize";		// action name
	private static final String 		DESCRIPTION =
			"Normalize the scores of the selected layer";				// tooltip
	private AbstractSCWLayer<SCWList> 	selectedLayer;					// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = SCWLANormalize.class.getName();


	/**
	 * Creates an instance of {@link SCWLANormalize}
	 */
	public SCWLANormalize() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * @param scwList
	 * @return a factor that will be used as the default factor value for the normalization
	 */
	private double computeDefaultFactor(SCWList scwList) {
		double scoreSum = scwList.getStatistics().getScoreSum();
		double roundedSum = 1;
		while (roundedSum < scoreSum) {
			roundedSum *= 10;
		}
		return roundedSum;
	}


	@Override
	protected void doAtTheEnd(SCWList actionResult) {
		if (actionResult != null) {
			selectedLayer.setData(actionResult, operation.getDescription());
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public Operation<SCWList> initializeOperation() {
		selectedLayer = (AbstractSCWLayer<SCWList>) getValue("Layer");
		if (selectedLayer != null) {
			SCWList inputList = selectedLayer.getData();
			double defaultFactor = computeDefaultFactor(inputList);
			Number factor = NumberOptionPane.getValue(getRootPane(), "Multiplicative constant", "Enter a factor of X:", 0, Double.MAX_VALUE, defaultFactor);
			if(factor != null) {
				Operation<SCWList> operation = new SCWLONormalize(inputList, factor.doubleValue());
				return operation;
			}
		}
		return null;
	}
}
