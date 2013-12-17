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
package edu.yu.einstein.genplay.core.SAM;

import java.io.File;
import java.io.Serializable;

import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileReader.ValidationStringency;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SAMFile implements Serializable {

	/** Generated default serial version ID */
	private static final long serialVersionUID = -1670659824153832661L;

	private SAMFileReader reader;
	private SAMFileHeader header;

	/**
	 * Constructor of {@link SAMFile}
	 * @param file a SAM/BAM file
	 */
	public SAMFile (File file) {
		reader = null;
		header = null;
		if (file.exists()) {
			File indexFile = findIndexFile(file);
			if (indexFile.exists()) {
				reader = new SAMFileReader(file, indexFile);
				reader.setValidationStringency(ValidationStringency.SILENT);
				header = reader.getFileHeader();
			} else {
				System.err.println("SAMFile: the indexed file does not exist.");
			}
		} else {
			System.err.println("SAMFile: the file does not exist.");
		}
	}


	/**
	 * Look for the indexed BAM file
	 * @param file a BAM file
	 * @return the index file
	 */
	private static File findIndexFile(File file) {
		final String bamPath = file.getAbsolutePath();

		// Regular convention: .bam.bai
		String indexPath = bamPath + ".bai";
		File indexFile = new File(indexPath);
		if (!indexFile.exists()) {

			// Picard convention: .bai
			final String bamExtension = ".bam";
			if (bamPath.toLowerCase().endsWith(bamExtension)) {
				indexPath = bamPath.substring(0, bamPath.length() - bamExtension.length()) + ".bai";
				indexFile = new File(indexPath);
			}
		}

		return indexFile;
	}


	/**
	 * @return the reader
	 */
	public SAMFileReader getReader() {
		return reader;
	}


	/**
	 * @return the header
	 */
	public SAMFileHeader getHeader() {
		return header;
	}


	/**
	 * Print the header information
	 */
	public void printHeader () {
		if (header != null) {
			//header.
		}
	}
}
