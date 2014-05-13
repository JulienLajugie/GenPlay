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
package edu.yu.einstein.genplay.gui.action.layer.sequenceLayer;

import java.util.List;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.nucleotideList.NLOCompare2NucleotideLists;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.nucleotideList.NucleotideList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.dialog.trackChooser.TrackChooser;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.AbstractSCWLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.gui.track.layer.NucleotideLayer;
import edu.yu.einstein.genplay.gui.track.layer.SimpleSCWLayer;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Creates a {@link SCWList} showing the differences between to {@link NucleotideList}
 * @author Julien Lajugie
 */
public class NLACompare2NucleotideLists extends TrackListActionOperationWorker<SCWList> {

	private static final long serialVersionUID = -7322522489324602187L;				// generated ID
	private static final String 	ACTION_NAME = "Compare Sequences"; 				// action name
	private static final String 	DESCRIPTION =
			"Creates a track showing the differences between 2 sequence layers";	// tooltip
	private NucleotideLayer		selectedLayer;										// selected layer
	private NucleotideLayer		otherLayer;											// other layer
	private Track				resultTrack;										// track where to add the result


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = NLACompare2NucleotideLists.class.getName();


	/**
	 * Creates an instance of {@link NLACompare2NucleotideLists}
	 */
	public NLACompare2NucleotideLists() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(SCWList actionResult) {
		if (actionResult != null) {
			AbstractSCWLayer<SCWList> resultLayer = new SimpleSCWLayer(resultTrack, actionResult, selectedLayer.getName() + " and " + otherLayer.getName() + " diff");
			// add info to the history
			resultLayer.getHistory().add("Differences between " + selectedLayer.getName() + " and " + otherLayer.getName(), Colors.GREY);
			resultTrack.getLayers().add(resultLayer);
			resultTrack.setActiveLayer(resultLayer);
		}
	}


	@Override
	public Operation<SCWList> initializeOperation() throws Exception {
		selectedLayer = (NucleotideLayer) getValue("Layer");
		if (selectedLayer != null) {
			LayerChooserDialog layerChooserDialog = new LayerChooserDialog();
			List<Layer<?>> sequenceLayer = Utils.getLayers(getTrackListPanel().getModel().getAllLayers(), new LayerType[] {LayerType.NUCLEOTIDE_LAYER});
			layerChooserDialog.setLayers(sequenceLayer);
			layerChooserDialog.setSelectableLayerTypes(new LayerType[] {LayerType.NUCLEOTIDE_LAYER});
			layerChooserDialog.setMultiselectable(false);
			if (layerChooserDialog.showDialog(getRootPane(), "Select 2nd Sequence Layer") == LayerChooserDialog.APPROVE_OPTION) {
				otherLayer = (NucleotideLayer) layerChooserDialog.getSelectedLayer();
				if (otherLayer != null) {
					resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackListPanel().getModel().getTracks());
					if (resultTrack != null) {
						operation = new NLOCompare2NucleotideLists(selectedLayer.getData(), otherLayer.getData());
						return operation;
					}
				}
			}
		}
		return null;
	}
}