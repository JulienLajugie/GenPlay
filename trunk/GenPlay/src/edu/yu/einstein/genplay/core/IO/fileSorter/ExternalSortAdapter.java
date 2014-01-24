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
package edu.yu.einstein.genplay.core.IO.fileSorter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.util.Comparator;
import java.util.List;

import edu.yu.einstein.genplay.core.IO.utils.Extractors;
import edu.yu.einstein.genplay.core.comparator.ChromosomeComparator;
import edu.yu.einstein.genplay.core.comparator.StringComparator;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.util.Utils;

/**
 * Adapts the ExternalSort class for GenPlay
 * @author Julien Lajugie
 */
public class ExternalSortAdapter {

	/**
	 * Comparator for genomic files. Compare the chromosome, start and stop positions found at the specied indexes
	 * @author Julien Lajugie
	 */
	private static class GenomicFileLineComparator implements Comparator<String> {

		private static ChromosomeComparator chromosomeComparator = new ChromosomeComparator(); // comparator for chromosomes
		private final int chromoFieldIndex;	// index of the chromosome field
		private final int startFieldIndex;	// index of the start field
		private final int stopFieldIndex;	// index of the stop field

		/**
		 * Creates an instance of {@link ExternalSortAdapter}
		 * @param chromoFieldIndex
		 * @param startFieldIndex
		 * @param stopFieldIndex
		 */
		public GenomicFileLineComparator(int chromoFieldIndex, int startFieldIndex, int stopFieldIndex) {
			this.chromoFieldIndex = chromoFieldIndex;
			this.startFieldIndex = startFieldIndex;
			this.stopFieldIndex = stopFieldIndex;
		}


		@Override
		public int compare(String o1, String o2) {
			if (Extractors.isHeaderLine(o1)) {
				return -1;
			} else if (Extractors.isHeaderLine(o2)) {
				return 1;
			}

			String[] splitLine1 = Utils.splitWithTab(o1);
			String[] splitLine2 = Utils.splitWithTab(o2);
			int cmp;
			try {
				cmp = chromosomeComparator.compareChromosomeName(splitLine1[chromoFieldIndex], splitLine2[chromoFieldIndex]);
			} catch (Exception e) {
				// if the chromosome comparator doesn't work we use a string cpmparator
				cmp = new StringComparator().compare(splitLine1[chromoFieldIndex], splitLine2[chromoFieldIndex]);
			}
			// if the chromosomes are equals we compare the positions
			if (cmp == 0) {
				try {
					ChromosomeWindow cw1 = new SimpleChromosomeWindow(splitLine1[startFieldIndex], splitLine1[stopFieldIndex]);
					ChromosomeWindow cw2 = new SimpleChromosomeWindow(splitLine2[startFieldIndex], splitLine2[stopFieldIndex]);
					cmp = cw1.compareTo(cw2);
				} catch (Exception e) {
					new StringComparator().compare(splitLine1[startFieldIndex], splitLine2[startFieldIndex]);
				}
			}
			return cmp;
		}
	}


	/**
	 * Sorts a genomic file per chromosome, start and then stop position
	 * @param inputFile
	 * @throws IOException
	 */
	public static void externalSortGenomicFile(File inputFile) throws IOException {
		Comparator<String> comparator = generateComparator(inputFile);
		int maxTmpFiles = ExternalSort.DEFAULTMAXTEMPFILES;
		Charset charset = Charset.defaultCharset();
		File tmpDir = new File(Utils.getTmpDirectoryPath());
		File outputFile = generateOutputFile(inputFile);
		List<File> l = ExternalSort.sortInBatch(inputFile, comparator, maxTmpFiles, charset, tmpDir, false, 0, false);
		ExternalSort.mergeSortedFiles(l, outputFile, comparator, charset, false, false, false);
	}


	/**
	 * @param inputFile
	 * @return a string comparator adapted that compares the lines of the input file
	 * @throws InvalidParameterException
	 */
	private static Comparator<String> generateComparator(File inputFile) throws InvalidParameterException {
		String fileExtension = Utils.getExtension(inputFile);
		if (fileExtension == null) {
			throw new InvalidParameterException("Cannot sort the specified file: " + inputFile.getName() + "\n" +
					"Files without extension are not recognized by the sorting function");
		} else if (fileExtension.equalsIgnoreCase("gff")) {
			return new GenomicFileLineComparator(0, 3, 4);
		} else if (fileExtension.equalsIgnoreCase("gr")) {
			return new GenomicFileLineComparator(0, 1, 2);
		} else if (fileExtension.equalsIgnoreCase("bed")) {
			return new GenomicFileLineComparator(0, 1, 2);
		} else if (fileExtension.equalsIgnoreCase("bgr")) {
			return new GenomicFileLineComparator(0, 1, 2);
		} else if (fileExtension.equalsIgnoreCase("pair")) {
			return new GenomicFileLineComparator(0, 4, 4);
		} else if (fileExtension.equalsIgnoreCase("psl")) {
			return new GenomicFileLineComparator(13, 15, 16);
		} else if (fileExtension.equalsIgnoreCase("sam")) {
			return new GenomicFileLineComparator(0, 3, 3);
		} else {
			throw new InvalidParameterException("Cannot sort the specified file: " + inputFile.getName() + "\n" +
					"Files with " + fileExtension + " extension are not recognized by the sorting function");
		}
	}


	/**
	 * @param inputFile input file
	 * @return a new File with prefix ".sorted" added before the extension
	 */
	public static File generateOutputFile(File inputFile) {
		String extension = Utils.getExtension(inputFile);
		String nameWithoutExtension = Utils.getFileNameWithoutExtension(inputFile);
		String newFileName = nameWithoutExtension + ".sorted." + extension;
		File parentDirectory = inputFile.getParentFile();
		return new File(parentDirectory, newFileName);
	}
}
