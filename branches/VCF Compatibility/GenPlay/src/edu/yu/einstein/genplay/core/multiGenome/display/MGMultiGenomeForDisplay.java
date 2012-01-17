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
package edu.yu.einstein.genplay.core.multiGenome.display;

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGGenome;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGMultiGenomeForDisplay {

	private List<MGGenomeForDisplay> 	genomesInformation;		// list of the genomes information
	
	
	/**
	 * Constructor of {@link MGMultiGenomeForDisplay}
	 * @param genomes genomes synchronizer
	 */
	public MGMultiGenomeForDisplay (List<MGGenome> genomes) {
		genomesInformation = new ArrayList<MGGenomeForDisplay>();
		for (MGGenome genome: genomes) {
			genomesInformation.add(new MGGenomeForDisplay(genome));
		}
	}
	
	
	/**
	 * @param genomeName genome name
	 * @return	the genome information object
	 */
	public MGGenomeForDisplay getGenomeInformation (String genomeName) {
		return genomesInformation.get(getGenomeIndex(genomeName));
	}
	
	
	/**
	 * Gets the index of a genome in the genome information list
	 * @param genomeName	the name of the genome
	 * @return				its index in the list
	 */
	private int getGenomeIndex (String genomeName) {
		int index = 0;
		for (MGGenomeForDisplay genome: genomesInformation) {
			if (genome.getGenome().getName().indexOf(genomeName) != -1) {
				return index;
			}
			index++;
		}
		return -1;
	}
	
	
	/**
	 * Show the information of the {@link MGMultiGenomeForDisplay}
	 */
	public void show () {
		for (MGGenomeForDisplay genome: genomesInformation) {
			genome.show();
		}
	}
	
}
