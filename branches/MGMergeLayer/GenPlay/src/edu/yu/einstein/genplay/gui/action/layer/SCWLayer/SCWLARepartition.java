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

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.SimpleScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLORepartition;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.scatterPlot.ScatterPlotData;
import edu.yu.einstein.genplay.gui.scatterPlot.ScatterPlotPane;
import edu.yu.einstein.genplay.gui.track.layer.ColoredLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.gui.track.layer.SCWLayer;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Generates an array containing the repartition of the score values of the selected {@link SCWLayer}
 * @author Chirag Gorasia
 * @version 0.1
 */
public final class SCWLARepartition extends TrackListActionOperationWorker<double [][][]>{

	private static final long serialVersionUID = -6665806475919318742L;
	private static final String 	ACTION_NAME = "Show Repartition";	// action name
	private static final String 	DESCRIPTION =
			"Generate a plot showing the repartition of the scores of the selected layer";	// tooltip
	private SCWLayer[] 				selectedLayers;
	private List<ScatterPlotData> 	scatPlotData;
	private int 					graphIndicator;

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLARepartition";


	/**
	 * Creates an instance of {@link SCWLARepartition}
	 */
	public SCWLARepartition() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * @param graphIndicator the graphIndicator to set
	 */
	public void setGraphIndicator(int graphIndicator) {
		this.graphIndicator = graphIndicator;
	}


	/**
	 * @return the graphIndicator
	 */
	public int getGraphIndicator() {
		return graphIndicator;
	}


	@Override
	public Operation<double [][][]> initializeOperation() {
		SCWLayer selectedLayer = (SCWLayer) getValue("Layer");
		if (selectedLayer != null) {
			Object[] graphTypes = {"Score vs Window Count", "Score vs Base Pair Count"};
			String selectedValue = (String) JOptionPane.showInputDialog(null, "Select the operation", "Graph Operation", JOptionPane.PLAIN_MESSAGE, null, graphTypes, graphTypes[0]);
			if (selectedValue != null) {
				if (selectedValue.toString().equals(graphTypes[0])) {
					// graph of score vs window count
					setGraphIndicator(SCWLORepartition.WINDOW_COUNT_GRAPH);
				} else {
					setGraphIndicator(SCWLORepartition.BASE_COUNT_GRAPH);
				}
				Number scoreBin = NumberOptionPane.getValue(getRootPane(), "Size", "Enter the size of the bin of score:", new DecimalFormat("0.0#####"), 0 + Double.MIN_NORMAL, 1000, 1);
				if (scoreBin != null) {
					// we ask the user to choose the layers for the repartition only if there is more than one layer
					LayerType[] availableLayerTypes = {LayerType.SCW_LAYER};
					Layer<?>[] scwLayers = Utils.getLayers(getTrackListPanel().getModel().getTracks(), availableLayerTypes);
					if (scwLayers.length > 1) {
						LayerChooserDialog layerChooserDialog = new LayerChooserDialog();
						layerChooserDialog.setLayers(getTrackListPanel().getAllLayers());
						layerChooserDialog.setSelectableLayers(availableLayerTypes);
						layerChooserDialog.setMultiselectable(true);
						if (layerChooserDialog.showDialog(getRootPane(), "Select Layers to Paste") == LayerChooserDialog.APPROVE_OPTION) {
							selectedLayers = layerChooserDialog.getSelectedLayers().toArray(new SCWLayer[0]);
						}
					} else {
						selectedLayers = (SCWLayer[]) scwLayers;
					}
					if ((selectedLayers != null)) {
						ScoredChromosomeWindowList[] scwListArray = new SimpleScoredChromosomeWindowList[selectedLayers.length];
						for (int i = 0; i < selectedLayers.length; i++) {
							scwListArray[i] = selectedLayers[i].getData();
						}
						if (scwListArray.length > 0) {
							Operation<double[][][]> operation = new SCWLORepartition(scwListArray, scoreBin.doubleValue(), getGraphIndicator());
							return operation;
						}
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
			if (getGraphIndicator() == SCWLORepartition.WINDOW_COUNT_GRAPH) {
				ScatterPlotPane.showDialog(getRootPane(), "Score", "Window Count", scatPlotData);
			} else {
				ScatterPlotPane.showDialog(getRootPane(), "Score", "bp Count", scatPlotData);
			}
		}
	}
}
