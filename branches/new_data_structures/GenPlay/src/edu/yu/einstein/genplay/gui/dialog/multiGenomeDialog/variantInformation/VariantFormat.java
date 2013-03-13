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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.variantInformation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFHeader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantFormat {

	private PanelInformation 		pane;			// scrollpane containing information
	private final String 			title;			// header of the pane
	private List<String> 			keys;			// key values
	private List<String> 			values;			// values
	private List<String> 			description;	// keys description


	/**
	 * Constructor of {@link VariantFormat}
	 * @param formatHeader string containing header information about the FORMAT field of the variant information
	 * @param formatValues string containing values information about the FORMAT field of the variant information according to a specific genome
	 */
	protected VariantFormat (Variant variant, VCFLine line, String genomeName) {
		title = "Format";

		//if ((line == null) || (variant instanceof ReferenceVariant)) {
		if (line == null) {
			pane = new PanelInformation(title, null, null, null);
		} else {
			keys = new ArrayList<String>();
			values = new ArrayList<String>();
			description = new ArrayList<String>();

			String[] headerElements = line.getFormat();
			String[] valueElements = line.getFormatValues(FormattedMultiGenomeName.getRawName(genomeName));

			VCFHeader header = null;
			if (line.getGenomeIndexer() instanceof VCFHeader) {
				header = (VCFHeader) line.getGenomeIndexer();
				for (int i = 0; i < headerElements.length; i++) {
					VCFHeaderAdvancedType headerField = header.getFormatHeaderFromID(headerElements[i]);
					if (headerField != null) {
						keys.add(headerField.getId());
						description.add(headerField.getDescription());
						if (i < valueElements.length) {
							values.add(valueElements[i]);
						} else {
							values.add("-");
						}
					}
				}
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
