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
package edu.yu.einstein.genplay.core.IO.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Factory that tries to create and to return a subclass of {@link Extractor} depending on a file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ExtractorFactory {


	/**
	 * @param fileToExtract file containing the data to extract
	 * @return an {@link Extractor} if the extension of the file is known. null otherwise
	 * @throws FileNotFoundException
	 */
	public static Extractor checkFileExtension(File fileToExtract) throws FileNotFoundException {
		String fileExtension = Utils.getExtension(fileToExtract);
		if (fileExtension == null) {
			return null;
		}
		if (fileExtension.equalsIgnoreCase("gff")) {
			return new GFFExtractor(fileToExtract);
		} else if (fileExtension.equalsIgnoreCase("gtf")) {
			return new GTFExtractor(fileToExtract);
		} else if (fileExtension.equalsIgnoreCase("gff3")) {
			// TODO return gff3 extractor
			return null;
		} else if (fileExtension.equalsIgnoreCase("gr")) {
			return new BedGraphExtractor(fileToExtract);
		} else if (fileExtension.equalsIgnoreCase("bed")) {
			return new BedExtractor(fileToExtract);
		} else if (fileExtension.equalsIgnoreCase("wig")) {
			return new WiggleExtractor(fileToExtract);
		} else if (fileExtension.equalsIgnoreCase("bgr")) {
			return new BedGraphExtractor(fileToExtract);
		} else if (fileExtension.equalsIgnoreCase("pair")) {
			return new PairExtractor(fileToExtract);
		} else if (fileExtension.equalsIgnoreCase("elx")) {
			return new ElandExtendedExtractor(fileToExtract);
		} else if (fileExtension.equalsIgnoreCase("psl")) {
			return new PSLExtractor(fileToExtract);
		} else if (fileExtension.equalsIgnoreCase("sam")) {
			return new SAMExtractor(fileToExtract);
		} else if (fileExtension.equalsIgnoreCase("bam")) {
			return new SAMExtractor(fileToExtract);
		} else if (fileExtension.equalsIgnoreCase("2bit")) {
			return new TwoBitExtractor(fileToExtract);
		} else {
			return null;
		}
	}


	/**
	 * @param fileToExtract
	 * @return an {@link Extractor} if there is some information about
	 * the type of {@link Extractor} in the header. Null otherwise
	 * @throws IOException
	 */
	public static Extractor checkHeader(File fileToExtract) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileToExtract));
			boolean isHeader = true;
			String line = null;

			while (((line = reader.readLine()) != null) && isHeader) {
				isHeader = false;
				line = line.trim();
				if (line.length() == 0) {
					isHeader = true;
				}
				// comment line
				if (line.charAt(0) == '#') {
					isHeader = true;
				}
				// browser line
				if (line.substring(0, 7).equalsIgnoreCase("browser")) {
					isHeader = true;
				}
				// track line
				if (line.substring(0, 5).equalsIgnoreCase("track")) {
					isHeader = true;
				}
				if (isHeader) {
					if (line.substring(0, 5).equalsIgnoreCase("##GFF")) {
						return new GFFExtractor(fileToExtract);
					} else if (line.substring(0, 5).equalsIgnoreCase("##GTF")) {
						return new GTFExtractor(fileToExtract);
					} else if (line.substring(0, 5).equalsIgnoreCase("track")) {
						String lineTmp = line.toLowerCase();
						if (lineTmp.contains("type")) {
							String type = null;
							int indexStart = lineTmp.indexOf("type") + 4;
							line = line.substring(indexStart);
							line = line.trim();
							if (line.charAt(0) == '=') {
								// remove the '=' from the line
								line = line.substring(1);
								line = line.trim();
								if (line.charAt(0) == '\"') {
									// remove the first "
									line = line.substring(1);
									//type = line.split("\"")[0];
									type = Utils.split(line, '"')[0];
								} else {
									line = line.trim();
									//type = line.split(" ")[0].trim();
									type = Utils.split(line, ' ')[0];
								}
								reader.close();
								if (type.equalsIgnoreCase("bedgraph")) {
									return new BedGraphExtractor(fileToExtract);
								} else if (type.equalsIgnoreCase("bed")) {
									return new BedExtractor(fileToExtract);
								} else if (type.equalsIgnoreCase("wiggle")) {
									return new WiggleExtractor(fileToExtract);
								} else if (type.equalsIgnoreCase("eland_extended")) {
									return new ElandExtendedExtractor(fileToExtract);
								} else if (type.equalsIgnoreCase("psl")) {
									return new PSLExtractor(fileToExtract);
								} else if (type.equalsIgnoreCase("sam")) {
									return new SAMExtractor(fileToExtract);
								} else {
									return null;
								}
							}
						}
					}
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return null;
	}


	/**
	 * @param fileToExtract file to extract
	 * @return an instance of a subclass of {@link Extractor} if the type has been found.
	 * Otherwise throw a {@link InvalidFileTypeException}
	 * @throws IOException
	 * @throws InvalidFileTypeException
	 */
	public static Extractor getExtractor(File fileToExtract) throws IOException, InvalidFileTypeException {
		Extractor extractor = null;
		extractor = checkHeader(fileToExtract);
		if (extractor != null) {
			return extractor;
		}
		extractor = checkFileExtension(fileToExtract);
		if (extractor != null) {
			return extractor;
		}
		// if we can't figure out the type of Extractor
		throw new InvalidFileTypeException();
	}
}
