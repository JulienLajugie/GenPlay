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
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOTwoLayers;
import edu.yu.einstein.genplay.core.operation.binList.BLOTwoLayers;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.dialog.trackChooser.TrackChooser;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.AbstractSCWLayer;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.gui.track.layer.SimpleSCWLayer;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Computes an arithmetic operation on two layers
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
public final class SCWLATwoLayersOperation extends TrackListActionOperationWorker<SCWList> {

	private static final long 				serialVersionUID = 4027173438789911860L; 		// generated ID
	private static final String 			ACTION_NAME = "Two Layers Operation";			// action name
	private static final String 			DESCRIPTION =
			"Performs an arithmetic operation between two layers" + HELP_TOOLTIP_SUFFIX;	// tooltip
	private static final String				HELP_URL = "http://genplay.einstein.yu.edu/wiki/index.php/Documentation#Two_Layers_Operation";
	private Layer<SCWList>					selectedLayer;									// selected layer
	private Layer<SCWList>					otherLayer = null;								// other layer
	private Track							resultTrack = null;								// result track
	private ScoreOperation 					scoreOperation;									// operation to compute the scores


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = SCWLATwoLayersOperation.class.getName();


	/**
	 * Creates an instance of {@link SCWLATwoLayersOperation}
	 */
	public SCWLATwoLayersOperation() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(HELP_URL_KEY, HELP_URL);
	}


	@Override
	protected void doAtTheEnd(SCWList actionResult) {
		if (actionResult != null) {
			AbstractSCWLayer<?> newLayer;
			if ((selectedLayer.getType() == LayerType.BIN_LAYER) &&
					(otherLayer.getType() == LayerType.BIN_LAYER)) {
				newLayer = new BinLayer(resultTrack, (BinList) actionResult, selectedLayer.getName() + " & " + otherLayer.getName());
			} else {
				newLayer = new SimpleSCWLayer(resultTrack, actionResult, selectedLayer.getName() + " & " + otherLayer.getName());
			}
			// add info to the history
			newLayer.getHistory().add("Operation on two tracks", Colors.GREY);
			newLayer.getHistory().add("Operation: " + scoreOperation.toString(), Colors.GREY);
			newLayer.getHistory().add("First track: " + selectedLayer.getName(), Colors.GREY);
			newLayer.getHistory().add("Second track: " + otherLayer.getName(), Colors.GREY);
			resultTrack.getLayers().add(newLayer);
			resultTrack.setActiveLayer(newLayer);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public Operation<SCWList> initializeOperation() {
		selectedLayer = (AbstractSCWLayer<SCWList>) getValue("Layer");
		if (selectedLayer != null) {
			LayerChooserDialog layerChooserDialog = new LayerChooserDialog();
			layerChooserDialog.setLayers(getTrackListPanel().getModel().getAllLayers());
			LayerType[] selectableLayers = {LayerType.BIN_LAYER, LayerType.SIMPLE_SCW_LAYER, LayerType.MASK_LAYER};
			layerChooserDialog.setSelectableLayerTypes(selectableLayers);
			layerChooserDialog.setMultiselectable(false);
			if (layerChooserDialog.showDialog(getRootPane(), "Select 2nd Layer") == LayerChooserDialog.APPROVE_OPTION) {
				otherLayer = (Layer<SCWList>) layerChooserDialog.getSelectedLayer();
				if (otherLayer != null) {
					resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackListPanel().getModel().getTracks());
					if (resultTrack != null) {
						scoreOperation = Utils.chooseScoreCalculation(getRootPane());
						if (scoreOperation != null) {
							if ((selectedLayer.getType() == LayerType.BIN_LAYER) &&
									(otherLayer.getType() == LayerType.BIN_LAYER)) {
								operation = new BLOTwoLayers((BinList) selectedLayer.getData(), (BinList) otherLayer.getData(), scoreOperation);
							} else {
								operation = new SCWLOTwoLayers(selectedLayer.getData(), otherLayer.getData(), scoreOperation);
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
