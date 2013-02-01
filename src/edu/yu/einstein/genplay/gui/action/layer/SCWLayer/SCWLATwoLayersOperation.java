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

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.enums.ScoreCalculationTwoLayersMethod;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOTwoLayers;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.dialog.trackChooser.TrackChooser;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.gui.track.layer.SCWLayer;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Computes an arithmetic operation on two layers
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class SCWLATwoLayersOperation extends TrackListActionOperationWorker<ChromosomeListOfLists<?>> {

	private static final long 				serialVersionUID = 4027173438789911860L; 		// generated ID
	private static final String 			ACTION_NAME = "Two Layers Operation";			// action name
	private static final String 			DESCRIPTION = "Run operation on two layers";	// tooltip
	private Layer<?> 						selectedLayer;									// selected layer
	private Layer<?> 						otherLayer = null;								// other layer
	private Track							resultTrack = null;								// result track
	private ScoreCalculationTwoLayersMethod 	scm;


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
	}


	@Override
	public Operation<ChromosomeListOfLists<?>> initializeOperation() {
		selectedLayer = (SCWLayer) getValue("Layer");
		if (selectedLayer != null) {
			LayerChooserDialog layerChooserDialog = new LayerChooserDialog();
			layerChooserDialog.setLayers(getTrackListPanel().getModel().getAllLayers());
			LayerType[] selectableLayers = {LayerType.BIN_LAYER, LayerType.SCW_LAYER, LayerType.MASK_LAYER};
			layerChooserDialog.setSelectableLayerTypes(selectableLayers);
			layerChooserDialog.setMultiselectable(false);
			if (layerChooserDialog.showDialog(getRootPane(), "Select 2nd Layer") == LayerChooserDialog.APPROVE_OPTION) {
				otherLayer = layerChooserDialog.getSelectedLayer();
				if (otherLayer != null) {
					resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackListPanel().getModel().getTracks());
					if (resultTrack != null) {
						scm = Utils.chooseScoreCalculationTwoLayersMethod(getRootPane());
						if (scm != null) {
							operation = new SCWLOTwoLayers(	(ChromosomeListOfLists<?>)selectedLayer.getData(),
									(ChromosomeListOfLists<?>)otherLayer.getData(),
									scm);
							return operation;
						}
					}
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ChromosomeListOfLists<?> actionResult) {
		if (actionResult != null) {
			SCWLayer newLayer = new SCWLayer(resultTrack, (ScoredChromosomeWindowList)actionResult, selectedLayer.getName() + " & " + otherLayer.getName());
			// add info to the history
			newLayer.getHistory().add("Operation on two tracks", Colors.GREY);
			newLayer.getHistory().add("Operation: " + scm.toString(), Colors.GREY);
			newLayer.getHistory().add("First track: " + selectedLayer.getName(), Colors.GREY);
			newLayer.getHistory().add("Second track: " + otherLayer.getName(), Colors.GREY);
			resultTrack.getLayers().add(newLayer);
			resultTrack.setActiveLayer(newLayer);
		}
	}
}
