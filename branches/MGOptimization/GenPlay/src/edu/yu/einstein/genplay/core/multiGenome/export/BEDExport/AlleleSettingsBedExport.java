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
package edu.yu.einstein.genplay.core.multiGenome.export.BEDExport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class AlleleSettingsBedExport {

	private final File 			bedFile;
	private FileWriter 			fw;
	private BufferedWriter 		data;
	private final AlleleType 	allele;

	private int					charIndex;
	private int					currentOffset;
	private int					currentLength;
	private int					currentStart;
	private int					currentStop;
	private int					currentAltIndex;


	/**
	 * Constructor of {@link AlleleSettingsBedExport}
	 * @param path
	 * @param allele
	 */
	protected AlleleSettingsBedExport (String path, AlleleType allele) {
		bedFile = getFile(path, allele);
		this.allele = allele;
		currentOffset = 0;
		if (allele.equals(AlleleType.ALLELE01)) {
			charIndex = 0;
		} else if (allele.equals(AlleleType.ALLELE02)) {
			charIndex = 2;
		}
	}


	/**
	 * Create the path according to the allele
	 * @param path		the path of the file
	 * @param allele	the allele to export
	 * @return the file
	 */
	private File getFile (String path, AlleleType allele) {
		int length = path.length() - 4;
		String newPath = path.substring(0, length);
		newPath += "_";
		newPath += allele.toString().toLowerCase();
		newPath += ".bed";
		return new File(newPath);
	}


	/**
	 * Write the line into the file
	 * @param line	the line to write
	 * @throws IOException
	 */
	protected void write (String line) throws IOException {
		if ((line != null) && !line.isEmpty()) {
			data.write(line + "\n");
		}
	}


	/**
	 * Open the file streams
	 * @throws IOException
	 */
	protected void openStreams () throws IOException {
		fw = new FileWriter(bedFile);
		data = new BufferedWriter(fw);
	}


	/**
	 * Close the file streams
	 * @throws IOException
	 */
	protected void closeStreams () throws IOException {
		data.close();
		fw.close();
	}


	/**
	 * Initializes the current information about:
	 * - start and stop position (on the current genome)
	 * - length
	 * - alternative index
	 * @param lengths		lengths of variations in the line
	 * @param currentLine	the current line
	 * @param altIndex		the index of the alternative
	 */
	public void initializeCurrentInformation (int[] lengths, VCFLine currentLine, int altIndex) {
		currentAltIndex = altIndex;
		if (currentAltIndex == -1) {
			currentLength = 0;
			currentStart = Integer.parseInt(currentLine.getPOS()) + currentOffset;
			currentStop = currentStart + 1;
		} else if (currentAltIndex > -1) {
			currentLength = lengths[currentAltIndex];
			currentStart = Integer.parseInt(currentLine.getPOS()) + currentOffset;
			currentStop = currentStart + 1;
			currentOffset += currentLength;
			if (currentLength > 0) {
				currentStop += currentLength;
			}
		} else {
			currentLength = 0;
			currentStart = -1;
			currentStop = -1;
		}
	}


	/**
	 * Initializes the current information about:
	 * - start and stop position (on the reference genome)
	 * - length
	 * - alternative index
	 * @param lengths		lengths of variations in the line
	 * @param currentLine	the current line
	 * @param altIndex		the index of the alternative
	 */
	public void initializeCurrentInformationForReferenceGenome (int[] lengths, VCFLine currentLine, int altIndex) {
		currentAltIndex = altIndex;
		if (currentAltIndex == -1) {
			currentLength = 0;
			currentStart = Integer.parseInt(currentLine.getPOS());
			currentStop = currentStart + 1;
		} else if (currentAltIndex > -1) {
			currentLength = lengths[currentAltIndex];
			currentStart = Integer.parseInt(currentLine.getPOS());
			currentStop = currentStart + 1;
			if (currentLength < 0) {
				currentStop += Math.abs(currentLength);
			}
		} else {
			currentLength = 0;
			currentStart = -1;
			currentStop = -1;
		}
	}


	/**
	 * Updates the current information using information from the other allele.
	 * e.g.: with a 0/1 genotype, information in the 0 allele has to be updated with information from the 1 allele
	 * @param allele the other allele
	 */
	public void updateCurrentInformation (AlleleSettingsBedExport allele) {
		if (isReference() && allele.isAlternative()) {
			currentLength = allele.getCurrentLength();
			currentOffset += currentLength;
			if (currentLength > 0) {
				currentStop += currentLength;
			}
		}
	}


	/**
	 * Updates the current information using information from the other allele.
	 * e.g.: with a 0/1 genotype, information in the 0 allele has to be updated with information from the 1 allele
	 * @param allele the other allele
	 */
	public void updateCurrentInformationForReferenceGenome (AlleleSettingsBedExport allele) {
		if (isReference() && allele.isAlternative()) {
			currentLength = allele.getCurrentLength();
			if (currentLength < 0) {
				currentStop += Math.abs(currentLength);
			}
		}
	}


	/**
	 * @param line the current VCF line
	 * @return a name for the position
	 */
	public String getName (VCFLine line) {
		String name = "";
		if (currentLength == 0) {
			name = "SNP:";
		} else if (currentLength > 0) {
			name = "INS:";
		} else {
			name = "DEL:";
		}
		name += currentLength + ":";
		name += line.getID();
		return name;
	}


	/**
	 * @return true if the current information refers to the reference, false otherwise
	 */
	public boolean isReference () {
		if (currentAltIndex == -1) {
			return true;
		}
		return false;
	}


	/**
	 * @return true if the current information refers to known variation (reference/alternatives), false otherwise (if '.')
	 */
	public boolean isKnown () {
		if (currentAltIndex > -2) {
			return true;
		}
		return false;
	}


	/**
	 * @return true if the current information refers to an alternative, false otherwise
	 */
	public boolean isAlternative () {
		if (currentAltIndex > -1) {
			return true;
		}
		return false;
	}


	/**
	 * @return the offset
	 */
	public int getOffset() {
		return currentOffset;
	}


	/**
	 * @param offset the offset to set
	 */
	public void addOffset(int offset) {
		this.currentOffset += offset;
	}


	/**
	 * @return the bedFile
	 */
	public File getBedFile() {
		return bedFile;
	}


	/**
	 * @return the allele
	 */
	public AlleleType getAllele() {
		return allele;
	}


	/**
	 * @return the charIndex
	 */
	public int getCharIndex() {
		return charIndex;
	}


	/**
	 * @return the currentLength
	 */
	public int getCurrentLength() {
		return currentLength;
	}


	/**
	 * @return the currentAltIndex
	 */
	public int getCurrentAltIndex() {
		return currentAltIndex;
	}


	/**
	 * @return the currentStart
	 */
	public int getCurrentStart() {
		return currentStart;
	}


	/**
	 * @return the currentStop
	 */
	public int getCurrentStop() {
		return currentStop;
	}

}
