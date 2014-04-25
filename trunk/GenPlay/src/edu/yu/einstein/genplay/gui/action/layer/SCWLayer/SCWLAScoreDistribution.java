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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOScoreDistribution;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotData;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotPane;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.track.layer.AbstractSCWLayer;
import edu.yu.einstein.genplay.gui.track.layer.ColoredLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Generates an array containing the distribution of the score values of the selected {@link AbstractSCWLayer}
 * @author Chirag Gorasia
 */
public final class SCWLAScoreDistribution extends TrackListActionOperationWorker<double [][][]>{

	private static final long serialVersionUID = -6665806475919318742L;
	private static final String 	ACTION_NAME = "Score Distribution Histogram";			// action name
	private static final String 	DESCRIPTION =
			"Generate a plot showing the distribution of the scores of the selected layer";	// tooltip
	private AbstractSCWLayer<?>[]	selectedLayers;
	private List<ScatterPlotData> 	scatPlotData;
	private int 					graphIndicator;

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = SCWLAScoreDistribution.class.getName();


	/**
	 * Creates an instance of {@link SCWLAScoreDistribution}
	 */
	public SCWLAScoreDistribution() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(double[][][] actionResult) {
		if ((actionResult != null) && (selectedLayers.length != 0)) {
			scatPlotData = new ArrayList<ScatterPlotData>();
			for (int k = 0; k < actionResult.length; k++) {
				Color layerColor = ((ColoredLayer) selectedLayers[k]).getColor(); // retrieve the color of the layer
				scatPlotData.add(new ScatterPlotData(actionResult[k], selectedLayers[k].toString(), layerColor));
			}
			if (getGraphIndicator() == SCWLOScoreDistribution.WINDOW_COUNT_GRAPH) {
				ScatterPlotPane.showDialog(getRootPane(), "Score", "Window Count", scatPlotData);
			} else {
				ScatterPlotPane.showDialog(getRootPane(), "Score", "bp Count", scatPlotData);
			}
		}
	}


	/**
	 * @return the graphIndicator
	 */
	public int getGraphIndicator() {
		return graphIndicator;
	}


	@Override
	public Operation<double [][][]> initializeOperation() {
		AbstractSCWLayer<?> selectedLayer = (AbstractSCWLayer<?>) getValue("Layer");
		if (selectedLayer != null) {
			Object[] graphTypes = {"Score vs Window Count", "Score vs Base Pair Count"};
			String selectedValue = (String) JOptionPane.showInputDialog(getRootPane(), "Select the operation", "Graph Operation", JOptionPane.PLAIN_MESSAGE, null, graphTypes, graphTypes[0]);
			if (selectedValue != null) {
				if (selectedValue.toString().equals(graphTypes[0])) {
					// graph of score vs window count
					setGraphIndicator(SCWLOScoreDistribution.WINDOW_COUNT_GRAPH);
				} else {
					setGraphIndicator(SCWLOScoreDistribution.BASE_COUNT_GRAPH);
				}
				Number scoreBin = NumberOptionPane.getValue(getRootPane(), "Size", "Enter the size of the bin of score:", 0 + Double.MIN_NORMAL, 1000, 1);
				if (scoreBin != null) {
					// we ask the user to choose the layers for the distribution only if there is more than one layer
					LayerType[] availableLayerTypes = {LayerType.SIMPLE_SCW_LAYER, LayerType.BIN_LAYER};
					Layer<?>[] scwLayers = Utils.getLayers(getTrackListPanel().getModel().getTracks(), availableLayerTypes);
					if (scwLayers.length > 1) {
						LayerChooserDialog layerChooserDialog = new LayerChooserDialog();
						layerChooserDialog.setLayers(getTrackListPanel().getModel().getAllLayers());
						layerChooserDialog.setSelectableLayerTypes(availableLayerTypes);
						layerChooserDialog.setMultiselectable(true);
						if (layerChooserDialog.showDialog(getRootPane(), "Select Layers") == LayerChooserDialog.APPROVE_OPTION) {
							selectedLayers = layerChooserDialog.getSelectedLayers().toArray(new AbstractSCWLayer[0]);
						}
					} else {
						selectedLayers = new AbstractSCWLayer[1];
						selectedLayers[0] =	(AbstractSCWLayer<?>) scwLayers[0];
					}
					if (selectedLayers != null) {
						SCWList[] scwListArray = new SCWList[selectedLayers.length];
						for (int i = 0; i < selectedLayers.length; i++) {
							scwListArray[i] = selectedLayers[i].getData();
						}
						if (scwListArray.length > 0) {
							Operation<double[][][]> operation = new SCWLOScoreDistribution(scwListArray, scoreBin.doubleValue(), getGraphIndicator());
							return operation;
						}
					}
				}
			}
		}
		return null;
	}


	/**
	 * @param graphIndicator the graphIndicator to set
	 */
	public void setGraphIndicator(int graphIndicator) {
		this.graphIndicator = graphIndicator;
	}
}
