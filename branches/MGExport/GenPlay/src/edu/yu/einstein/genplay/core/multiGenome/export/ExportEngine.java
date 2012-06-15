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

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.yu.einstein.genplay.core.comparator.ListComparator;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public abstract class ExportEngine implements ExportEngineInterface {

	
	
	protected ObjectOutputStream data;
	protected String header;
	
	protected Map<String, List<VCFFile>> 		fileMap;
	protected Map<String, List<VariantType>> 	variationMap;
	protected List<VCFFilter> 					filterList;
	protected String 							path;


	@Override
	public void process() throws Exception {
		String errors = getParameterErrors();
		if (errors == null) {
			if (canStart()) {
				File dataFile = new File(getDataPath());
				
				createDataFile(dataFile);
				
				createHeader();
				
				writeFinalFile(dataFile);
				
				dataFile.delete();
			}
		} else {
			System.err.println("ExportEngine.process()\n" + errors);
		}
	}


	/**
	 * @return true if the export can start, false otherwise
	 */
	protected abstract boolean canStart ();


	/**
	 * Performs the export action
	 * @throws IOException 
	 */
	protected abstract void performExport() throws IOException;
	
	
	/**
	 * Creates the header of the new VCF file
	 * @throws IOException
	 */
	protected abstract void createHeader() throws IOException;

	
	/**
	 * Initializes the data output stream
	 * @throws Exception
	 */
	private void createDataFile (File dataFile) throws Exception {
		FileOutputStream fos = new FileOutputStream(dataFile);
		GZIPOutputStream gz = new GZIPOutputStream(fos);
		data = new ObjectOutputStream(gz);
		
		performExport();
		
		data.close();
		gz.close();
		fos.close();
	}
	
	
	/**
	 * 
	 * @return the path of the temporary data file
	 */
	private String getDataPath () {
		return (path.substring(0, path.length() - 4) + "_tmp.vcf");
	}
	
	
	/**
	 * Merges the data file and the header into a new file
	 * @param dataFile		the file containing the data
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void writeFinalFile (File dataFile) throws IOException, ClassNotFoundException {
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
	 * Checks every parameter and create an full error message if any of them is not valid.
	 * @return the error message, null if no error.
	 */
	private String getParameterErrors () {
		String errors = null;

		if (fileMap == null) {
			errors = addErrorMessage(errors, "No file map has been declared.");
		} else if (fileMap.size() == 0) {
			errors = addErrorMessage(errors, "The file map is empty.");
		}

		if (variationMap == null) {
			errors = addErrorMessage(errors, "No variation map has been declared.");
		} else if (variationMap.size() == 0) {
			errors = addErrorMessage(errors, "The variation map is empty.");
		}

		if (path == null) {
			errors = addErrorMessage(errors, "The path of the new VCF file has been declared.");
		} else {
			File file = new File(path);
			try {
				file.createNewFile();
			} catch (IOException e) {
				errors = addErrorMessage(errors, "The file could not created, the path may not be valid: " + path + ".");
				e.printStackTrace();
			}
			if (!file.isFile()) {
				errors = addErrorMessage(errors, "The path of the new VCF file is not a valid file: " + path + ".");
			}
		}

		if (fileMap != null && variationMap != null) {
			ListComparator<String> comparator = new ListComparator<String>();
			List<String> genomesFileMap = new ArrayList<String>(fileMap.keySet());
			List<String> genomesVariationMap = new ArrayList<String>(variationMap.keySet());

			int result = comparator.compare(genomesFileMap, genomesVariationMap);
			if (result != 0) {
				errors = addErrorMessage(errors, "Genome names lists are not equal:");
				errors = addErrorMessage(errors, comparator.getErrorCode());
			}
		}

		return errors;
	}


	/**
	 * Add a message to a full error message
	 * @param errors	the full error message
	 * @param message	the message
	 * @return			the full error message containing the new message
	 */
	private String addErrorMessage (String errors, String message) {
		if (errors == null) {
			errors = message;
		} else if (errors.length() > 0) {
			errors += "\n" + message;
		} else {
			errors += message;
		}
		return errors;
	}


	/**
	 * @return the sorted list of genome names
	 */
	protected List<String> getGenomeList () {
		List<String> result = new ArrayList<String>(variationMap.keySet());
		Collections.sort(result);
		return result;
	}


	/**
	 * @return the list of files
	 */
	protected List<VCFFile> getFileList () {
		List<VCFFile> fileList = new ArrayList<VCFFile>();
		for (String genome: fileMap.keySet()) {
			List<VCFFile> projectList = fileMap.get(genome);
			for (VCFFile file: projectList) {
				if (!fileList.contains(file)) {
					fileList.add(file);
				}
			}
		}
		return fileList;
	}


	@Override
	public void setFileMap(Map<String, List<VCFFile>> fileMap) {
		this.fileMap = fileMap;
	}


	@Override
	public void setVariationMap(Map<String, List<VariantType>> variationMap) {
		this.variationMap = variationMap;
	}


	@Override
	public void setFilterList(List<VCFFilter> filterList) {
		this.filterList = filterList;
	}


	@Override
	public void setPath(String path) {
		this.path = path;
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
		if (filterList == null || filterList.size() == 0) {
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
