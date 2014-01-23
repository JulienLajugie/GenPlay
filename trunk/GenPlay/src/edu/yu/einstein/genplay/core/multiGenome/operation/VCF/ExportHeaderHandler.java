/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.operation.VCF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.util.Utils;


/**
 * This class provides help to handle the header and to build it for export action.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ExportHeaderHandler {

	private Map<VCFFile, List<VCFHeaderType>> altHeadersMap;	// Map between files and their list of ALT headers
	private Map<VCFFile, List<VCFHeaderType>> filterHeadersMap;	// Map between files and their list of FILTER headers
	private Map<VCFFile, List<VCFHeaderType>> infoHeadersMap;	// Map between files and their list of INFO headers
	private Map<VCFFile, List<VCFHeaderType>> formatHeadersMap;	// Map between files and their list of FORMAT headers


	/**
	 * Constructor of {@link ExportHeaderHandler}
	 */
	protected ExportHeaderHandler () {
	}


	/**
	 * Set the map between files and their list of headers using a list of files.
	 * Must be called at the beginning, existing data will be removed.
	 * @param files the list of files
	 */
	protected void initializeHeadersMap (List<VCFFile> files) {
		altHeadersMap = new HashMap<VCFFile, List<VCFHeaderType>>();
		filterHeadersMap = new HashMap<VCFFile, List<VCFHeaderType>>();
		infoHeadersMap = new HashMap<VCFFile, List<VCFHeaderType>>();
		formatHeadersMap = new HashMap<VCFFile, List<VCFHeaderType>>();
		for (VCFFile file: files) {
			altHeadersMap.put(file, new ArrayList<VCFHeaderType>());
			filterHeadersMap.put(file, new ArrayList<VCFHeaderType>());
			infoHeadersMap.put(file, new ArrayList<VCFHeaderType>());
			formatHeadersMap.put(file, new ArrayList<VCFHeaderType>());
		}
	}


	/**
	 * Process line information in order to update data for the new header
	 * @param file		the file related to the information fields
	 * @param ALT		the ALT information field
	 * @param FILTER	the FILTER information field
	 * @param INFO		the INFO information field
	 * @param FORMAT	the FORMAT information field
	 */
	protected void processLine (VCFFile file, String ALT, String FILTER, String INFO, String FORMAT) {
		processALT(file, ALT);
		processFILTER(file, FILTER);
		processINFO(file, INFO);
		processFORMAT(file, FORMAT);
	}


	/**
	 * Retrieves ALT information in order to update data for the new header
	 * @param file 	the file related to the information field
	 * @param ALT	the information field
	 */
	private void processALT (VCFFile file, String ALT) {
		String[] alternatives = Utils.split(ALT, ',');
		for (String alternative: alternatives) {
			if (alternative.charAt(0) == '<') {
				List<VCFHeaderType> altHeader = file.getHeader().getAltHeader();
				for (VCFHeaderType header: altHeader) {
					if (header.getId().equals(alternative.substring(1, alternative.length() - 1))) {
						if (!altHeadersMap.get(file).contains(header)) {
							altHeadersMap.get(file).add(header);
						}
					}
				}
			}
		}
	}


	/**
	 * Retrieves FILTER information in order to update data for the new header
	 * @param file 	the file related to the information field
	 * @param FILTER	the information field
	 */
	private void processFILTER (VCFFile file, String FILTER) {
	}


	/**
	 * Retrieves INFO information in order to update data for the new header
	 * @param file 	the file related to the information field
	 * @param INFO	the information field
	 */
	private void processINFO (VCFFile file, String INFO) {
		String[] information = Utils.split(INFO, ';');
		for (String info: information) {
			String id = Utils.split(info, '=')[0];
			List<VCFHeaderAdvancedType> infoHeader = file.getHeader().getInfoHeader();
			for (VCFHeaderAdvancedType header: infoHeader) {
				if (header.getId().equals(id)) {
					if (!infoHeadersMap.get(file).contains(header)) {
						infoHeadersMap.get(file).add(header);
					}
				}
			}
		}
	}


	/**
	 * Retrieves FORMAT information in order to update data for the new header
	 * @param file 	the file related to the information field
	 * @param FORMAT	the information field
	 */
	private void processFORMAT (VCFFile file, String FORMAT) {
		String[] format = Utils.split(FORMAT, ':');
		for (String id: format) {
			List<VCFHeaderAdvancedType> formatHeader = file.getHeader().getFormatHeader();
			for (VCFHeaderAdvancedType header: formatHeader) {
				if (header.getId().equals(id)) {
					if (!formatHeadersMap.get(file).contains(header)) {
						formatHeadersMap.get(file).add(header);
					}
				}
			}
		}
	}


	/**
	 * @return the header containing the used fields
	 */
	protected String getFieldHeader () {
		String header = "";

		// Gets the headers
		String alt = getHeader(altHeadersMap);
		String filter = getHeader(filterHeadersMap);
		String info = getHeader(infoHeadersMap);
		String format = getHeader(formatHeadersMap);

		// Adds the ALT header
		if (!alt.isEmpty()) {
			header += alt;
		}

		// Adds the FILTER header
		if (!filter.isEmpty()) {
			if (!header.isEmpty()) {
				header += "\n";
			}
			header += filter;
		}

		// Adds the INFO header
		if (!info.isEmpty()) {
			if (!header.isEmpty()) {
				header += "\n";
			}
			header += info;
		}

		// Adds the FORMAT header
		if (!format.isEmpty()) {
			if (!header.isEmpty()) {
				header += "\n";
			}
			header += format;
		}

		return header;
	}


	/**
	 * @param map map of headers
	 * @return the formatted field header part using the map
	 */
	private String getHeader (Map<VCFFile, List<VCFHeaderType>> map) {
		String result = "";

		for (List<VCFHeaderType> headers: map.values()) {
			for (int i = 0; i < headers.size(); i++) {
				String line = headers.get(i).getAsOriginalLine();
				if (!line.isEmpty()) {
					result += line;
					if (i < (headers.size() - 1)) {
						result += "\n";
					}
				}
			}
		}

		return result;
	}
}
