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

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.binList.BLODensity;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.dialog.trackChooser.TrackChooser;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.util.colors.Colors;



/**
 * Computes the densities of none null bins of the selected {@link BinLayer}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLADensity extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 8669677084318132021L;	// generated ID
	private static final String 	ACTION_NAME = "Density";			// action name
	private static final String 	DESCRIPTION =
			"Computes the densities of none null bins of the selected layer";// tooltip
	private BinLayer 				selectedLayer;						// selected layer
	private Track	 				resultTrack;						// result track
	private Number 					halfWidth;							// half width


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = BLADensity.class.getName();


	/**
	 * Creates an instance of {@link BLADensity}
	 */
	public BLADensity() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			BinLayer newLayer = new BinLayer(resultTrack, actionResult, "Densities of " + selectedLayer.getName());
			// add info to the history
			newLayer.getHistory().add("Result of the density calculation of " + selectedLayer.getName() + ", Half Width = " + halfWidth);
			newLayer.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp", Colors.GREY);
			resultTrack.getLayers().add(newLayer);
		}
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedLayer = (BinLayer) getValue("Layer");
		if (selectedLayer != null) {
			BinList binList = selectedLayer.getData();
			halfWidth = NumberOptionPane.getValue(getRootPane(), "Enter Value", "<html>Enter the half width<br><center>(in number of bins)</center></html>", 1, Integer.MAX_VALUE, 5);
			if(halfWidth != null) {
				resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackListPanel().getModel().getTracks());
				if (resultTrack != null) {
					Operation<BinList> operation = new BLODensity(binList, halfWidth.intValue());
					return operation;
				}
			}
		}
		return null;
	}
}
