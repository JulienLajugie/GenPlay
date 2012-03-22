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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.toolTipStripe;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantInfo {

	private PanelInformation 		pane;			// pane containing information
	private String 					title;			// header of the pane
	private List<String> 			keys;			// key values
	private List<String> 			values;			// values
	private List<String> 			description;	// keys description


	/**
	 * Constructor of {@link VariantInfo}
	 * @param info string containing information about the INFO field of the variant information
	 */
	protected VariantInfo (Variant variant) {
		title = "Information";
		
		if (variant == null) {
			pane = new PanelInformation(title, null, null, null);
		} else {
			keys = new ArrayList<String>();
			values = new ArrayList<String>();
			description = new ArrayList<String>();

			String[] elements = variant.getInfo().split(";");

			for (String element: elements) {
				String[] map = element.split("=");
				String key = map[0];
				String value = null;
				if (map.length > 1) {
					value = map[1];
				}
				keys.add(key);
				values.add(value);
			}
			
			for (String key: keys) {
				description.add(variant.getPositionInformation().getInfoHeader(key).getDescription());
			}

			pane = new PanelInformation(title, keys, values, description);
		}
	}


	/**
	 * @return the scrollpane containing the information
	 */
	protected JPanel getPane () {
		return pane;
	}

}
