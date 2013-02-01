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

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.BLORepartition;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.scatterPlot.ScatterPlotData;
import edu.yu.einstein.genplay.gui.scatterPlot.ScatterPlotPane;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.gui.track.layer.ColoredLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Generates an array containing the repartition of the score values of the selected {@link BinLayer}
 * @author Chirag Gorasia
 * @version 0.1
 */
public final class BLARepartition extends TrackListActionOperationWorker<double [][][]> {

	private static final long serialVersionUID = -7166030548181210580L; // generated ID
	private static final String 	ACTION_NAME = "Show Repartition";	// action name
	private static final String 	DESCRIPTION =
			"Generate a plot showing the repartition of the scores of the selected layers";	// tooltip
	private Layer<?>[] 				selectedLayers;
	private List<ScatterPlotData> 	scatPlotData;

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLARepartition";


	/**
	 * Creates an instance of {@link BLARepartition}
	 */
	public BLARepartition() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<double [][][]> initializeOperation() {
		BinLayer selectedLayer = (BinLayer) getValue("Layer");
		if (selectedLayer != null) {
			Number scoreBin = NumberOptionPane.getValue(getRootPane(), "Size", "Enter the size of the bin of score:", new DecimalFormat("0.0#####"), 0 + Double.MIN_NORMAL, 1000, 1);
			if (scoreBin != null) {
				// we ask the user to choose the layers for the repartition only if there is more than one layer
				LayerType[] availableLayerTypes = {LayerType.BIN_LAYER};
				Layer<?>[] binLayers = Utils.getLayers(getTrackListPanel().getModel().getTracks(), availableLayerTypes);
				if (binLayers.length > 1) {
					LayerChooserDialog layerChooserDialog = new LayerChooserDialog();
					layerChooserDialog.setLayers(getTrackListPanel().getModel().getAllLayers());
					layerChooserDialog.setSelectableLayers(availableLayerTypes);
					layerChooserDialog.setMultiselectable(true);
					if (layerChooserDialog.showDialog(getRootPane(), "Select Layers") == LayerChooserDialog.APPROVE_OPTION) {
						selectedLayers = layerChooserDialog.getSelectedLayers().toArray(new Layer<?>[0]);
					}
				} else {
					selectedLayers = binLayers;
				}
				if ((selectedLayers != null)) {
					BinList[] binListArray = new BinList[selectedLayers.length];
					for (int i = 0; i < selectedLayers.length; i++) {
						binListArray[i] = ((BinLayer)selectedLayers[i]).getData();
					}
					if (binListArray.length > 0) {
						Operation<double[][][]> operation = new BLORepartition(binListArray, scoreBin.doubleValue());
						return operation;
					}
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(double[][][] actionResult) {
		if ((actionResult != null) && (selectedLayers.length != 0)) {
			scatPlotData = new ArrayList<ScatterPlotData>();
			for (int k = 0; k < actionResult.length; k++) {
				Color layerColor = ((ColoredLayer) selectedLayers[k]).getColor(); // retrieve the color of the layer
				scatPlotData.add(new ScatterPlotData(actionResult[k], selectedLayers[k].toString(), layerColor));
			}
			ScatterPlotPane.showDialog(getRootPane(), "Score", "Bin Count", scatPlotData);
		}
	}
}
