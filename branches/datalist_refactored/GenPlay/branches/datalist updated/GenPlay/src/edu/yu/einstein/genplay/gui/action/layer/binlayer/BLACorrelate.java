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

import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOCorrelate;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.CorrelationReportDialog;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;


/**
 * Computes the coefficient of correlation for every chromosome between
 * the selected {@link BinLayer} and another {@link BinLayer}.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLACorrelate extends TrackListActionOperationWorker<Double[]> {

	private static final long serialVersionUID = -3513622153829181945L; // generated ID
	private static final String 	ACTION_NAME = "Correlation";		// action name
	private static final String 	DESCRIPTION =
			"Compute the coefficient of correlation between " +
					"the selected layer and another layer";				// tooltip
	private BinLayer 				selectedLayer;						// 1st selected layer
	private BinLayer 				otherLayer;							// 2nd selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = BLACorrelate.class.getName();


	/**
	 * Creates an instance of {@link BLACorrelate}
	 */
	public BLACorrelate() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Double[]> initializeOperation() {
		selectedLayer = (BinLayer) getValue("Layer");
		if (selectedLayer != null) {
			LayerChooserDialog layerChooserDialog = new LayerChooserDialog();
			layerChooserDialog.setLayers(getTrackListPanel().getModel().getAllLayers());
			LayerType[] selectableLayers = {LayerType.BIN_LAYER};
			layerChooserDialog.setSelectableLayerTypes(selectableLayers);
			layerChooserDialog.setMultiselectable(false);
			if (layerChooserDialog.showDialog(getRootPane(), "Select Layer") == LayerChooserDialog.APPROVE_OPTION) {
				otherLayer = (BinLayer) layerChooserDialog.getSelectedLayer();
				if (otherLayer != null) {
					BinList binList1 = selectedLayer.getData();
					BinList binList2 = otherLayer.getData();
					Operation<Double[]> operation = new BLOCorrelate(binList1, binList2);
					return operation;
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Double[] actionResult) {
		if (actionResult != null) {
			CorrelationReportDialog.showDialog(getRootPane(), actionResult, selectedLayer, otherLayer);
		}
	}
}
