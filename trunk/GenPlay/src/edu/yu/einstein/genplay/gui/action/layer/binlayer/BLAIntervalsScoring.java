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

import java.awt.Color;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.binList.BLOIntervalsScoring;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.dialog.trackChooser.TrackChooser;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Computes the average, sum or max of the selected layer on intervals defined by another layer
 * @author Julien Lajugie
 */
public class BLAIntervalsScoring extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = -3736735803307616477L;			// generated ID
	private static final String 	ACTION_NAME = "Intervals Scoring";			// action name
	private static final String 	DESCRIPTION =
			"Compute the average, the sum or the max of the " +
					"selected layer on intervals defined by another layer" + HELP_TOOLTIP_SUFFIX; // tooltip
	private static final String		HELP_URL = "http://genplay.einstein.yu.edu/wiki/index.php/Documentation#Intervals_Scoring";
	private BinLayer	 			selectedLayer;		// selected layer
	private BinLayer				intervalLayer;		// layer defining the intervals
	private Number 					percentage;			// percentage of the greatest values
	private ScoreOperation 	method;				// method of calculation
	private Track 					resultTrack;		// result track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = BLAIntervalsScoring.class.getName();


	/**
	 * Creates an instance of {@link BLAIntervalsScoring}
	 */
	public BLAIntervalsScoring() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(HELP_URL_KEY, HELP_URL);
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			BinLayer newLayer = new BinLayer(resultTrack, actionResult, method + " of " + selectedLayer.getName() + " from intervals of  " + intervalLayer.getName());
			// add info to the history
			newLayer.getHistory().add("Result of the " + method + " of " + selectedLayer.getName() + " calculated on the intervals defined by " + intervalLayer.getName() + " on the " + percentage + "% greatest values", Color.GRAY);
			newLayer.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp", Colors.GREY);
			resultTrack.getLayers().add(newLayer);
		}
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedLayer = (BinLayer) getValue("Layer");
		if (selectedLayer != null) {
			LayerChooserDialog layerChooserDialog = new LayerChooserDialog();
			layerChooserDialog.setLayers(getTrackListPanel().getModel().getAllLayers());
			LayerType[] selectableLayers = {LayerType.BIN_LAYER};
			layerChooserDialog.setSelectableLayerTypes(selectableLayers);
			layerChooserDialog.setMultiselectable(false);
			if (layerChooserDialog.showDialog(getRootPane(), "Select Interval Layer") == LayerChooserDialog.APPROVE_OPTION) {
				intervalLayer = (BinLayer) layerChooserDialog.getSelectedLayer();
				if(intervalLayer != null) {
					percentage = NumberOptionPane.getValue(getRootPane(), "Enter a percentage", "Perform the calculation on the x% greatest values of each interval:", 0, 100, 100);
					if (percentage != null) {
						method = Utils.chooseScoreCalculation(getRootPane());
						if (method != null) {
							resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackListPanel().getModel().getTracks());
							if (resultTrack != null) {
								BinList valueBinList = selectedLayer.getData();
								BinList scoringBinList = intervalLayer.getData();
								Operation<BinList> operation = new BLOIntervalsScoring(scoringBinList, valueBinList, percentage.intValue(), method);
								return operation;
							}
						}
					}
				}
			}
		}
		return null;
	}
}
