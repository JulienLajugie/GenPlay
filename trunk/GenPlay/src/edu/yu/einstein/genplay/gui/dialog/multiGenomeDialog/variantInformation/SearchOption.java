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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.variantInformation;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SearchOption {

	/** True if insertions have to be included, false otherwise. */
	public boolean includeInsertion;
	/** True if deletions have to be included, false otherwise. */
	public boolean includeDeletion;
	/** True if SNPs have to be included, false otherwise. */
	public boolean includeSNP;
	/** True if references have to be included, false otherwise. */
	public boolean includeReference;
	/** True if heterozygotes have to be included, false otherwise. */
	public boolean includeHeterozygote;
	/** True if homozygotes have to be included, false otherwise. */
	public boolean includeHomozygote;
	/** True if no calls have to be included, false otherwise. */
	public boolean includeNoCall;


	/**
	 * Constructor of {@link SearchOption}
	 */
	protected SearchOption () {
		setOptions(true, true, true, true, true, true, true);
	}


	/**
	 * Initialize the {@link SearchOption}
	 * @param includeInsertion
	 * @param includeDeletion
	 * @param includeSNP
	 * @param includeReference
	 * @param includeHeterozygote
	 * @param includeHomozygote
	 * @param includeNoCall
	 */
	protected void setOptions(boolean includeInsertion, boolean includeDeletion, boolean includeSNP, boolean includeReference, boolean includeHeterozygote, boolean includeHomozygote, boolean includeNoCall) {
		this.includeInsertion = includeInsertion;
		this.includeDeletion = includeDeletion;
		this.includeSNP = includeSNP;
		this.includeReference = includeReference;
		this.includeHeterozygote = includeHeterozygote;
		this.includeHomozygote = includeHomozygote;
		this.includeNoCall = includeNoCall;
	}


	/**
	 * @return the includeInsertion
	 */
	public boolean isIncludeInsertion() {
		return includeInsertion;
	}


	/**
	 * @return the includeDeletion
	 */
	public boolean isIncludeDeletion() {
		return includeDeletion;
	}


	/**
	 * @return the includeSNP
	 */
	public boolean isIncludeSNP() {
		return includeSNP;
	}


	/**
	 * @return the includeReference
	 */
	public boolean isIncludeReference() {
		return includeReference;
	}


	/**
	 * @return the includeHeterozygote
	 */
	public boolean isIncludeHeterozygote() {
		return includeHeterozygote;
	}


	/**
	 * @return the includeHomozygote
	 */
	public boolean isIncludeHomozygote() {
		return includeHomozygote;
	}


	/**
	 * @return the includeNoCall
	 */
	public boolean isIncludeNoCall() {
		return includeNoCall;
	}


	@Override
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		// object must be Test at this point
		SearchOption test = (SearchOption)obj;
		return (includeInsertion == test.isIncludeInsertion()) &&
				(includeDeletion == test.isIncludeDeletion()) &&
				(includeSNP == test.isIncludeSNP()) &&
				(includeReference == test.isIncludeReference()) &&
				(includeHeterozygote == test.isIncludeHeterozygote()) &&
				(includeHomozygote == test.isIncludeHomozygote()) &&
				(includeNoCall == test.isIncludeNoCall());
	}
}
