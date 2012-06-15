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
package edu.yu.einstein.genplay.core.multiGenome.export;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.samtools.util.BlockCompressedInputStream;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BGZIPReader {

	private File file;
	private BlockCompressedInputStream bcis;
	private VCFLine currentLine;
	private String header;
	//private String columns;
	private Map<String, Integer> genomeMap;


	/**
	 * Constructor of {@link BGZIPReader}
	 * @param file
	 * @throws IOException
	 */
	protected BGZIPReader (File file) throws IOException {
		this.file = file;
		this.bcis = new BlockCompressedInputStream(file);
		this.currentLine = null;
		this.header = "";
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
			String line = readLine(bcis);
			if (line != null && line.length() > 2) {
				if (line.substring(0, 2).equals("##")) {
					if (!header.isEmpty()) {
						header += "\n";
					}
					header += line;
				} else if (line.substring(0, 1).equals("#")) {
					//this.columns = line;
					String[] columns = line.split("\t");
					for (int i = 9; i < columns.length; i++) {
						genomeMap.put(columns[i], i);
					}
				} else {
					currentLine = new VCFLine(line);
					isData = true;
				}
			}
		}
	}

	
	/**
	 * @return the header without the columns
	 */
	protected String getHeader () {
		return header;
	}
	
	
	/**
	 * Creates the string of fixed columns of a VCF files.
	 * It goes from CHROM to FORMAT included.
	 * It includes tabs (even after the FORMAT field).
	 * @return the formated string of the fixed columns 
	 */
	protected String getFixedColumns () {
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
	 * Reads a line from the input stream
	 * @return		the line (or null)
	 * @throws IOException
	 */
	private String readLine(InputStream is) throws IOException {
		int c = is.read();
		String result = "";
		while (c >= 0 && !isNewLine(c)) {
			result += (char)c;
			c = is.read();
		}
		if (c < 0) {
			return null;
		}
		return result;
	}


	/**
	 * Checks if the integer code is about a new line.
	 * @param code the char code
	 * @return true if the char code is a new line, false otherwise
	 */
	private boolean isNewLine (int code) {
		switch (code) {
		case 10:
			return true;
		case 13:
			return true;
		default:
			return false;
		}
	}


	/**
	 * Go to the next line in the file and read it.
	 * Updates the current line.
	 * @throws IOException
	 */
	protected void goNextLine () throws IOException {
		currentLine = new VCFLine(readLine(bcis));
	}


	/**
	 * @return the currentLine
	 */
	protected VCFLine getCurrentLine() {
		return currentLine;
	}


	/**
	 * @return the file
	 */
	protected File getFile() {
		return file;
	}
	
	
	/**
	 * @param genomeName the full genome name
	 * @return the column index of the genome
	 */
	protected int getGenomeIndex (String genomeName) {
		return genomeMap.get(FormattedMultiGenomeName.getRawName(genomeName));
	}


	/**
	 * Print the all the lines of the file
	 * @throws IOException
	 */
	protected void printAllFile () throws IOException {
		BlockCompressedInputStream bcis = new BlockCompressedInputStream(file);
		System.out.println("Content of the file " + file.getName() + ":");
		boolean end = false;
		while (!end) {
			String currentLine = readLine(bcis);
			if (currentLine == null) {
				end = true;
			} else if (!currentLine.isEmpty()) {
				System.out.println(currentLine);
			}
		}
	}


	/**
	 * Print the all the lines of the file with the detail of each line
	 * @throws IOException
	 */
	protected void printFileAsElements () throws IOException {
		BlockCompressedInputStream bcis = new BlockCompressedInputStream(file);
		System.out.println("Content of the file " + file.getName() + ":");
		boolean end = false;
		while (!end) {
			VCFLine currentLine = new VCFLine(readLine(bcis));
			if (currentLine.isLastLine()) {
				end = true;
			} else if (currentLine.isValid()) {
				currentLine.showElements();
			}
		}
	}
}
