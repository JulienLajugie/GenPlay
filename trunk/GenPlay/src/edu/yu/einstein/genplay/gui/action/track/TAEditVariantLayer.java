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
package edu.yu.einstein.genplay.gui.action.track;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.action.multiGenome.properties.MGARefresh;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.managers.EditingDialogManagerForVariants;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.variants.VariantData;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.variantLayer.VariantLayer;


/**
 * Edit the {@link VariantLayer} information
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TAEditVariantLayer extends TrackListAction {

	private static final long serialVersionUID = 5229478480046927796L;
	private static final String ACTION_NAME = "Edit Variant Layer"; 				// action name
	private static final String DESCRIPTION = "Edit the layer information"; 		// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "TAEditVariantLayer";


	/**
	 * Creates an instance of {@link TAEditVariantLayer}
	 */
	public TAEditVariantLayer() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final Track selectedTrack = getTrackListPanel().getSelectedTrack();
		if (selectedTrack != null) {
			Layer<VariantData> layer = (Layer<VariantData>) getValue("Layer");
			EditingDialogManagerForVariants manager = new EditingDialogManagerForVariants();
			manager.setEnableSelection(false);
			manager.setData(layer.getData());
			List<VariantData> data = manager.showDialog();

			if (data.size() > 0) {
				MGDisplaySettings settings = MGDisplaySettings.getInstance();
				data.get(0).setHasChanged(true);
				layer.setData(data.get(0));

				// Updates track (filters, display)
				MGARefresh action = new MGARefresh();
				action.setPreviousFilterList(settings.getFilterSettings().getAllMGFilters());
				action.actionPerformed(null);
			}
		}
	}

}
