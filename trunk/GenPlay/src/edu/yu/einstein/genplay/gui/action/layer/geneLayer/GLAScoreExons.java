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

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.geneList.GLOScoreFromSCWList;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.track.layer.GeneLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Gives a score to the exons of the genes from a scored layer
 * @author Julien Lajugie
 */
public class GLAScoreExons  extends TrackListActionOperationWorker<GeneList> {

	private static final long serialVersionUID = 2102571378866219218L; 		// generated ID
	private static final String 	ACTION_NAME = "Score Exons";			// action name
	private static final String 	DESCRIPTION =
			"Give a score to the exons of the genes from a scored layer" + HELP_TOOLTIP_SUFFIX;	// tooltip
	private static final String		HELP_URL = "http://genplay.einstein.yu.edu/wiki/index.php/Documentation#Score_Exons";
	private GeneLayer 			selectedLayer;								// selected layer
	private Layer<?> 			otherLayer;									// other layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = GLAScoreExons.class.getName();


	/**
	 * Creates an instance of {@link GLAScoreExons}
	 */
	public GLAScoreExons() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(HELP_URL_KEY, HELP_URL);
	}


	@Override
	protected void doAtTheEnd(GeneList actionResult) {
		if (actionResult != null) {
			selectedLayer.setData(actionResult, operation.getDescription() + otherLayer.getName());
		}
	}


	@Override
	public Operation<GeneList> initializeOperation() throws Exception {
		selectedLayer = (GeneLayer) getValue("Layer");
		if (selectedLayer != null) {
			LayerType[] availableLayerTypes = {LayerType.BIN_LAYER, LayerType.SIMPLE_SCW_LAYER};
			Layer<?>[] scwLayers = Utils.getLayers(getTrackListPanel().getModel().getTracks(), availableLayerTypes);
			if ((scwLayers == null) || (scwLayers.length == 0)) {
				JOptionPane.showMessageDialog(getRootPane(), "You need to load at least one Fixed or Variable Window layer before using this operation", "Warning", JOptionPane.WARNING_MESSAGE);
			} else {
				LayerChooserDialog layerChooserDialog = new LayerChooserDialog();
				layerChooserDialog.setLayers(getTrackListPanel().getModel().getAllLayers());
				layerChooserDialog.setSelectableLayerTypes(availableLayerTypes);
				layerChooserDialog.setMultiselectable(false);
				if (layerChooserDialog.showDialog(getRootPane(), "Select Layer With The Scores") == LayerChooserDialog.APPROVE_OPTION) {
					otherLayer = layerChooserDialog.getSelectedLayer();
				}
			}
			if (otherLayer != null) {
				GeneScoreType geneScore = Utils.chooseGeneScoreCalculation(getRootPane());
				if (geneScore != null) {
					operation = new GLOScoreFromSCWList(selectedLayer.getData(), (SCWList) otherLayer.getData(), geneScore);
					return operation;
				}
			}
		}
		return null;
	}
}
