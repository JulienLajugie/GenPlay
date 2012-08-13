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
package edu.yu.einstein.genplay.core.multiGenome.operation.BED;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;


/**
 * This class help for the export of VCF track to a new BED file and for a specific allele.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class AlleleSettingsBedExport extends AlleleSettingsBed {

	private final File 			bedFile;	// The BED file to export the data.
	private FileWriter 			fw;			// The file writer.
	private BufferedWriter 		data;		// The main data stream.


	/**
	 * Constructor of {@link AlleleSettingsBedExport}
	 * @param path				the path of the new BED file
	 * @param allele			the allele to use
	 * @param coordinateSystem	the coordinate system to use to export position
	 */
	protected AlleleSettingsBedExport (String path, AlleleType allele, CoordinateSystemType coordinateSystem) {
		super(allele, coordinateSystem);
		bedFile = getFile(path);
	}


	/**
	 * Create the path according to the allele
	 * @param path		the path of the file
	 * @param allele	the allele to export
	 * @return the file
	 */
	private File getFile (String path) {
		int length = path.length() - 4;
		String newPath = path.substring(0, length);
		newPath += "_";
		if (coordinateSystem == CoordinateSystemType.CURRENT_GENOME) {
			newPath +=  allele.toString().toLowerCase();
		} else if (coordinateSystem == CoordinateSystemType.METAGENOME) {
			newPath += "meta_genome";
		} else if (coordinateSystem == CoordinateSystemType.REFERENCE) {
			newPath += "reference_genome";
		}
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
	 * ON BED FORMAT: Some variation cannot be written depending on the coordinate system.
	 * Start position of an insertion does not have a match on the reference genome.
	 * Start position of an deletion does not have a match on the current genome.
	 * @return true if the current variation can be written, false otherwise.
	 */
	public boolean isWritable () {
		if ((coordinateSystem == CoordinateSystemType.REFERENCE) && (currentLength > 0)) { // Cannot write an insertion from a genome to the reference genome
			return false;
		} else if (coordinateSystem == CoordinateSystemType.CURRENT_GENOME) {	// when the coordinate system if the one of the current genome
			if (currentLength < 0) {											// cannot export the deletion
				return false;
			} else if ((currentLength > 0) && isReference()) {					// cannot export blank of synchronization (same issue as deletion)
				return false;
			}
		}
		return true;
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
	 * @return the bedFile
	 */
	public File getBedFile() {
		return bedFile;
	}

}
