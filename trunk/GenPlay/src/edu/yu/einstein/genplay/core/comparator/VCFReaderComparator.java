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
package edu.yu.einstein.genplay.core.comparator;

import java.util.Comparator;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;

/**
 * This class compares vcf file readers according to the path of the vcf files.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFReaderComparator implements Comparator<VCFFile> {


	@Override
	public int compare(VCFFile reader1, VCFFile reader2) {
		String path1 = reader1.getFile().getPath();
		String path2 = reader1.getFile().getPath();
		return path1.compareToIgnoreCase(path2);
	}


}
