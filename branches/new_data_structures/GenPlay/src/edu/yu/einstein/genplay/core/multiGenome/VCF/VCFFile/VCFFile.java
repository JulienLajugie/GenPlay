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
package edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.manager.ProjectFiles;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFStatistics.VCFFileFullStatistic;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.ListOfIntArraysAsIntegerList;


/**
 * This class handles VCF files.
 * It indexes information to perform fast queries.
 * It also gets VCF header information.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFFile implements Serializable {

	/** Default generated serial version ID */
	private static final long serialVersionUID = 7316097355767936880L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private File 								file;				// Path of the VCF file
	private VCFHeader 							header;				// Information about the header
	private VCFReader 							reader;				// Reader for the VCF file
	private VCFFileFullStatistic				statistics;			// VCF file statistics
	private Map<String, List<VariantType>>		variantTypeList;	// List of the different variant type contained in the VCF file and sorted by genome name
	private ListOfIntArraysAsIntegerList		positionList;		// reference genome position array (indexes match with the boolean list of filters)
	private Chromosome							chromosomeOfList;


	/**
	 * Constructor of {@link VCFFile}
	 * @param file		the VCF file
	 * @throws IOException
	 */
	public VCFFile (File file) throws IOException {
		this.file = file;
		positionList = null;
		variantTypeList = new HashMap<String, List<VariantType>>();

		reader = new VCFReader();
		header = new VCFHeader();
		statistics = new VCFFileFullStatistic();

		indexVCFFile();
		header.processHeader(reader);
		reader.setColumnNames(header.getColumnNames());

		chromosomeOfList = null;
	}


	/**
	 * Add a genome name to the list of genome name
	 * @param genomeName a full genome name
	 */
	public void addGenomeName (String genomeName) {
		header.addGenomeName(genomeName);
		statistics.addGenomeName(genomeName);
	}


	/**
	 * Add a type of variant if it is not already present in the list.
	 * @param genomeName name of the genome
	 * @param type	variant type to add
	 */
	public void addVariantType (String genomeName, VariantType type) {
		if (!variantTypeList.containsKey(genomeName)) {
			variantTypeList.put(genomeName, new ArrayList<VariantType>());
		}
		if (!variantTypeList.get(genomeName).contains(type)) {
			variantTypeList.get(genomeName).add(type);
		}
	}


	/**
	 * Checks if this VCF contains the information for the given genome and a variation type
	 * @param genomeName	the name of the genome
	 * @param variantType	the type of the variation
	 * @return	true if this VCF can manage the request
	 */
	public boolean canManage (String genomeName, VariantType variantType) {
		if ((getVariantTypes(genomeName) != null) && getVariantTypes(genomeName).contains(variantType)) {
			return true;
		}
		return false;
	}


	private boolean chromosomeHasChanged (Chromosome chromosome) {
		if (chromosomeOfList == null) {
			return true;
		}

		if (!chromosomeOfList.equals(chromosome)){
			return true;
		}

		return false;
	}


	@Override
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		// object must be Test at this point
		VCFFile test = (VCFFile)obj;
		return file.getAbsolutePath().equals(test.getFile().getAbsoluteFile());
	}


	/**
	 * @return the vcf
	 */
	public File getFile() {
		return file;
	}


	/**
	 * Gets the value of the FORMAT field and a specific field
	 * @param value	the FORMAT string
	 * @param field the specific field
	 * @return		the value of the specific field of the FORMAT field
	 */
	public Object getFormatValue (String value, String field) {
		Object result = null;
		List<VCFHeaderAdvancedType> formatHeader = header.getFormatHeader();
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
	 * @return the header
	 */
	public VCFHeader getHeader() {
		return header;
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

		while (!found && (index < list.size())) {
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
	 * Gets the value according to the INFO field and a specific field
	 * @param info	the INFO string
	 * @param field	the specific field
	 * @return		the value of the specific field of the INFO field
	 */
	public Object getInfoValues (String info, String field) {
		Object result = null;
		List<VCFHeaderAdvancedType> infoHeader = header.getInfoHeader();
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


	/////////////////////////////////////////////////

	/**
	 * @return the positionList
	 */
	public ListOfIntArraysAsIntegerList getPositionList() {
		return positionList;
	}


	/**
	 * @return the reader
	 */
	public VCFReader getReader() {
		return reader;
	}


	/**
	 * @return the statistics
	 */
	public VCFFileFullStatistic getStatistics() {
		return statistics;
	}


	/**
	 * @param genomeName genome name
	 * @return the list of variant type present in this vcf for this genome
	 */
	public List<VariantType> getVariantTypes (String genomeName) {
		if (variantTypeList.containsKey(genomeName)) {
			return variantTypeList.get(genomeName);
		}
		return null;
	}


	/**
	 * This method indexes the VCF file using the Tabix Java API.
	 * @throws IOException
	 */
	private void indexVCFFile () throws IOException {
		if (!isVCFIndexed ()) {
			file = ProjectFiles.getInstance().getValidFileOf(file);
			if (reader == null) {
				reader = new VCFReader();
			}
			reader.indexVCFFile(file);
		}
	}


	/**
	 * Initializes the list of reference genome position for this reader.
	 * It is required when using VCF Filters.
	 * @param chromosome the current chromosome
	 * @param results		the list of result
	 */
	public void initializePositionList (Chromosome chromosome, List<VCFLine> results) {
		if (chromosomeHasChanged(chromosome)) {
			if (results != null) {
				positionList = new ListOfIntArraysAsIntegerList(results.size());
				//VCFLine line = new VCFLine(null, null);
				for (int i = 0; i < results.size(); i++) {
					//line.initialize(results.get(i), null);
					positionList.set(i, results.get(i).getReferencePosition());
				}
			} else {
				positionList = new ListOfIntArraysAsIntegerList(0);
			}
		}
	}



	/**
	 * This method checks if the VCF has been indexed.
	 * @return true if the VCF is indexed
	 */
	private boolean isVCFIndexed () {
		if ((reader != null) && (reader.getVCFParser() != null)) {
			return true;
		}
		return false;
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
		header = (VCFHeader) in.readObject();
		statistics = (VCFFileFullStatistic) in.readObject();
		variantTypeList = (Map<String, List<VariantType>>) in.readObject();
		positionList = (ListOfIntArraysAsIntegerList) in.readObject();
		chromosomeOfList = (Chromosome) in.readObject();
		indexVCFFile(); // recreate the tabix reader
		reader.setColumnNames(header.getColumnNames());
	}


	@Override
	public String toString () {
		return file.getName();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(file);
		out.writeObject(header);
		out.writeObject(statistics);
		out.writeObject(variantTypeList);
		out.writeObject(positionList);
		out.writeObject(chromosomeOfList);
	}

}
