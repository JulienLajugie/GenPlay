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
package edu.yu.einstein.genplay.gui.action.layer.binlayer;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.binList.BLOMin;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Shows the minimum score of the selected {@link BinLayer}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLAMin extends TrackListActionOperationWorker<Double> {

	private static final long serialVersionUID = 3523404731226850786L;	// generated ID
	private static final String 	ACTION_NAME = "Minimum";			// action name
	private static final String 	DESCRIPTION =
			"Show the minimum score of the selected layer";					// tooltip
	private BinLayer	 			selectedLayer;						// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = BLAMin.class.getName();


	/**
	 * Creates an instance of {@link BLAMin}
	 */
	public BLAMin() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Double> initializeOperation() {
		selectedLayer = (BinLayer) getValue("Layer");
		if (selectedLayer != null) {
			boolean[] selectedChromo = Utils.chooseChromosomes(getRootPane());
			if (selectedChromo != null) {
				BinList binList = selectedLayer.getData();
				Operation<Double> operation = new BLOMin(binList, selectedChromo);
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Double actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), actionResult, "Minimum of \"" + selectedLayer.getName() +"\":", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
