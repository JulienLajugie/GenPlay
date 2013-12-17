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
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.sf.samtools.util.BlockCompressedInputStream;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGOBGZIPUncompression {

	private final File bgzFile;	// the bgzip file
	private File file;		// the uncompressed file


	/**
	 * Constructor of {@link MGOBGZIPUncompression}
	 * @param file the BGZIP file to uncompress
	 */
	public MGOBGZIPUncompression(File file) {
		bgzFile = file;
	}


	/**
	 * Uncompress a BGZIP file into a VCf file.
	 * @return true if the operation has been done correctly. False otherwise.
	 * @throws IOException
	 */
	public Boolean compute() throws IOException {
		if (Utils.getExtension(bgzFile).equals("gz")) {
			// Open the BGZIP input stream
			BlockCompressedInputStream bgzipBCIS = new BlockCompressedInputStream(bgzFile);
			InputStreamReader bgzipISR = new InputStreamReader(bgzipBCIS);
			BufferedReader bgzipBR = new BufferedReader(bgzipISR);

			// Get the file
			file = getFile(bgzFile);

			// Open the file output stream
			FileOutputStream fos = new FileOutputStream(file);
			DataOutputStream on = new DataOutputStream(fos);
			OutputStreamWriter osr = new OutputStreamWriter(on);
			BufferedWriter bw = new BufferedWriter(osr);

			String newLine = "\n";
			String gzLine;
			while ((gzLine = bgzipBR.readLine()) != null) {
				bw.write(gzLine);
				bw.write(newLine);
			}

			// Close the VCF output stream
			bw.close();
			osr.close();
			on.close();
			fos.close();

			// Close the BGZIP input stream
			bgzipBR.close();
			bgzipISR.close();
			bgzipBCIS.close();

			return true;
		} else {
			return false;
		}
	}


	/**
	 * @param file a compressed file
	 * @return the associated file
	 */
	private File getFile (File file) {
		File newFile = null;
		String filePath = file.getPath();
		String newPath = filePath.substring(0, filePath.length() - 3);
		newFile = new File(newPath);
		return newFile;
	}

}
