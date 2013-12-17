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
package edu.yu.einstein.genplay.core.multiGenome.operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.comparator.ListComparator;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.core.multiGenome.operation.VCF.MGOApplyVCFGenotype;
import edu.yu.einstein.genplay.core.multiGenome.operation.fileScanner.FileScannerInterface;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;

/**
 * The update engine is made to create a new VCF file based on a file to update, using data from a current VCF track.
 * The first example in GenPlay is the {@link MGOApplyVCFGenotype}.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public abstract class BasicEngine {

	protected FileScannerInterface				fileScanner;	// The file scanner.

	// Input parameters: data to use as model
	protected Map<String, List<VCFFile>> 		fileMap;		// The map between genome names and their related files.
	protected Map<String, List<VariantType>> 	variationMap;	// The map between genome names and their required variations.
	protected List<MGFilter> 					filterList;		// The list of filters.
	protected boolean includeReferences;
	protected boolean includeNoCall;


	/**
	 * @param fileMap		map between genome names and their related files
	 * @param variationMap	map between genome names and their required variations
	 * @param filterList	list of filters
	 * @param includeReferences include the references (0)
	 * @param includeNoCall 	include the no call (.)
	 */
	public void initializeEngine (Map<String, List<VCFFile>> fileMap, Map<String, List<VariantType>> variationMap, List<MGFilter> filterList, boolean includeReferences, boolean includeNoCall) {
		this.fileMap = fileMap;
		this.variationMap = variationMap;
		this.filterList = filterList;
		this.includeReferences = includeReferences;
		this.includeNoCall = includeNoCall;
	}


	/**
	 * Export the data
	 * @throws Exception
	 */
	public void compute () throws Exception {
		String errors = getParameterErrors();
		if (errors == null) {
			if (canStart()) {
				process();
			}
		} else {
			System.err.println("BasicEngine.process()\n" + errors);
		}
	}


	/**
	 * Checks every parameter and create an full error message if any of them is not valid.
	 * @return the error message, null if no error.
	 */
	protected String getParameterErrors () {
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

		if ((fileMap != null) && (variationMap != null)) {
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
	protected String addErrorMessage (String errors, String message) {
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
	 * @return true if the export is from only one file, false if several files are involved
	 */
	public boolean isSingleExport () {
		if (getFileList().size() == 1) {
			return true;
		}
		return false;
	}


	/**
	 * @return the sorted list of genome names
	 */
	public List<String> getGenomeList () {
		List<String> result = new ArrayList<String>(variationMap.keySet());
		Collections.sort(result);
		return result;
	}


	/**
	 * @return the list of files
	 */
	public List<VCFFile> getFileList () {
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


	/**
	 * @return true if the export can start, false otherwise
	 * @throws Exception
	 */
	protected abstract boolean canStart () throws Exception;


	/**
	 * Processes the export
	 * @throws Exception
	 */
	protected abstract void process () throws Exception;


	/**
	 * Processes a line
	 * @param src the VCF file to use as reference for phasing
	 * @param dest the VCF file to apply the phasing
	 * @throws IOException
	 */
	public abstract void processLine (VCFLine src, VCFLine dest) throws IOException;


	/**
	 * Processes a line
	 * @param fileAlgorithm the file reading algorithm
	 * @throws IOException
	 */
	public abstract void processLine (FileScannerInterface fileAlgorithm) throws IOException;


	/**
	 * @return the fileMap
	 */
	public Map<String, List<VCFFile>> getFileMap() {
		return fileMap;
	}


	/**
	 * @return the variationMap
	 */
	public Map<String, List<VariantType>> getVariationMap() {
		return variationMap;
	}


	/**
	 * @return the filterList
	 */
	public List<MGFilter> getFilterList() {
		return filterList;
	}


	/**
	 * @return the includeReferences
	 */
	public boolean isIncludeReferences() {
		return includeReferences;
	}


	/**
	 * @return the includeNoCall
	 */
	public boolean isIncludeNoCall() {
		return includeNoCall;
	}


	/**
	 * @return a description of the genomes and their variants
	 */
	protected String getVariantDescription () {
		String description = "";

		int genomeIndex = 0;
		List<String> genomeNames = new ArrayList<String>(variationMap.keySet());
		for (String genomeName: genomeNames) {
			description += genomeName + " (";
			List<VariantType> list = variationMap.get(genomeName);
			for (int i = 0; i < list.size(); i++) {
				description += list.get(i);
				if (i < (list.size() - 1)) {
					description += ", ";
				}
			}
			description += ")";
			if (genomeIndex < (genomeNames.size() - 1)) {
				description += " ";
			}
			genomeIndex++;
		}

		return description;
	}


}
