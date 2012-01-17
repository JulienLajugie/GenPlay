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

import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGGenome;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGGenomeForDisplay {
	
	
	private MGGenome	 		genome;			// genome synchronizer
	private MGAlleleForDisplay 	alleleA;		// first allele of the genome
	private MGAlleleForDisplay 	alleleB;		// second allele of the genome
	
	
	/**
	 * Constructor of {@link MGGenomeForDisplay}
	 * @param name full name of the genome
	 */
	protected MGGenomeForDisplay (MGGenome genome) {
		this.genome = genome;
		alleleA = new MGAlleleForDisplay(genome);
		alleleB = new MGAlleleForDisplay(genome);
	}

	
	/**
	 * @return the genome synchronizer
	 */
	public MGGenome getGenome() {
		return genome;
	}
	
	
	/**
	 * @return the allele A of the genome
	 */
	public MGAlleleForDisplay getAlleleA() {
		return alleleA;
	}


	/**
	 * @return the allele B of the genome
	 */
	public MGAlleleForDisplay getAlleleB() {
		return alleleB;
	}

	
	/**
	 * Show the information of the {@link MGGenomeForDisplay}
	 */
	public void show () {
		System.out.println("Genome: " + genome.getName());
		System.out.println("Allele A");
		alleleA.show();
		System.out.println("Allele B");
		alleleB.show();
	}

}
