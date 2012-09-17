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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes.StripesData;
import edu.yu.einstein.genplay.gui.track.drawer.multiGenome.MultiGenomeDrawer;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ExportSettings {

	private final Map<String, List<VariantType>> variationMap;
	private final Map<String, List<VCFFile>> fileMap;
	private final List<MGFilter> filterList;
	private final List<VCFFile> fileList;


	/**
	 * Constructor of {@link ExportSettings}
	 * @param genomeDrawer the genome drawer
	 */
	public ExportSettings (MultiGenomeDrawer genomeDrawer) {
		variationMap = getVariationMap(genomeDrawer.getStripesList());
		fileMap = getGenomeFileMap(genomeDrawer.getStripesList());
		filterList = genomeDrawer.getFiltersList();
		fileList = getFileList(fileMap);
	}


	/**
	 * Retrieves a map between genome names and their list of variant type from a list of stripes.
	 * @param stripeList the list of stripes
	 * @return the map genome/file list
	 */
	private Map<String, List<VariantType>> getVariationMap (List<StripesData> stripeList) {
		Map<String, List<VariantType>> map = new HashMap<String, List<VariantType>>();
		for (StripesData stripe: stripeList) {
			String genome = stripe.getGenome();
			List<VariantType> variationList = stripe.getVariationTypeList();
			if (!map.containsKey(genome)) {
				map.put(genome, new ArrayList<VariantType>());
			}
			List<VariantType> currentList = map.get(genome);
			for (VariantType variantType: variationList) {
				if (!currentList.contains(variantType)) {
					currentList.add(variantType);
				}
			}
		}
		return map;
	}


	/**
	 * Retrieves a map between genome names and their list of file from a list of stripes.
	 * The selection takes into account what kind of variation a file can handle according to the genome.
	 * If insertions are required but the file contains deletion for the same genome, it won't be selected.
	 * @param stripeList the list of stripes
	 * @return the map genome/file list
	 */
	private Map<String, List<VCFFile>> getGenomeFileMap (List<StripesData> stripeList) {
		Map<String, List<VCFFile>> map = new HashMap<String, List<VCFFile>>();
		Map<String, List<VCFFile>> projectMap = ProjectManager.getInstance().getMultiGenomeProject().getGenomeFileAssociation();
		for (StripesData stripe: stripeList) {
			String genome = stripe.getGenome();
			if (!map.containsKey(genome)) {
				map.put(genome, new ArrayList<VCFFile>());
			}
			List<VCFFile> projectList = projectMap.get(genome);
			List<VCFFile> currentList = map.get(genome);
			for (VCFFile file: projectList) {
				if (!currentList.contains(file) && canManageRequirements(file, genome, stripe.getVariationTypeList())) {
					currentList.add(file);
				}
			}
		}
		return map;
	}


	/**
	 * Checks if a file contains data about the given genome for at least one of the given variant type.
	 * @param file			the VCF file
	 * @param genomeName	the genome name
	 * @param variantList	the variation list
	 * @return				true if the file contains information for that genome and those variation types, false otherwise
	 */
	private boolean canManageRequirements (VCFFile file, String genomeName, List<VariantType> variantList) {
		for (VariantType variantType: variantList) {
			if (file.canManage(genomeName, variantType)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * @return the number of file involved into the export process
	 */
	private List<VCFFile> getFileList (Map<String, List<VCFFile>> fileMap) {
		List<VCFFile> fileList = new ArrayList<VCFFile>();
		for (String genome: fileMap.keySet()) {
			List<VCFFile> projectList = fileMap.get(genome);
			for (VCFFile file: projectList) {
				if (!fileList.contains(file)) {
					fileList.add(file);
				}
			}
		}
		return fileList;
	}


	/**
	 * @return the list of all genomes
	 */
	public List<String> getGenomeNames () {
		List<String> result = new ArrayList<String>(variationMap.keySet());
		return result;
	}



	/**
	 * @return the number of file involved into the export process
	 */
	public int getFileNumber () {
		return fileList.size();
	}


	/**
	 * @return the variationMap
	 */
	public Map<String, List<VariantType>> getVariationMap() {
		return variationMap;
	}


	/**
	 * @return the fileMap
	 */
	public Map<String, List<VCFFile>> getFileMap() {
		return fileMap;
	}


	/**
	 * @return the filterList
	 */
	public List<MGFilter> getFilterList() {
		return filterList;
	}


	/**
	 * @return the fileList
	 */
	public List<VCFFile> getFileList() {
		return fileList;
	}

}
