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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.stripeManagement;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.multiGenomeManager.MultiGenomeManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;


/**
 * 
 * @author Nicolas Fourel
 */
public class MultiGenomeStripe {

	private Map<String, Map<VariantType, Color>> colorAssociation;
	private int transparency;
	private String genomeName;
	
	
	/**
	 * Constructor of {@link MultiGenomeStripe}
	 */
	public MultiGenomeStripe () {
		colorAssociation = new HashMap<String, Map<VariantType,Color>>();
		transparency = 50;
	}
	
	
	/**
	 * @return the colorAssociation
	 */
	public Map<String, Map<VariantType, Color>> getColorAssociation() {
		return colorAssociation;
	}


	/**
	 * Sets stripe type and color association 
	 * @param genomeName	genome formatted name
	 * @param association	stripe type and color association
	 */
	public void addColorInformation (String genomeName, Map<VariantType, Color> association) {
		colorAssociation.put(genomeName, association);
	}
	
	
	public void showInformation () {
		for (String name: colorAssociation.keySet()) {
			for (VariantType type: colorAssociation.get(name).keySet()) {
				System.out.println(name + " - " + type + ": " + colorAssociation.get(name).get(type));
			}
		}
	}
	
	
	private void initStripes () {
		colorAssociation = new HashMap<String, Map<VariantType,Color>>();
		for(Object genome: MultiGenomeManager.getInstance().getFormattedGenomeArray()) {
			String fullName = genome.toString();
			String rawName;
			try {
				rawName = FormattedMultiGenomeName.getRawName(fullName);
			} catch (Exception e) {
				rawName = fullName;
			}
			Map<VariantType, Color> colors = new HashMap<VariantType, Color>();
			if (genomeName.equals(rawName)) {
				colors.put(VariantType.INSERTION, MultiGenomeManager.getInsertionDefaultColor());
				colors.put(VariantType.DELETION, MultiGenomeManager.getDeletionDefaultColor());
				//colors.put(VariantType.SV, MultiGenomeManager.getSvDefaultColor());
				//colors.put(VariantType.SNPS, MultiGenomeManager.getSnpsDefaultColor());
			} else {
				colors.put(VariantType.INSERTION, MultiGenomeManager.getDefaultColor());
				/*colors.put(VariantType.DELETION, MultiGenomeManager.getDefaultColor());
				colors.put(VariantType.SV, MultiGenomeManager.getDefaultColor());
				colors.put(VariantType.SNPS, MultiGenomeManager.getDefaultColor());*/
			}
			colorAssociation.put(fullName, colors);
		}
	}
	
	
	/**
	 * @return the genomeName
	 */
	public String getGenomeName() {
		return genomeName;
	}


	/**
	 * @param genomeName the genomeName to set
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
		initStripes();
	}


	/**
	 * @return the transparency
	 */
	public int getTransparency() {
		return transparency;
	}


	/**
	 * @param transparency the transparency to set
	 */
	public void setTransparency(int alpha) {
		this.transparency = alpha * 100 / 255;
	}
	
}