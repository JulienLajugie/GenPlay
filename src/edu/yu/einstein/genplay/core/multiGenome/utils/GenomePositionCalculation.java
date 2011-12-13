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
package edu.yu.einstein.genplay.core.multiGenome.utils;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFIndel;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFSNP;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFSV;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;

/**
 * Gathers methods of position calculation for multi genome algorithm
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GenomePositionCalculation {
	
	
	
	/**
	 * @param variant 	a variant
	 * @return			its next position on the genome 
	 */
	public static int getNextGenomePosition(Variant variant) {
		int nextGenomePosition = variant.getGenomePosition() + 1;
		if (VariantType.isInsertion(variant.getType())){
			nextGenomePosition += variant.getLength();
		}
		return nextGenomePosition;
	}
	
	
	/**
	 * @param variant 	a variant
	 * @return 			its position on the reference genome
	 */
	public static int getReferenceGenomePosition(Variant variant) {
		int position = variant.getGenomePosition() + variant.getInitialReferenceOffset();
		return position;
	}
	
	
	/**
	 * @param variant 	a variant
	 * @return			its next position on the reference genome
	 */
	public static int getNextReferenceGenomePosition(Variant variant) {
		int position = getNextGenomePosition(variant);
		return getNextReferenceGenomePosition(variant, position);
	}
	
	
	/**
	 * @param variant	a variant
	 * @param position	an offset position
	 * @return			its position on the reference genome according to the offset value
	 */
	public static int getNextReferenceGenomePosition(Variant variant, int position) {
		int current = getReferenceGenomePosition(variant);
		int difference = position - variant.getGenomePosition();
		if (VariantType.isInsertion(variant.getType())){
			if (difference > variant.getLength()) {
				current += difference - variant.getLength();
			} else {
				System.out.println("WARNING: difference < length");
			}
		} else {
			current += difference;
			if (VariantType.isDeletion(variant.getType())){
				current += variant.getLength();
			}
		}
		return current;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			its position on the meta genome
	 */
	public static int getMetaGenomePosition(Variant variant) {
		int position = variant.getGenomePosition() + variant.getInitialMetaGenomeOffset();
		return position;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			its next position on the meta genome
	 */
	public static int getNextMetaGenomePosition(Variant variant) {
		int position = getNextGenomePosition(variant);
		return getNextMetaGenomePosition(variant, position);
	}

	
	/**
	 * @param variant	a variant
	 * @param position	an offset position
	 * @return			its position ont the meta genome according to the offset value
	 */
	public static int getNextMetaGenomePosition(Variant variant, int position) {
		int current = getMetaGenomePosition(variant) + (position - variant.getGenomePosition());
		if (!VariantType.isInsertion(variant.getType()) && !VariantType.isSNP(variant.getType())) {
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
	public static int getNextReferencePositionOffset(Variant variant) {
		int nextGenomePosition = getNextGenomePosition(variant);
		int nextReferencePosition = getNextReferenceGenomePosition(variant, nextGenomePosition);
		return nextReferencePosition - nextGenomePosition;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			the next offset value of the variant on the meta genome 
	 */
	public static int getNextMetaGenomePositionOffset(Variant variant) {
		int nextGenomePosition = getNextGenomePosition(variant);
		int nextMetaGenomePosition = getNextMetaGenomePosition(variant, nextGenomePosition);
		return nextMetaGenomePosition - nextGenomePosition;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			true if the variant is an InDel VCF type
	 */
	public static boolean isInDelType (Variant variant) {
		if (variant instanceof VCFIndel) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			true if the variant is an SNP VCF type
	 */
	public static boolean isSNPType (Variant variant) {
		if (variant instanceof VCFSNP) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param variant	a variant
	 * @return			true if the variant is an SV VCF type
	 */
	public static boolean isSVType (Variant variant) {
		if (variant instanceof VCFSV) {
			return true;
		}
		return false;
	}
}
