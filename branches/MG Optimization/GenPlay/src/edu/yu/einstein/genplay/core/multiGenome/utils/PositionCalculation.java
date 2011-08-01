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
package edu.yu.einstein.genplay.core.multiGenome.utils;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFIndel;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFSNP;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFSV;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGPosition;

/**
 * Gathers methods of position calculation for multi genome algorithm
 * @author Nicolas Fourel
 * @version 0.1
 */
public class PositionCalculation {
	
	
	
	/**
	 * @param variant 	a variant
	 * @return			its next position on the genome 
	 */
	public static int getNextGenomePosition(MGPosition variant) {
		int nextGenomePosition = variant.getGenomePosition() + 1;
		if (isInsertion(variant)) {
			nextGenomePosition += variant.getLength();
		}
		return nextGenomePosition;
	}
	
	
	/**
	 * @param variant 	a variant
	 * @return 			its position on the reference genome
	 */
	public static int getReferenceGenomePosition(MGPosition variant) {
		int position = variant.getGenomePosition() + variant.getInitialReferenceOffset();
		return position;
	}
	
	
	/**
	 * @param variant 	a variant
	 * @return			its next position on the reference genome
	 */
	public static int getNextReferenceGenomePosition(MGPosition variant) {
		int position = getNextGenomePosition(variant);
		return getNextReferenceGenomePosition(variant, position);
	}
	
	
	/**
	 * @param variant	a variant
	 * @param position	an offset position
	 * @return			its position on the reference genome according to the offset value
	 */
	public static int getNextReferenceGenomePosition(MGPosition variant, int position) {
		int current = getReferenceGenomePosition(variant);
		int difference = position - variant.getGenomePosition();
		if (isInsertion(variant)) {
			if (difference > variant.getLength()) {
				current += difference - variant.getLength();
			} else {
				System.out.println("WARNING: difference < length");
			}
		} else {
			current += difference;
			if (isDeletion(variant)) {
				current += variant.getLength();
			}
		}
		return current;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			its position on the meta genome
	 */
	public static int getMetaGenomePosition(MGPosition variant) {
		int position = variant.getGenomePosition() + variant.getInitialMetaGenomeOffset();
		return position;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			its next position on the meta genome
	 */
	public static int getNextMetaGenomePosition(MGPosition variant) {
		int position = getNextGenomePosition(variant);
		return getNextMetaGenomePosition(variant, position);
	}

	
	/**
	 * @param variant	a variant
	 * @param position	an offset position
	 * @return			its position ont the meta genome according to the offset value
	 */
	public static int getNextMetaGenomePosition(MGPosition variant, int position) {
		int current = getMetaGenomePosition(variant) + (position - variant.getGenomePosition());
		if (!isInsertion(variant)) {
			current += variant.getLength();
		}
		if (position > (variant.getGenomePosition() + variant.getLength())) {
			current += variant.getExtraOffset();
		}
		return current;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			the next offset value of the variant on the reference genome
	 */
	public static int getNextReferencePositionOffset(MGPosition variant) {
		int nextGenomePosition = getNextGenomePosition(variant);
		int nextReferencePosition = getNextReferenceGenomePosition(variant, nextGenomePosition);
		return nextReferencePosition - nextGenomePosition;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			the next offset value of the variant on the meta genome 
	 */
	public static int getNextMetaGenomePositionOffset(MGPosition variant) {
		int nextGenomePosition = getNextGenomePosition(variant);
		int nextMetaGenomePosition = getNextMetaGenomePosition(variant, nextGenomePosition);
		return nextMetaGenomePosition - nextGenomePosition;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			true if the variant is an insertion
	 */
	public static boolean isInsertion (MGPosition variant) {
		if (variant.getType() == VariantType.INSERTION ||
				variant.getType() == VariantType.INS) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			true if the variant is a deletion
	 */
	public static boolean isDeletion (MGPosition variant) {
		if (variant.getType() == VariantType.DELETION ||
				variant.getType() == VariantType.DEL) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			true if the variant is a SNP
	 */
	public static boolean isSNP (MGPosition variant) {
		if (variant.getType() == VariantType.SNPS ||
				variant.getType() == VariantType.SVSNPS) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			true if the variant is an InDel VCF type
	 */
	public static boolean isInDelType (MGPosition variant) {
		if (variant instanceof VCFIndel) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			true if the variant is an SNP VCF type
	 */
	public static boolean isSNPType (MGPosition variant) {
		if (variant instanceof VCFSNP) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			true if the variant is an SV VCF type
	 */
	public static boolean isSVType (MGPosition variant) {
		if (variant instanceof VCFSV) {
			return true;
		}
		return false;
	}
}
