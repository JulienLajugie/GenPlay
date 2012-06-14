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
package edu.yu.einstein.genplay.core.multiGenome.export;

import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface ExportEngineInterface {
	
	
	/**
	 * Export the data to a new VCF
	 */
	public void process ();
	
	
	/**
	 * Set the map of genome according to the files they are mentionned.
	 * @param fileMap the map between genome and their files
	 */
	public void setFileMap (Map<String, List<VCFFile>> fileMap);
	
	
	/**
	 * Set the map of variation types according to their required genomes.
	 * Each genome can have its own variation types list.
	 * @param variationMap the map between genome and their variation types
	 */
	public void setVariationMap (Map<String, List<VariantType>> variationMap);
	
	
	/**
	 * Set the list of filter applied to the track.
	 * @param filterList the list of {@link VCFFilter}
	 */
	public void setFilterList (List<VCFFilter> filterList);
	
	
	/**
	 * Set the path of the new VCF
	 * @param path the VCF path
	 */
	public void setPath (String path);
	
}
