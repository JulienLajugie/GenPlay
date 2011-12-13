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
package edu.yu.einstein.genplay.core.multiGenome.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;

/**
 * This class gathers all common genome information contained in a line of a VCF file.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGPosition implements Serializable {

	private static final long serialVersionUID = 3254401647936434675L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private Chromosome 				chromosome;	// The chromosome
	private Map<String, Object> 	VCFLine;	// The line from the VCF file
	private VCFReader 				reader;		// The reader object of the VCF file


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(chromosome);
		out.writeObject(VCFLine);
		out.writeObject(reader);		
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
		chromosome = (Chromosome) in.readObject();
		VCFLine = (Map<String, Object>) in.readObject();
		reader = (VCFReader) in.readObject();
	}


	/**
	 * Constructor of {@link MGPosition}
	 * @param chromosome 	the chromosome
	 * @param line 			the line information from the VCF file
	 * @param reader 		the VCF reader associated to the the VCF file
	 */
	public MGPosition (Chromosome chromosome, Map<String, Object> line, VCFReader reader) {
		this.chromosome = chromosome;
		this.reader = reader;
		VCFLine = line;
		this.reader.retrievePositionInformation(this);
	}


	/**
	 * @return the chromosome name
	 */
	public String getChromosomeName() {
		if (chromosome != null) {
			return chromosome.getName();
		}
		return ProjectManager.getInstance().getAssembly().getDisplayName();
	}


	/**
	 * @return the POS field (position on the reference genome)
	 */
	public int getPos() {
		return Integer.parseInt(getString(VCFLine.get("POS")));
	}


	/**
	 * @return the ID field
	 */
	public String getId() {
		return getString(VCFLine.get("ID"));
	}


	/**
	 * @return the REF field
	 */
	public String getReference() {
		return getString(VCFLine.get("REF"));
	}


	/**
	 * @return the ALT field
	 */
	public String getAlternative() {
		return getString(VCFLine.get("ALT"));
	}


	/**
	 * @return the QUAL field or 50.0 if it is unknown (defined as a '.')
	 */
	public Double getQuality() {
		try {
			return Double.valueOf(getString(VCFLine.get("QUAL")));
		} catch (Exception e) {
			return 100.0;
		}
	}


	/**
	 * @return the FILTER field
	 */
	public String getFilter() {
		return getString(VCFLine.get("FILTER"));
	}


	/**
	 * @return the INFO field
	 */
	public String getInfo() {
		return getString(VCFLine.get("INFO"));
	}


	/**
	 * @param field an ID from the INFO field
	 * @return the value associated to the ID
	 */
	public Object getInfoValue(String field) {
		return reader.getInfoValues(getString(VCFLine.get("INFO")), field);
	}


	/**
	 * @return the FORMAT field
	 */
	public String getFormat() {
		return getString(VCFLine.get("FORMAT"));
	}


	/**
	 * @param genomeRawName the genome raw name
	 * @return the format value for the given genome name
	 */
	public String getFormatValues(String genomeRawName) {
		return getString(VCFLine.get(genomeRawName));
	}


	/**
	 * @param genomeRawName the genome raw name
	 * @param field			an ID from the FORMAT field
	 * @return				the value associated to the ID
	 */
	public Object getFormatValue(String genomeRawName, String field) {
		Object result = null;
		String[] formatHeader = getString(VCFLine.get("FORMAT")).split(":");
		String[] formatValues;
		if (formatHeader.length == 1) {
			formatValues = new String[1];
			formatValues[0] = getString(VCFLine.get(genomeRawName));
		} else {
			formatValues = getString(VCFLine.get(genomeRawName)).split(":");
		}
		for (int i = 0; i < formatHeader.length; i++) {
			if (formatHeader[i].equals(field)) {
				return reader.getFormatValue(formatValues[i], field);
			}
		}
		return result;
	}


	/**
	 * @return the string of the VCF line
	 */
	public String getVCFLine () {
		return VCFLine.toString();
	}
	
	
	/**
	 * Gets the INFO header related to an ID
	 * @param id	the ID
	 * @return		the Header
	 */
	public VCFHeaderAdvancedType getInfoHeader (String id) {
		VCFHeaderAdvancedType header = null;
		List<VCFHeaderAdvancedType> headers = reader.getInfoHeader();
		for (VCFHeaderAdvancedType current: headers) {
			if (current.getId().equals(id)) {
				header = current;
				break;
			}
		}
		return header;
	}
	
	
	/**
	 * Gets the FORMAT header related to an ID
	 * @param id	the ID
	 * @return		the Header
	 */
	public VCFHeaderAdvancedType getFormatHeader (String id) {
		VCFHeaderAdvancedType header = null;
		List<VCFHeaderAdvancedType> headers = reader.getFormatHeader();
		for (VCFHeaderAdvancedType current: headers) {
			if (current.getId().equals(id)) {
				header = current;
				break;
			}
		}
		return header;
	}
	
	
	/**
	 * Gets the ALT header related to an ID
	 * @param id	the ID value (with <>, eg: <DEL> id value will be transformed into DEL to match the ID name)
	 * @return		the Header
	 */
	public VCFHeaderType getAltHeader (String id) {
		VCFHeaderType header = null;
		List<VCFHeaderType> headers = reader.getAltHeader();
		String idTmp = id.substring(1, id.length()-1);
		for (VCFHeaderType current: headers) {
			if (current.getId().equals(idTmp)) {
				header = current;
				break;
			}
		}
		return header;
	}


	/**
	 * Casts an object to a String value and performs a trim operation
	 * @param o Object to cast
	 * @return	a String value
	 */
	private String getString (Object o) {
		return o.toString().trim();
	}
}
