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
package edu.yu.einstein.genplay.core.multiGenome.export.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import net.sf.samtools.util.BlockCompressedInputStream;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.util.Utils;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BGZIPReader {

	private final VCFFile 						vcfFile;			// file to read
	private final BlockCompressedInputStream 	bcis;				// stream for the file
	private final InputStreamReader				isr;
	private final BufferedReader 				reader;
	private String 								metaDataHeader;		// header of the file
	private String 								fieldDataHeader;	// header of the file
	private VCFLine 							currentLine;		// current line in the file
	//private String columns;
	private final Map<String, Integer> 			genomeMap;			// map between genome names and their related index according to their location on the column line


	/**
	 * Constructor of {@link BGZIPReader}
	 * @param vcfFile		the VCF file
	 * @throws IOException
	 */
	public BGZIPReader (VCFFile vcfFile) throws IOException {
		this.vcfFile = vcfFile;
		this.bcis = new BlockCompressedInputStream(vcfFile.getFile());
		this.isr = new InputStreamReader(bcis);
		this.reader = new BufferedReader(isr);
		this.currentLine = null;
		this.metaDataHeader = "";
		this.fieldDataHeader = "";
		this.genomeMap = new HashMap<String, Integer>();
		initialize();
	}


	/**
	 * Initializes the reader.
	 * It reads the header, the first line of data and then stop.
	 * @throws IOException
	 */
	private void initialize () throws IOException {
		boolean isData = false;

		while (!isData) {
			String line = readLine(reader);
			if ((line != null) && (line.length() > 2)) {
				if (line.substring(0, 2).equals("##")) {

					if (isMetaDataLine(line)) {
						if (!metaDataHeader.isEmpty()) {
							metaDataHeader += "\n";
						}
						metaDataHeader += line;
					} else {
						if (!fieldDataHeader.isEmpty()) {
							fieldDataHeader += "\n";
						}
						fieldDataHeader += line;
					}
				} else if (line.substring(0, 1).equals("#")) {
					//this.columns = line;
					String[] columns = Utils.splitWithTab(line);
					for (int i = 9; i < columns.length; i++) {
						genomeMap.put(columns[i], i);
					}
				} else {
					currentLine = new VCFLine(this, line);
					isData = true;
				}
			}
		}
	}

	private boolean isMetaDataLine (String line) {
		if (line.length() > 8) {
			if (line.substring(2, 2 + VCFColumnName.ALT.toString().length()).equals(VCFColumnName.ALT.toString())) {
				return false;
			} else if (line.substring(2, 2 + VCFColumnName.FILTER.toString().length()).equals(VCFColumnName.FILTER.toString())) {
				return false;
			} else if (line.substring(2, 2 + VCFColumnName.INFO.toString().length()).equals(VCFColumnName.INFO.toString())) {
				return false;
			} else if (line.substring(2, 2 + VCFColumnName.FORMAT.toString().length()).equals(VCFColumnName.FORMAT.toString())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the header without the columns
	 */
	public String getMetaDataHeader () {
		return metaDataHeader;
	}


	/**
	 * Creates the string of fixed columns of a VCF files.
	 * It goes from CHROM to FORMAT included.
	 * It includes tabs (even after the FORMAT field).
	 * @return the formated string of the fixed columns
	 */
	public String getFixedColumns () {
		String columns = "#";
		columns += VCFColumnName.CHROM.toString() + "\t";
		columns += VCFColumnName.POS.toString() + "\t";
		columns += VCFColumnName.ID.toString() + "\t";
		columns += VCFColumnName.REF.toString() + "\t";
		columns += VCFColumnName.ALT.toString() + "\t";
		columns += VCFColumnName.QUAL.toString() + "\t";
		columns += VCFColumnName.FILTER.toString() + "\t";
		columns += VCFColumnName.INFO.toString() + "\t";
		columns += VCFColumnName.FORMAT.toString() + "\t";
		return columns;
	}


	/**
	 * Close the streams
	 */
	public void closeStreams () {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			isr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			bcis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Reads a line from the input stream
	 * @return		the line (or null)
	 * @throws IOException
	 */
	private String readLine(BufferedReader reader) throws IOException {
		return reader.readLine();
	}


	/**
	 * Go to the next line in the file and read it.
	 * Updates the current line.
	 * @throws IOException
	 */
	public void goNextLine () throws IOException {
		currentLine = new VCFLine(this, readLine(reader));
	}


	/**
	 * @return the currentLine
	 */
	public VCFLine getCurrentLine() {
		return currentLine;
	}


	/**
	 * @return the file
	 */
	public VCFFile getVCFFile() {
		return vcfFile;
	}


	/**
	 * @param genomeName the full genome name
	 * @return the column index of the genome
	 */
	public int getIndexFromGenome (String genomeName) {
		return genomeMap.get(FormattedMultiGenomeName.getRawName(genomeName));
	}


	/**
	 * @param index the index of a genome
	 * @return		the genome raw name associated to the index
	 */
	public String getGenomeFromIndex (int index) {
		for (String genome: genomeMap.keySet()) {
			if (genomeMap.get(genome) == index) {
				return genome;
			}
		}
		return null;
	}


	/**
	 * @return the vcfFile
	 */
	public VCFFile getVcfFile() {
		return vcfFile;
	}


	/**
	 * Print the all the lines of the file
	 * @throws IOException
	 */
	public void printAllFile () throws IOException {
		BlockCompressedInputStream bcis = new BlockCompressedInputStream(vcfFile.getFile());
		BufferedReader reader = new BufferedReader(new InputStreamReader(bcis));
		System.out.println("Content of the file " + vcfFile.getFile().getName() + ":");
		boolean end = false;
		while (!end) {
			String currentLine = readLine(reader);
			if (currentLine == null) {
				end = true;
			} else if (!currentLine.isEmpty()) {
				System.out.println(currentLine);
			}
		}
		reader.close();
	}


	/**
	 * Print the all the lines of the file with the detail of each line
	 * @throws IOException
	 */
	public void printFileAsElements () throws IOException {
		BlockCompressedInputStream bcis = new BlockCompressedInputStream(vcfFile.getFile());
		BufferedReader reader = new BufferedReader(new InputStreamReader(bcis));
		System.out.println("Content of the file " + vcfFile.getFile().getName() + ":");
		boolean end = false;
		while (!end) {
			VCFLine currentLine = new VCFLine(this, readLine(reader));
			if (currentLine.isLastLine()) {
				end = true;
			} else if (currentLine.isValid()) {
				currentLine.showElements();
			}
		}
		reader.close();
	}
}
