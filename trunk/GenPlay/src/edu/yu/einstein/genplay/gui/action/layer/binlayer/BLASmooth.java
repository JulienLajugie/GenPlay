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
package edu.yu.einstein.genplay.gui.action.layer.binlayer;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.binList.BLOGauss;
import edu.yu.einstein.genplay.core.operation.binList.BLOLoessRegression;
import edu.yu.einstein.genplay.core.operation.binList.BLOMovingAverage;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.GenomeWidthChooser;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;


/**
 * Smoothes the signal of the selected layer
 * @author Julien Lajugie
 */
public class BLASmooth extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 8280552130742399765L;
	private static final String 	ACTION_NAME = "Smooth";				// action name
	private static final String 	DESCRIPTION =
			"Smooth the signal of the selected layer" + HELP_TOOLTIP_SUFFIX;	// tooltip
	private static final String		HELP_URL = "http://genplay.einstein.yu.edu/wiki/index.php/Documentation#Smooth";
	private BinLayer				selectedLayer;						// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = BLASmooth.class.getName();


	/**
	 * Creates an instance of {@link BLASmooth}
	 */
	public BLASmooth() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(HELP_URL_KEY, HELP_URL);
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			selectedLayer.setData(actionResult, operation.getDescription());
		}
	}


	@Override
	public Operation<BinList> initializeOperation() throws Exception {
		selectedLayer = (BinLayer) getValue("Layer");
		if (selectedLayer != null) {
			String gaussianOption = "Gaussian Smoothing";
			String loessOption = "Loess Smoothing";
			String movingAverageOption = "Moving Average Smoothing";
			String[] smoothingAlgos = {gaussianOption, loessOption, movingAverageOption};
			String selectedAlgorithm = (String) JOptionPane.showInputDialog(getRootPane(), "Please choose a smoothing algorithm", "Smoothing Algorithms", JOptionPane.QUESTION_MESSAGE, null, smoothingAlgos, gaussianOption);
			if (selectedAlgorithm != null) {
				BinList binList = selectedLayer.getData();
				int windowSize = binList.getBinSize();
				if(windowSize > 0) {
					boolean showSigma = selectedAlgorithm == gaussianOption;
					Integer movingWindowWidth = GenomeWidthChooser.getMovingWindowSize(getRootPane(), windowSize, showSigma);
					if(movingWindowWidth != null) {
						int fillNullOption = JOptionPane.showConfirmDialog(getRootPane(), "Do you want to extrapolate the null windows", "Extrapolate null windows", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (fillNullOption != JOptionPane.CANCEL_OPTION) {
							boolean fillNullValues = (fillNullOption == JOptionPane.YES_OPTION);
							Operation<BinList> operation = null;
							if (selectedAlgorithm == gaussianOption) {
								operation = new BLOGauss(binList, movingWindowWidth, fillNullValues);
							} else if (selectedAlgorithm == loessOption) {
								operation = new BLOLoessRegression(binList, movingWindowWidth, fillNullValues);
							} else if (selectedAlgorithm == movingAverageOption) {
								operation = new BLOMovingAverage(binList, movingWindowWidth, fillNullValues);
							}
							return operation;
						}
					}
				}
			}
		}
		return null;
	}
}
