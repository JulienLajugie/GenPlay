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
package edu.yu.einstein.genplay.dataStructure.enums;


/**
 * Enumeration of the different types of gene or exon scores
 * @author Julien Lajugie
 */
public enum GeneScoreType {

	/**
	 * Gene score is the sum of the coverage of gene or exon
	 */
	BASE_COVERAGE_SUM ("Base Coverage Sum"),

	/**
	 * Maximum coverage on the gene or exon
	 */
	MAXIMUM_COVERAGE ("Maximum Coverage"),

	/**
	 * Minimum coverage on the gene or exon
	 */
	MINIMUM_COVERAGE ("Minimum Coverage"),

	/**
	 * RPKM score (Read count Per Kilobase per Million mapped reads)
	 */
	RPKM ("RPKM");


	/**
	 * @param description a description of a {@link GeneScoreType}
	 * @return the {@link GeneScoreType} element having the specified description. Null if none
	 */
	public static GeneScoreType lookup(String description) {
		for (GeneScoreType currentScoreType: GeneScoreType.values()) {
			if (currentScoreType.getDescription().equals(description)) {
				return currentScoreType;
			}
		}
		return null;
	}


	/**
	 * Description of the gene score
	 */
	private String description;


	/**
	 * Creates an instance of {@link GeneScoreType}
	 * @param description
	 */
	private GeneScoreType(String description) {
		this.description = description;
	}


	/**
	 * @return a description of the {@link GeneScoreType}
	 */
	public String getDescription() {
		return description;
	};


	@Override
	public String toString() {
		return description;
	}
}
