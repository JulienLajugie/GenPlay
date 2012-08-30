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
package edu.yu.einstein.genplay.core.multiGenome.operation.convert;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sf.samtools.util.BlockCompressedOutputStream;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGOBGZIPCompression {

	private final File vcfFile;	// the vcf file
	private File bgzFile;	// the bgzip file


	/**
	 * Constructor of {@link MGOBGZIPCompression}
	 * @param file the VCF file to compress
	 */
	public MGOBGZIPCompression(File file) {
		vcfFile = file;
	}


	/**
	 * Compress a VCf file into a BGZIP file.
	 * @return true if the operation has been done correctly. False otherwise.
	 * @throws IOException
	 */
	public Boolean compute() throws IOException {
		if (Utils.getExtension(vcfFile).equals("vcf")) {
			// Open the VCF input stream
			FileInputStream vcfFIS = new FileInputStream(vcfFile);
			DataInputStream vcfIN = new DataInputStream(vcfFIS);
			InputStreamReader vcfISR = new InputStreamReader(vcfIN);
			BufferedReader vcfBR = new BufferedReader(vcfISR);

			// Get the BGZIP file
			bgzFile = new File(vcfFile.getPath() + ".gz");

			// Open the BGZIP output stream
			BlockCompressedOutputStream bgzipBCOS = new BlockCompressedOutputStream(bgzFile);

			String newLine = "\n";
			String vcfLine;
			while ((vcfLine = vcfBR.readLine()) != null) {
				bgzipBCOS.write(vcfLine.getBytes());
				bgzipBCOS.write(newLine.getBytes());
			}

			// Close the BGZIP output stream
			bgzipBCOS.close();

			// Close the VCF input stream
			vcfBR.close();
			vcfISR.close();
			vcfIN.close();
			vcfFIS.close();

			return true;
		} else {
			return false;
		}
	}


	/**
	 * @return the bgzFile
	 */
	public File getBgzFile() {
		return bgzFile;
	}

}
