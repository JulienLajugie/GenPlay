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
package edu.yu.einstein.genplay.core.multiGenome.export.VCFExport;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.sf.samtools.util.BlockCompressedOutputStream;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.export.ExportEngine;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public abstract class ExportVCFEngine extends ExportEngine {


	/** Option to export the track as VCF file (not compressed) */
	public static boolean EXPORT_AS_VCF_FILE = true;
	/** Option to export the track as a compressed VCF file (BGZIP) */
	public static boolean EXPORT_AS_BGZIP_FILE = false;

	protected ObjectOutputStream 				data;			// the temporary stream for data
	protected String 							header;			// the new VCF header
	protected ExportHeaderHandler				headerHandler;	// handler for the new header


	@Override
	public void process() throws Exception {
		createHeaderHandler();

		File dataFile = new File(getDataPath());
		createDataFile(dataFile);

		createHeader();

		writeFile(dataFile);

		dataFile.delete();
	}


	/**
	 * Creates the header of the new VCF file
	 * @throws IOException
	 */
	protected abstract void createHeader() throws IOException;


	/**
	 * Creates and initializes the handler for the header
	 */
	private void createHeaderHandler () {
		headerHandler = new ExportHeaderHandler();
		headerHandler.initializeHeadersMap(getFileList());
	}


	/**
	 * Initializes the data output stream
	 * @throws Exception
	 */
	private void createDataFile (File dataFile) throws Exception {
		FileOutputStream fos = new FileOutputStream(dataFile);
		GZIPOutputStream gz = new GZIPOutputStream(fos);
		data = new ObjectOutputStream(gz);

		fileHandler.compute();

		data.close();
		gz.close();
		fos.close();
	}


	/**
	 * 
	 * @return the path of the temporary data file
	 */
	private String getDataPath () {
		return (path.substring(0, path.length() - 4) + "_tmp.vcf.gz");
	}


	/**
	 * Writes required files.
	 * Merges the data file and the header into a new file.
	 * @param dataFile	the file containing the data
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void writeFile (File dataFile) throws IOException, ClassNotFoundException {
		if (EXPORT_AS_VCF_FILE) {
			writeVCFFinalFile(dataFile);
		}

		if (EXPORT_AS_BGZIP_FILE) {
			writeCompressedFinalFile(dataFile);
		}
	}


	/**
	 * Merges the data file and the header into a new file
	 * @param dataFile		the file containing the data
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void writeVCFFinalFile (File dataFile) throws IOException, ClassNotFoundException {
		// Initializes the output file writer
		File outputFile = new File(path);
		FileWriter fw = new FileWriter(outputFile);
		BufferedWriter out = new BufferedWriter(fw);

		// Writes the header
		header += "\n";
		out.write(header);

		// Initializes the data file reader
		FileInputStream fis = new FileInputStream(dataFile);
		GZIPInputStream gz = new GZIPInputStream(fis);
		ObjectInputStream ois = new ObjectInputStream(gz);

		// Writes the data file into the output file
		boolean endOfFile = false;
		while (!endOfFile) {
			String line = null;
			try {
				line = (String) ois.readObject();
				out.write(line);
			} catch (EOFException e) {
				endOfFile = true;
			}
		}

		// Closes the data file reader
		ois.close();
		gz.close();
		fis.close();

		// Closes the output file writer
		out.close();
		fw.close();
	}


	/**
	 * Merges the data file and the header into a new file (compressed as bgzip)
	 * @param dataFile		the file containing the data
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void writeCompressedFinalFile (File dataFile) throws IOException, ClassNotFoundException {
		// Initializes the output file writer
		File outputFile = new File(path);
		BlockCompressedOutputStream output = new BlockCompressedOutputStream(outputFile);

		// Writes the header
		header += "\n";
		output.write(header.getBytes());

		// Initializes the data file reader
		FileInputStream fis = new FileInputStream(dataFile);
		GZIPInputStream gz = new GZIPInputStream(fis);
		ObjectInputStream ois = new ObjectInputStream(gz);

		// Writes the data file into the output file
		boolean endOfFile = false;
		while (!endOfFile) {
			String line = null;
			try {
				line = (String) ois.readObject();
				output.write(line.getBytes());
			} catch (EOFException e) {
				endOfFile = true;
			}
		}

		// Closes the data file reader
		ois.close();
		gz.close();
		fis.close();

		// Closes the output file writer
		output.close();
	}


	///////////////////////////////////////////////////////////////////////////////////// DEVELOPMENT

	protected void showInformation () {
		System.out.println("===== ExportEngine.showInformation()");
		System.out.println("Path: " + path);
		showFileMap();
		showVariationMap();
		showFilterList();
		System.out.println("=====");
	}

	private void showFileMap () {
		String info = "List of VCF files:\n";
		for (String genome: fileMap.keySet()) {
			info += "Genome: " + genome + "\n";
			info += "Files: ";
			List<VCFFile> list = fileMap.get(genome);
			for (int i = 0; i < list.size(); i++) {
				info += list.get(i).getFile();
				if (i < (list.size() - 1)) {
					info += "; ";
				}
			}
		}
		System.out.println(info);
	}

	private void showVariationMap () {
		String info = "List of variation:\n";
		for (String genome: variationMap.keySet()) {
			info += "Genome: " + genome + "\n";
			info += "Variations: ";
			List<VariantType> list = variationMap.get(genome);
			for (int i = 0; i < list.size(); i++) {
				info += list.get(i).toString();
				if (i < (list.size() - 1)) {
					info += "; ";
				}
			}
		}
		System.out.println(info);
	}

	private void showFilterList () {
		String info = "List of filter:";
		if ((filterList == null) || (filterList.size() == 0)) {
			info += " no filter";
		} else {
			info += "\n";
			for (int i = 0; i < filterList.size(); i++) {
				info += filterList.get(i).getFilter().toStringForDisplay();
				if (i < (filterList.size() - 1)) {
					info += "\n";
				}
			}
		}
		System.out.println(info);
	}
	/////////////////////////////////////////////////////////////////////////////////////

}
