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

import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAltType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderFilterType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderFormatType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderInfoType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.MGPosition;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFHeader implements Serializable {

	/** Default generated serial version ID */
	private static final long serialVersionUID = 5071204705996276780L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version	
	
	private Map<String, String> 				headerInfo;			// Header main information
	private	Map<String, Class<?>>				fieldType;			// Association between field type and java class
	private	List<String>						fixedColumn;		// Fixed header names included in the VCF file
	
	private List<VCFHeaderType> 				altHeader;			// Header for the ALT field
	private List<VCFHeaderType> 				filterHeader;		// Header for the FILTER field
	private List<VCFHeaderAdvancedType> 		infoHeader;			// Header for the INFO field
	private List<VCFHeaderAdvancedType> 		formatHeader;		// Header for the FORMAT field
	
	private	List<String>						columnNames;		// All column header names
	private	List<String>						genomeRawNames;		// Dynamic header names included in the VCF file (raw genome names)
	private	List<String>						genomeNames;		// Full genome names list
	
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);

		out.writeObject(headerInfo);
		out.writeObject(fieldType);
		out.writeObject(fixedColumn);
		out.writeObject(altHeader);
		out.writeObject(filterHeader);
		out.writeObject(infoHeader);
		out.writeObject(formatHeader);
		out.writeObject(columnNames);
		out.writeObject(genomeRawNames);
		out.writeObject(genomeNames);
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

		headerInfo = (Map<String, String>) in.readObject();
		fieldType = (Map<String, Class<?>>) in.readObject();
		fixedColumn = (List<String>) in.readObject();
		altHeader = (List<VCFHeaderType>) in.readObject();
		filterHeader = (List<VCFHeaderType>) in.readObject();
		infoHeader = (ArrayList<VCFHeaderAdvancedType>) in.readObject();
		formatHeader = (ArrayList<VCFHeaderAdvancedType>) in.readObject();
		columnNames = (List<String>) in.readObject();
		genomeRawNames = (List<String>) in.readObject();
		genomeNames = (List<String>) in.readObject();
	}
	
	
	/**
	 * Constructor of {@link VCFHeader}.
	 */
	protected VCFHeader () {
		initFieldType();
		initFixedColumnList();
		genomeNames = new ArrayList<String>();
	}
	
	
	/**
	 * Initializes field/java class association.
	 */
	protected void initFieldType () {
		fieldType = new HashMap<String, Class<?>>();
		fieldType.put("Integer", Integer.class);
		fieldType.put("Float", Float.class);
		fieldType.put("Flag", Boolean.class);
		fieldType.put("Character", char.class);
		fieldType.put("String", String.class);
	}
	
	
	/**
	 * Initializes column header list.
	 */
	private void initFixedColumnList () {
		fixedColumn = new ArrayList<String>();
		fixedColumn.add(VCFColumnName.CHROM.toString());
		fixedColumn.add(VCFColumnName.POS.toString());
		fixedColumn.add(VCFColumnName.ID.toString());
		fixedColumn.add(VCFColumnName.REF.toString());
		fixedColumn.add(VCFColumnName.ALT.toString());
		fixedColumn.add(VCFColumnName.QUAL.toString());
		fixedColumn.add(VCFColumnName.FILTER.toString());
		fixedColumn.add(VCFColumnName.INFO.toString());
		fixedColumn.add(VCFColumnName.FORMAT.toString());
	}
	
	
	/**
	 * This method reads and saves the vcf header information
	 * @param reader the VCF reader
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void processHeader (VCFReader reader) throws FileNotFoundException, IOException {
		boolean valid = true;
		headerInfo = new HashMap<String, String>();
		altHeader = new ArrayList<VCFHeaderType>();
		filterHeader = new ArrayList<VCFHeaderType>();
		infoHeader = new ArrayList<VCFHeaderAdvancedType>();
		formatHeader = new ArrayList<VCFHeaderAdvancedType>();
		while (valid) {
			String line = reader.getVCFParser().readLine();
			if (line.length() > 0) {
				if (line.substring(0, 2).equals("##")) {
					int equalChar = line.indexOf("=");
					String type = line.substring(2, equalChar);

					if (type.equals(VCFColumnName.INFO.toString()) ||
							type.equals(VCFColumnName.ALT.toString()) ||
							type.equals(VCFColumnName.FILTER.toString()) ||
							type.equals(VCFColumnName.FORMAT.toString())) {
						Map<String, String> info = parseVCFHeaderInfo(line.substring(equalChar + 2, line.length() - 2));

						VCFHeaderType headerType = null;

						if (type.equals(VCFColumnName.ALT.toString())) {
							headerType = new VCFHeaderAltType();
							altHeader.add(headerType);
						} else if (type.equals(VCFColumnName.FILTER.toString())) {
							headerType = new VCFHeaderFilterType();
							filterHeader.add(headerType);
						} else if (type.equals(VCFColumnName.INFO.toString())) {
							headerType = new VCFHeaderInfoType();
							infoHeader.add((VCFHeaderAdvancedType) headerType);
						} else if (type.equals(VCFColumnName.FORMAT.toString())) {
							headerType = new VCFHeaderFormatType();
							formatHeader.add((VCFHeaderAdvancedType) headerType);
						}

						headerType.setId(info.get(VCFColumnName.ID.toString()));
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
			if (detail.length > 1 && !detail[0].equals("Description")) {
				info.put(detail[0], detail[1]);
			}
		}
		String descriptionPattern = "Description=\"";
		int descriptionStart = line.indexOf(descriptionPattern) + descriptionPattern.length();
		int descriptionStop = line.indexOf("\"", descriptionStart);
		String description = line.substring(descriptionStart, descriptionStop);
		info.put("Description", description);
		return info;
	}
	
	
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
	 * Add a genome name to the list of genome name
	 * @param genomeName a full genome name
	 */
	protected void addGenomeName (String genomeName) {
		if (!genomeNames.contains(genomeName)) {
			genomeNames.add(genomeName);
		}
	}


	/**
	 * @return the columnNames
	 */
	public List<String> getRawGenomesNames() {
		if (genomeRawNames == null) {
			List<String> list = new ArrayList<String>();
			for (String s: columnNames) {
				if (!fixedColumn.contains(s)) {
					list.add(s);
				}
			}
			Collections.sort(list);
			genomeRawNames = list;
		}
		return genomeRawNames;
	}
	
	
	/**
	 * @return the columnNames
	 */
	public List<String> getColumnNames() {
		return columnNames;
	}
	
	
	/**
	 * @return the genomeNames
	 */
	public List<String> getGenomeNames() {
		return genomeNames;
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
}
