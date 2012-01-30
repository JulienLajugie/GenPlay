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
package edu.yu.einstein.genplay.core.multiGenome.VCF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAltType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderFilterType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderFormatType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderInfoType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.tabixAPI.Iterator;
import edu.yu.einstein.genplay.core.multiGenome.tabixAPI.TabixReader;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;


/**
 * This class handles VCF files.
 * It indexes information to perform fast queries.
 * It also get VCF header information.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFReader implements Serializable {

	private static final long serialVersionUID = 7316097355767936880L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version	
	
	private transient	TabixReader 			vcfParser;		// Tabix object for the VCF file (Tabix Java API)
	private File 								file;			// Path of the VCF file
	
	private Map<String, String> 				headerInfo;			// Header main information
	private	Map<String, Class<?>>				fieldType;			// Association between field type and java class
	private Map<String, List<VariantType>>		variantTypeList;	// List of the different variant type contained in the VCF file and sorted by genome raw name
	
	private	List<String>						columnNames;		// All column header names
	private	List<String>						fixedColumn;		// Fixed header names included in the VCF file
	private	List<String>						genomeNames;		// Dynamic header names included in the VCF file (raw genome names)
	
	private List<VCFHeaderType> 				altHeader;			// Header for the ALT field
	private List<VCFHeaderType> 				filterHeader;		// Header for the FILTER field
	private List<VCFHeaderAdvancedType> 		infoHeader;			// Header for the INFO field
	private List<VCFHeaderAdvancedType> 		formatHeader;		// Header for the FORMAT field


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(file);
		out.writeObject(headerInfo);
		out.writeObject(columnNames);
		out.writeObject(fixedColumn);
		out.writeObject(genomeNames);
		out.writeObject(fieldType);
		out.writeObject(altHeader);
		out.writeObject(filterHeader);
		out.writeObject(infoHeader);
		out.writeObject(formatHeader);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		file = (File) in.readObject();
		headerInfo= (Map<String, String>) in.readObject();
		columnNames = (List<String>) in.readObject();
		fixedColumn = (List<String>) in.readObject();
		genomeNames = (List<String>) in.readObject();
		fieldType = (Map<String, Class<?>>) in.readObject();
		altHeader = (List<VCFHeaderType>) in.readObject();
		filterHeader = (List<VCFHeaderType>) in.readObject();
		infoHeader = (ArrayList<VCFHeaderAdvancedType>) in.readObject();
		formatHeader = (ArrayList<VCFHeaderAdvancedType>) in.readObject();
		indexVCFFile(); // recreate the tabix reader
	}


	/**
	 * Constructor of {@link VCFReader}
	 * @param file		the VCF file
	 * @throws IOException
	 */
	public VCFReader (File file) throws IOException {
		this.file = file;
		variantTypeList = new HashMap<String, List<VariantType>>();
		initFixedColumnList();
		initFieldType();
		indexVCFFile();
		processHeader();
	}


	/**
	 * Initializes column header list.
	 */
	private void initFixedColumnList () {
		fixedColumn = new ArrayList<String>();
		fixedColumn.add("CHROM");
		fixedColumn.add("POS");
		fixedColumn.add("ID");
		fixedColumn.add("REF");
		fixedColumn.add("ALT");
		fixedColumn.add("QUAL");
		fixedColumn.add("FILTER");
		fixedColumn.add("INFO");
		fixedColumn.add("FORMAT");
	}


	/**
	 * Initializes field/java class association.
	 */
	private void initFieldType () {
		fieldType = new HashMap<String, Class<?>>();
		fieldType.put("Integer", Integer.class);
		fieldType.put("Float", Float.class);
		fieldType.put("Flag", Boolean.class);
		fieldType.put("Character", char.class);
		fieldType.put("String", String.class);
	}


	/**
	 * This method indexes the VCF file using the Tabix Java API.
	 * @throws IOException
	 */
	private void indexVCFFile () throws IOException {
		if (!isVCFIndexed ()) {
			this.vcfParser = new TabixReader(file.getPath());
		}
	}


	/**
	 * This method checks if the VCF has been indexed.
	 * @return true if the VCF is indexed
	 */
	private boolean isVCFIndexed () {
		if (this.vcfParser != null) {
			return true;
		}
		return false;
	}


	/**
	 * This method reads and saves the vcf header information
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void processHeader () throws FileNotFoundException, IOException {
		boolean valid = true;
		headerInfo = new HashMap<String, String>();
		altHeader = new ArrayList<VCFHeaderType>();
		filterHeader = new ArrayList<VCFHeaderType>();
		infoHeader = new ArrayList<VCFHeaderAdvancedType>();
		formatHeader = new ArrayList<VCFHeaderAdvancedType>();
		while (valid) {
			String line = vcfParser.readLine();
			if (line.length() > 0) {
				if (line.substring(0, 2).equals("##")) {
					int equalChar = line.indexOf("=");
					String type = line.substring(2, equalChar);

					if (type.equals("INFO") || type.equals("ALT") || type.equals("FILTER") || type.equals("FORMAT")) {
						Map<String, String> info = parseVCFHeaderInfo(line.substring(equalChar + 2, line.length() - 1));

						VCFHeaderType headerType = null;

						if (type.equals("ALT")) {
							headerType = new VCFHeaderAltType();
							altHeader.add(headerType);
						} else if (type.equals("FILTER")) {
							headerType = new VCFHeaderFilterType();
							filterHeader.add(headerType);
						} else if (type.equals("INFO")) {
							headerType = new VCFHeaderInfoType();
							infoHeader.add((VCFHeaderAdvancedType) headerType);
						} else if (type.equals("FORMAT")) {
							headerType = new VCFHeaderFormatType();
							formatHeader.add((VCFHeaderAdvancedType) headerType);
						}

						headerType.setId(info.get("ID"));
						headerType.setDescription(info.get("Description"));
						if (headerType instanceof VCFHeaderAdvancedType) {
							((VCFHeaderAdvancedType) headerType).setNumber(info.get("Number"));
							((VCFHeaderAdvancedType) headerType).setType(fieldType.get(info.get("Type")));
						}
					} else {
						headerInfo.put(type, line.substring(equalChar + 1, line.length() - 1));
					}
				} else {
					valid = false;
					if (line.substring(0, 1).equals("#")) {
						columnNames = new ArrayList<String>();
						for (String name: line.substring(1, line.length()).split("[\t]")) {
							columnNames.add(name.trim());
						}
					}
				}
			}
		}
	}


	/**
	 * This method parses the content of attributes header information.
	 * @param line
	 * @return the parsed line
	 */
	private Map<String, String> parseVCFHeaderInfo (String line) {
		Map<String, String> info = new HashMap<String, String>();
		String details[] = line.split(",");
		String detail[];
		for (String s: details) {
			detail = s.split("=");
			if (detail.length > 1) {
				if (detail[0].equals("Description")) {
					String element;
					if (detail.length == 2) {
						element = detail[1].substring(1, detail[1].length() - 2);
					} else {
						element = "";
						for (int i = 1; i < detail.length; i++) {
							if (i == 1) {
								element += detail[i].substring(1) + "=";
							} else if (i == (detail.length - 1)) {
								element += detail[i].substring(0, detail[i].length() - 2);
							} else {
								element += detail[i] + "=";
							}
						}
					}
					info.put(detail[0], element);
				} else {
					info.put(detail[0], detail[1]);
				}
			}
		}
		return info;
	}
	

	/**
	 * Shows the main header information
	 */
	public void showHeaderInfo () {
		System.out.println("===== Header information");
		for (String key: headerInfo.keySet()) {
			System.out.println(key + ": " + headerInfo.get(key));
		}
	}


	/**
	 * Shows the column names
	 */
	public void showColumnNames () {
		System.out.println("===== Column names");
		String line = "";
		for (String name: columnNames) {
			line = line + name + "\t";
		}
		System.out.println(line);
	}


	/**
	 * Performs a query on the indexed VCF file.
	 * @param chr		chromosome
	 * @param start		start position
	 * @param stop		stop position
	 * @return query 	results list
	 * @throws IOException
	 */
	public List<Map<String, Object>> query (String chr, int start, int stop) throws IOException {
		return query(chr, start, stop, columnNames);
	}


	/**
	 * Performs a query on the indexed VCF file.
	 * @param chr		chromosome
	 * @param start		start position
	 * @param stop		stop position
	 * @param fields	filter to select specific fields
	 * @return query 	results list
	 * @throws IOException
	 */
	public List<Map<String, Object>> query (String chr, int start, int stop, List<String> fields) throws IOException {
		Iterator iter = vcfParser.query(chr + ":" + start + "-" + stop);
		String line;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		while (iter != null && (line = iter.next()) != null){
			String info[] = line.split("[\t]");
			Map<String, Object> row = new HashMap<String, Object>();
			for (String columnName: columnNames) {
				if (fields.indexOf(columnName) != -1) {
					row.put(columnName, info[columnNames.indexOf(columnName)]);
				}
			}
			result.add(row);
		}
		return result;
	}


	/**
	 * Performs a query on the first chromosome of the indexed VCF file and return the 10 first results.
	 * @return query 	results list
	 * @throws IOException
	 */
	public List<Map<String, Object>> shortQuery () throws IOException {
		Iterator iter = vcfParser.shortQuery(0);
		List<String> fields = new ArrayList<String>();
		fields.add("REF");
		fields.add("ALT");
		String line;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int cpt = 0;
		while (iter != null && (line = iter.next()) != null && cpt < 10){
			String info[] = line.split("[\t]");
			Map<String, Object> row = new HashMap<String, Object>();
			for (String columnName: columnNames) {
				if (fields.indexOf(columnName) != -1) {
					row.put(columnName, info[columnNames.indexOf(columnName)]);
				}
			}
			result.add(row);
			cpt++;
		}
		return result;
	}


	/**
	 * Shows the result of a query.
	 * A query can have more than one result.
	 * @param result the list of result
	 */
	public static void showQueryResults (List<Map<String, Object>> result) {
		System.out.println("===== Query results");
		String line;
		if (result.size() == 0) {
			System.out.println("no result");
		} else {
			for (int i = 0; i < result.size(); i++) {
				line = "Result " + (i + 1) + ": ";
				for (String name: result.get(i).keySet()) {
					line = line + name + " = " + result.get(i).get(name) + ", ";
				}
				System.out.println(line);
			}
		}
	}


	/**
	 * Shows the result of several query.
	 * @param result the list of result
	 */
	public static void showQueriesFileResults (List<List<Map<String, Object>>> result) {
		System.out.println("===== Queries file results");
		for (List<Map<String, Object>> queryResults: result) {
			showQueryResults(queryResults);
		}
	}


	/**
	 * @return the columnNames
	 */
	public List<String> getColumnNames() {
		return columnNames;
	}


	/**
	 * @return the columnNames
	 */
	public List<String> getRawGenomesNames() {
		if (genomeNames == null) {
			List<String> list = new ArrayList<String>();
			for (String s: columnNames) {
				if (!fixedColumn.contains(s)) {
					list.add(s);
				}
			}
			Collections.sort(list);
			genomeNames = list;
		}
		return genomeNames;
	}


	/**
	 * Gets the value according to the INFO field and a specific field
	 * @param info	the INFO string
	 * @param field	the specific field
	 * @return		the value of the specific field of the INFO field
	 */
	public Object getInfoValues (String info, String field) {
		Object result = null;
		int indexInList = getIndex(infoHeader, field);
		if (indexInList != -1) {
			int indexInString = info.indexOf(field);
			if (indexInString != -1) {
				Class<?> type = infoHeader.get(indexInList).getType();
				if (type == Boolean.class) {
					result = true;
				} else {
					int start = indexInString + field.length() + 1;
					int stop = info.indexOf(";", start);
					if (stop == -1) {
						stop = info.length();
					}
					String value = info.substring(start, stop);
					if (type == Integer.class) {
						result = Integer.parseInt(value);
					} else if (type == Float.class) {
						result = Float.parseFloat(value);
					} else if (type == char.class) {
						result = value.charAt(0);
					} else if (type == String.class) {
						result = value;
					}
				}
			}
		}
		return result;
	}


	/**
	 * Gets the value of the FORMAT field and a specific field
	 * @param value	the FORMAT string
	 * @param field the specific field
	 * @return		the value of the specific field of the FORMAT field
	 */
	public Object getFormatValue (String value, String field) {
		Object result = null;
		int indexInList = getIndex(formatHeader, field);
		if (indexInList != -1) {
			Class<?> type = formatHeader.get(indexInList).getType();
			if (type == Integer.class) {
				try {
					result = Integer.parseInt(value);
				} catch (Exception e) {
					result = value;
				}

			} else if (type == Float.class) {
				result = Float.parseFloat(value);
			} else if (type == char.class) {
				result = value.charAt(0);
			} else if (type == String.class) {
				result = value;
			}
		}
		return result;
	}


	/**
	 * Gets the index of a specific ID field in a advanced type header list
	 * @param list	the advanced type header list
	 * @param id	the specific ID field
	 * @return		the index
	 */
	private int getIndex (List<VCFHeaderAdvancedType> list, String id) {		
		boolean found = false;
		int index = 0;

		while (!found && index < list.size()) {
			if (id.equals(list.get(index).getId())) {
				found = true;
			} else {
				index++;
			}
		}

		if (found) {
			return index;
		} else {
			return -1;
		}
	}
	

	/**
	 * @return the fixedColumn
	 */
	public List<String> getFixedColumn() {
		List<String> list = new ArrayList<String>(fixedColumn);
		return list;
	}


	/**
	 * @return the headerInfo
	 */
	public Map<String, String> getHeaderInfo() {
		return headerInfo;
	}


	/**
	 * @return the altHeader
	 */
	public List<VCFHeaderType> getAltHeader() {
		return altHeader;
	}


	/**
	 * @return the filterHeader
	 */
	public List<VCFHeaderType> getFilterHeader() {
		return filterHeader;
	}


	/**
	 * @return the infoHeader
	 */
	public List<VCFHeaderAdvancedType> getInfoHeader() {
		return infoHeader;
	}


	/**
	 * @return the formatHeader
	 */
	public List<VCFHeaderAdvancedType> getFormatHeader() {
		return formatHeader;
	}


	/**
	 * @return the vcf
	 */
	public File getFile() {
		return file;
	}


	@Override
	public String toString () {
		return file.getName();
	}


	/////////////////////////////////////////////////

	/**
	 * Analyze a a VCF position line to update the IDs elements lists.
	 * @param positionInformation the position information object
	 */
	public void retrievePositionInformation (MGPosition positionInformation) {
		retrieveINFOValues(positionInformation.getInfo());
		retrieveFORMATValues(positionInformation);
	}


	/**
	 * Analyze a a VCF INFO line to update the IDs elements lists.
	 * @param line a INFO line of a VCF
	 */
	private void retrieveFORMATValues (MGPosition positionInformation) {
		if (formatHeader.size() > 0) {
			//for (String genomeRawName: genomeNames) {
				for (VCFHeaderAdvancedType header: formatHeader) {
					if (header.getType().isInstance(new String()) && header.acceptMoreElements()) {
						Object value = positionInformation.getFormatValue(header.getId());
						header.addElement(value);
					}
				}
			//}
		}
	}


	/**
	 * Analyze a a VCF INFO line to update the IDs elements lists.
	 * @param line a INFO line of a VCF
	 */
	private void retrieveINFOValues (String line) {
		if (infoHeader.size() > 0) {
			for (VCFHeaderAdvancedType header: infoHeader) {
				if (header.getType().isInstance(new String()) && header.acceptMoreElements()) {
					if (header.getId() == null) {
						
					}
					Object value;
					try {
						value = getIDValue(line, header.getId());
					} catch (Exception e) {
						String info = "ID: " + header.getId() + "; ";
						info += "DESCRIPTION: " + header.getDescription() + "; ";
						info += "NUMBER: " + header.getNumber() + "; ";
						info += "CLASS: " + header.getClass() + "; ";
						System.out.println(info);
					}
					value = getIDValue(line, header.getId());
					//Object value = getIDValue(line, header.getId());
					header.addElement(value);
				}
			}
		}
	}


	/**
	 * Looks in a line for an ID in order to return its value
	 * @param line	part of a VCF line
	 * @param ID	ID name
	 * @return		the value of the ID
	 */
	private Object getIDValue (String line, String ID) {
		int indexID;
		try {
			indexID = line.indexOf(ID);
		} catch (Exception e) {
			System.out.println("line: " + line + "; ID: " + ID);
		}
		indexID = line.indexOf(ID);
		//int indexID = line.indexOf(ID);
		int indexValue = indexID + ID.length() + 1;
		int indexEnd = line.indexOf(";", indexValue);
		if (indexEnd == -1) {
			indexEnd = line.length();
		}
		return line.substring(indexValue, indexEnd);
	}
	
	
	/**
	 * Add a type of variant if it is not already present in the list.
	 * @param genomeRawName name of the genome
	 * @param type	variant type to add
	 */
	public void addVariantType (String genomeRawName, VariantType type) {
		if (!variantTypeList.containsKey(genomeRawName)) {
			variantTypeList.put(genomeRawName, new ArrayList<VariantType>());
		}
		if (!variantTypeList.get(genomeRawName).contains(type)) {
			variantTypeList.get(genomeRawName).add(type);
		}
	}
	
	
	/**
	 * @param genomeName genome name
	 * @return the list of variant type present in this vcf for this genome
	 */
	public List<VariantType> getVariantTypes (String genomeName) {
		String genomeRawName = FormattedMultiGenomeName.getRawName(genomeName);
		if (variantTypeList.containsKey(genomeRawName)) {
			return variantTypeList.get(genomeRawName);
		}
		return null;
	}
	
	
	/**
	 * Checks if this VCF contains the information for the given genome and a variation type
	 * @param genomeName	the name of the genome
	 * @param variantType	the type of the variation
	 * @return	true if this VCF can manage the request
	 */
	public boolean canManage (String genomeName, VariantType variantType) {
		if (getVariantTypes(genomeName).contains(variantType)) {
			return true;
		}
		return false;
	}
	
}
