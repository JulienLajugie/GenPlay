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
package edu.yu.einstein.genplay.core.multiGenome.data.display.content;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGLineContent {

	/** When alternative length cannot be given */
	public static final int NO_ALTERNATIVE = Integer.MIN_VALUE;
	private int referenceGenomePosition;
	private float score;
	private int[] alternatives;
	private Map<String, byte[]> genotypes;


	/**
	 * Constructor of {@link MGLineContent}
	 */
	public MGLineContent () {
		genotypes = new HashMap<String, byte[]>();
	}


	/**
	 * @return the referenceGenomePosition
	 */
	public int getReferenceGenomePosition() {
		return referenceGenomePosition;
	}


	/**
	 * @param referenceGenomePosition the referenceGenomePosition to set
	 */
	public void setReferenceGenomePosition(int referenceGenomePosition) {
		this.referenceGenomePosition = referenceGenomePosition;
	}


	/**
	 * @return the score
	 */
	public float getScore() {
		return score;
	}


	/**
	 * @param score the score to set
	 */
	public void setScore(float score) {
		this.score = score;
	}


	/**
	 * @return the alternatives
	 */
	public int[] getAlternatives() {
		return alternatives;
	}


	/**
	 * @param alternatives the alternatives to set
	 */
	public void setAlternatives(int[] alternatives) {
		this.alternatives = alternatives;
	}


	/**
	 * @return the genotypes
	 */
	public Map<String, byte[]> getGenotypes() {
		return genotypes;
	}


	/**
	 * @param genotypes the genotypes to set
	 */
	public void setGenotypes(Map<String, byte[]> genotypes) {
		this.genotypes = genotypes;
	}


	/**
	 * @param genomeName
	 * @param genotype
	 */
	public void setGenotype(String genomeName, byte[] genotype) {
		genotypes.put(genomeName, genotype);
	}


	@Override
	public String toString () {
		String info = "";
		info += referenceGenomePosition + "\t";
		info += score + "\t";

		for (int alternative: alternatives) {
			info += alternative + ".";
		}

		info += "\t";

		for (String genomeName: genotypes.keySet()) {
			info += genomeName + "(";
			for (byte genotype: genotypes.get(genomeName)) {
				info += genotype + ".";
			}
			info += ")\t";
		}
		return info;
	}

}
