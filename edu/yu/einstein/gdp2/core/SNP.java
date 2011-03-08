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
package yu.einstein.gdp2.core;

import java.io.Serializable;

import yu.einstein.gdp2.core.enums.Nucleotide;


/**
 * A SNP (Single-Nucleotide Polymorphism)
 * @author Julien Lajugie
 * @version 0.1
 */
public class SNP implements Serializable, Comparable<SNP> {
	
	private static final long serialVersionUID = 6378547913690473618L;	// generated ID
	private final String		name;					// name of the SNP
	private final int 			position;				// position of the SNP
	private final Nucleotide 	firstBase;				// first base
	private final int 			firstBaseCount;			// count of the first base
	private final Nucleotide 	secondBase;				// second base
	private final int 			secondBaseCount;		// second base count
	private final boolean		isSecondBaseSignificant;// true if the second base is significant
	
	
	/**
	 * Creates an instance of {@link SNP}
	 * @param name name of the SNP 
	 * @param position position of the SNP
	 * @param firstBase first base
	 * @param firstBaseCount count of the best base
	 * @param secondBase second base
	 * @param secondBaseCount count of the second base
	 * @param isSecondBaseSignificant true if the second base is significant
	 */
	public SNP(String name, int position, Nucleotide firstBase, int firstBaseCount, Nucleotide secondBase, int secondBaseCount, boolean isSecondBaseSignificant) {
		this.name = name;
		this.position = position;
		this.firstBase = firstBase;
		this.firstBaseCount = firstBaseCount;
		this.secondBase = secondBase;
		this.secondBaseCount = secondBaseCount;
		this.isSecondBaseSignificant = isSecondBaseSignificant;
	}


	@Override
	public int compareTo(SNP otherSNP) {
		if (this.position > otherSNP.position) {
			return 1;
		} else if (this.position < otherSNP.position) {
			return -1;
		} else {
			return 0;
		}
	}


	/**
	 * @return the firstBase
	 */
	public final Nucleotide getFirstBase() {
		return firstBase;
	}


	/**
	 * @return the firstBaseCount
	 */
	public final int getFirstBaseCount() {
		return firstBaseCount;
	}


	/**
	 * @return the name of the SNP
	 */
	public final String getName() {
		return name;
	}
	

	/**
	 * @return the position of the SNP
	 */
	public final int getPosition() {
		return position;
	}


	/**
	 * @return the secondBase
	 */
	public final Nucleotide getSecondBase() {
		return secondBase;
	}


	/**
	 * @return the secondBaseCount
	 */
	public final int getSecondBaseCount() {
		return secondBaseCount;
	}


	/**
	 * @return the if the second base is significant
	 */
	public final boolean isSecondBaseSignificant() {
		return isSecondBaseSignificant;
	}	
}
