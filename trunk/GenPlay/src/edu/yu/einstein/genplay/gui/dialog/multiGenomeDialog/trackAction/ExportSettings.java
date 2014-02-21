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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filterDialog.variants.VariantData;
import edu.yu.einstein.genplay.gui.track.layer.variantLayer.VariantLayer;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ExportSettings {

	private Map<String, List<VariantType>> variationMap;
	private Map<String, List<VCFFile>> fileMap;
	private List<MGFilter> filterList;
	private List<VCFFile> fileList;


	/**
	 * Constructor of {@link ExportSettings}
	 * @param layer a {@link VariantLayer}
	 */
	public ExportSettings (VariantLayer layer) {
		List<VariantLayer> layers = new ArrayList<VariantLayer>();
		layers.add(layer);
		initialize(layers);
	}


	/**
	 * Constructor of {@link ExportSettings}
	 * @param layers a list {@link VariantLayer}
	 */
	public ExportSettings (List<VariantLayer> layers) {
		initialize(layers);
	}


	/**
	 * Initializes the export settings
	 * @param layers a list of {@link VariantLayer}
	 */
	public void initialize (List<VariantLayer> layers) {
		List<VariantData> data = getDataList(layers);
		variationMap = getVariationMap(data);
		fileMap = getGenomeFileMap(data);
		fileList = getFileList(fileMap);
		filterList = getFilters(layers);
	}


	/**
	 * @param layers a list of {@link VariantLayer}
	 * @return the {@link VariantData} list from the {@link VariantLayer} list
	 */
	private List<VariantData> getDataList (List<VariantLayer> layers) {
		List<VariantData> data = new ArrayList<VariantData>();
		for (VariantLayer layer: layers) {
			data.add(layer.getData());
		}
		return data;
	}


	/**
	 * Retrieves a map between genome names and their list of variant type from a list of stripes.
	 * @param stripeList the list of stripes
	 * @return the map genome/file list
	 */
	private Map<String, List<VariantType>> getVariationMap (List<VariantData> stripeList) {
		Map<String, List<VariantType>> map = new HashMap<String, List<VariantType>>();
		for (VariantData stripe: stripeList) {
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
	private Map<String, List<VCFFile>> getGenomeFileMap (List<VariantData> stripeList) {
		Map<String, List<VCFFile>> map = new HashMap<String, List<VCFFile>>();
		Map<String, List<VCFFile>> projectMap = ProjectManager.getInstance().getMultiGenomeProject().getGenomeFileAssociation();
		for (VariantData stripe: stripeList) {
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


	private List<MGFilter> getFilters (List<VariantLayer> layers) {
		List<MGFilter> filters = new ArrayList<MGFilter>();
		for (VariantLayer layer: layers) {
			for (MGFilter filter: layer.getFilters()) {
				if (!filters.contains(filter)) {
					filters.add(filter);
				}
			}
		}
		return filters;
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


	/**
	 * @return the allele type (BOTH)
	 */
	public AlleleType getAlleleType() {
		return AlleleType.BOTH;
	}

}
