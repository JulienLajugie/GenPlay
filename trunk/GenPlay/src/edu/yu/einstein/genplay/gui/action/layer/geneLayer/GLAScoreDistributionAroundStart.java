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
package edu.yu.einstein.genplay.gui.action.layer.geneLayer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.geneList.GLOScoreDistributionAroundStart;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotData;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotPane;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.gui.track.layer.GeneLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Generates a chart showing the score distribution around the start
 * @author Julien Lajugie
 */
public class GLAScoreDistributionAroundStart extends TrackListActionOperationWorker<double[][]> {

	private static final long serialVersionUID = -3916743291195449577L; 				// generated id
	private static final String 	ACTION_NAME = "Score Distribution Around Start";	// action name
	private static final String 	DESCRIPTION = "Generates a chart showing the " +
			"score distribution around the start"; 										// tooltip
	private GeneLayer				selectedLayer;										// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = GLAScoreDistributionAroundStart.class.getName();


	/**
	 * Creates an instance of {@link GLAScoreDistributionAroundStart}
	 */
	public GLAScoreDistributionAroundStart() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(double[][] actionResult) {
		if (actionResult != null) {
			List<ScatterPlotData> scatPlotData = new ArrayList<ScatterPlotData>();
			scatPlotData.add(new ScatterPlotData(actionResult, "Score Distribution around promoters of " + selectedLayer.toString(), Colors.RED));
			ScatterPlotPane.showDialog(getRootPane(), "Distance", "Score", scatPlotData);
		}
	}


	@Override
	public Operation<double[][]> initializeOperation() {
		selectedLayer = (GeneLayer) getValue("Layer");
		if (selectedLayer != null) {
			GeneList geneList = selectedLayer.getData();
			LayerType[] availableLayerTypes = {LayerType.BIN_LAYER};
			Layer<?>[] scwLayers = Utils.getLayers(getTrackListPanel().getModel().getTracks(), availableLayerTypes);
			if ((scwLayers == null) || (scwLayers.length == 0)) {
				JOptionPane.showMessageDialog(getRootPane(), "You need to load at least one Fixed Window layer before using this operation", "Warning", JOptionPane.WARNING_MESSAGE);
			} else {
				LayerChooserDialog layerChooserDialog = new LayerChooserDialog();
				layerChooserDialog.setLayers(getTrackListPanel().getModel().getAllLayers());
				layerChooserDialog.setSelectableLayerTypes(availableLayerTypes);
				layerChooserDialog.setMultiselectable(false);
				if (layerChooserDialog.showDialog(getRootPane(), "Select Layer") == LayerChooserDialog.APPROVE_OPTION) {
					BinLayer binLayer = (BinLayer) layerChooserDialog.getSelectedLayer();
					if (binLayer != null) {
						BinList binList = binLayer.getData();
						boolean[] selectedChromo = Utils.chooseChromosomes(getRootPane());
						if (selectedChromo != null) {
							Number binSize = NumberOptionPane.getValue(getRootPane(), "Enter Value", "Enter the size of the bins in bp", 1, Integer.MAX_VALUE, 10);
							if (binSize != null) {
								Number binCount = NumberOptionPane.getValue(getRootPane(), "Enter Value", "Enter the number of bins each side of the promoters", 1, Integer.MAX_VALUE, 50);
								if (binCount != null) {
									ScoreOperation scm = Utils.chooseScoreCalculation(getRootPane());
									if (scm != null) {
										operation = new GLOScoreDistributionAroundStart(geneList, binList, selectedChromo, binSize.intValue(), binCount.intValue(), scm);
										return operation;
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
}
