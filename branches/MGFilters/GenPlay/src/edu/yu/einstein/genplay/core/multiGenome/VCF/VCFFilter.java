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
package edu.yu.einstein.genplay.core.multiGenome.VCF;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.list.arrayList.ByteArrayAsBooleanList;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFFilter {

	private final IDFilterInterface filter;				// the filter
	private final VCFReader			reader;				// the file reader
	private IntArrayAsIntegerList	positionList;		// reference genome position array (indexes match with the boolean list)
	private ByteArrayAsBooleanList 	booleanList;		// list of boolean meaning whether variants pass the filter or not
	
	
	/**
	 * Constructor of {@link VCFFilter}
	 * @param filter the filter
	 * @param reader the reader
	 */
	public VCFFilter (IDFilterInterface filter, VCFReader reader) {
		this.filter = filter;
		this.reader = reader;
	}

	
	/**
	 * @param variant the variant
	 * @return true if the variant is valid, false otherwise
	 */
	public boolean isValid (VariantInterface variant) {
		MGPosition information = variant.getFullVariantInformation();
		if (information != null) {
			if (reader.equals(information.getReader())) {
				int index = positionList.getIndex(information.getPos());
				if (index != -1) {
					return booleanList.get(index);
				}
			}
		}
		return false;
	}
	

	/**
	 * @return the filter
	 */
	public IDFilterInterface getFilter() {
		return filter;
	}


	/**
	 * @return the positionList
	 */
	public IntArrayAsIntegerList getPositionList() {
		return positionList;
	}


	/**
	 * @return the booleanList
	 */
	public ByteArrayAsBooleanList getBooleanList() {
		return booleanList;
	}
	
	
	/**
	 * 
	 */
	public void generateFilter () {
		Chromosome chromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
		List<Map<String, Object>> results = null;
		try {
			results = reader.query(chromosome.getName(), 0, chromosome.getLength());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (results != null) {
			
		}
	}
}
