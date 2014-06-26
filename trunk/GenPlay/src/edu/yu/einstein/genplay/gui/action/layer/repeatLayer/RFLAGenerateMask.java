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
package edu.yu.einstein.genplay.gui.action.layer.repeatLayer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.repeatFamilyList.RFLOConvertIntoMask;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.checkBoxTableChooser.CheckBoxTableChooserDialog;
import edu.yu.einstein.genplay.gui.dialog.trackChooser.TrackChooser;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.MaskLayer;
import edu.yu.einstein.genplay.gui.track.layer.RepeatLayer;


/**
 * Converts the selected families from the selected repeat track into a Mask
 * @author Julien Lajugie
 */
public class RFLAGenerateMask extends TrackListActionOperationWorker<SCWList> {

	private static final long serialVersionUID = -167849335384206182L;  						// generated ID
	private static final String 	ACTION_NAME = "Convert Into Mask";							// action name
	private static final String 	DESCRIPTION = "Convert the specified families into a mask" + HELP_TOOLTIP_SUFFIX;	// tooltip
	private static final String		HELP_URL = "http://genplay.einstein.yu.edu/wiki/index.php/Documentation#Convert_Into_Mask";
	private RepeatLayer				selectedLayer;												// selected layer
	private Track	 				resultTrack;												// track for the result layer
	List<String> selectedFamilies;

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = RFLAGenerateMask.class.getName();


	/**
	 * Creates an instance of {@link RFLAGenerateMask}
	 */
	public RFLAGenerateMask() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(HELP_URL_KEY, HELP_URL);
	}


	@Override
	protected void doAtTheEnd(SCWList actionResult) {
		if (actionResult != null) {
			String layerName = "Mask from repeats:";
			for (String currentFamily: selectedFamilies) {
				layerName += " " + currentFamily;
			}
			MaskLayer newLayer = new MaskLayer(resultTrack, actionResult, layerName);
			newLayer.getHistory().add(layerName, Color.GRAY);
			resultTrack.getLayers().add(newLayer);
			resultTrack.setActiveLayer(newLayer);
		}
	}


	@Override
	public Operation<SCWList> initializeOperation() throws Exception {
		selectedLayer = (RepeatLayer) getValue("Layer");
		if (selectedLayer != null) {
			RepeatFamilyList selectedTrackData = selectedLayer.getData();
			List<String> families = new ArrayList<String>();
			for (ListView<RepeatFamilyListView> currentRepeatList: selectedTrackData) {
				for (RepeatFamilyListView currentFamily: currentRepeatList) {
					if (!families.contains(currentFamily.getName())) {
						families.add(currentFamily.getName());
					}
				}
			}
			CheckBoxTableChooserDialog<String> familyChooser = new CheckBoxTableChooserDialog<String>();
			familyChooser.setItems(families);
			familyChooser.setSelectedItems(families);
			familyChooser.setOrdering(false);
			if (familyChooser.showDialog(getRootPane()) == CheckBoxTableChooserDialog.APPROVE_OPTION) {
				selectedFamilies = familyChooser.getSelectedItems();
				if ((selectedFamilies != null) && (!selectedFamilies.isEmpty())) {
					resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackListPanel().getModel().getTracks());
					if (resultTrack != null) {
						return new RFLOConvertIntoMask(selectedTrackData, selectedFamilies);
					}
				}
			}
		}
		return null;
	}
}
