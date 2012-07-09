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


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class AlleleSettingsBedExport {

	private final File 			bedFile;
	private FileWriter 			fw;
	private BufferedWriter 		data;
	private final AlleleType 	allele;
	private int					offset;


	/**
	 * Constructor of {@link AlleleSettingsBedExport}
	 * @param path
	 * @param allele
	 */
	protected AlleleSettingsBedExport (String path, AlleleType allele) {
		bedFile = getFile(path, allele);
		this.allele = allele;
		offset = 0;
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
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}


	/**
	 * @param offset the offset to set
	 */
	public void addOffset(int offset) {
		this.offset += offset;
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

}
