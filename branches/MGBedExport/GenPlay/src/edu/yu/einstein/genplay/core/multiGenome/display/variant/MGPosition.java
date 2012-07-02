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
package edu.yu.einstein.genplay.core.multiGenome.display.variant;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.util.Utils;

/**
 * This class gathers all common genome information contained in a line of a VCF file.
 * When created, it is done according to a specific variant, the genome raw name is therefore the one related to this variant.
 * However, the VCF Line object can contain information about the other variant.
 * For the FORMAT methods, if no genome raw name are defined, the default name is the one defined at the creation.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGPosition implements Serializable {

	private static final long serialVersionUID = 3254401647936434675L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private VariantInterface		variant;	// The variant
	private Map<String, Object> 	VCFLine;	// The line from the VCF file
	private VCFFile 				vcfFile;	// The vcfFile object of the VCF file
	private String genomeRawName;
	private List<String> otherGenomeRawName;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(variant);
		out.writeObject(VCFLine);
		out.writeObject(vcfFile);
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
		variant = (VariantInterface) in.readObject();
		VCFLine = (Map<String, Object>) in.readObject();
		vcfFile = (VCFFile) in.readObject();
	}


	/**
	 * Constructor of {@link MGPosition}
	 * @param variant 		the native variant
	 * @param line 			the line information from the VCF file
	 * @param vcfFile 		the VCF file associated to the the VCF file
	 */
	public MGPosition (VariantInterface variant, Map<String, Object> line, VCFFile vcfFile) {
		this.variant = variant;
		this.vcfFile = vcfFile;
		VCFLine = line;
		this.genomeRawName = FormattedMultiGenomeName.getRawName(variant.getVariantListForDisplay().getAlleleForDisplay().getGenomeInformation().getName());
		initOtherGenomeRawName();
	}


	/**
	 * @return the variant
	 */
	public VariantInterface getVariant() {
		return variant;
	}


	/**
	 * @return the chromosome name
	 */
	public String getChromosomeName() {
		return variant.getVariantListForDisplay().getChromosome().getName();
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
	 * @return the QUAL field or 100.0 if it is unknown (defined as a '.')
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
		return vcfFile.getInfoValues(getString(VCFLine.get("INFO")), field);
	}


	/**
	 * @return the FORMAT field
	 */
	public String getFormat() {
		return getString(VCFLine.get("FORMAT"));
	}


	/**
	 * @return the format value for the given genome name
	 */
	public String getFormatValues() {
		return getFormatValues(genomeRawName);
	}

	
	/**
	 * @param genomeRawName a genome raw name
	 * @return the format value for the given genome name
	 */
	public String getFormatValues(String genomeRawName) {
		return getString(VCFLine.get(genomeRawName));
	}
	

	/**
	 * @param field	an ID from the FORMAT field
	 * @return		the value associated to the ID
	 */
	public Object getFormatValue(String field) {
		return getFormatValue(field, genomeRawName);
	}
	
	
	/**
	 * @param field	an ID from the FORMAT field
	 * @param genomeRawName a genome raw name
	 * @return		the value associated to the ID
	 */
	public Object getFormatValue(String field, String genomeRawName) {
		Object result = null;
		String[] formatHeader = Utils.split(getString(VCFLine.get("FORMAT")), ':');
		String[] formatValues;
		if (formatHeader.length == 1) {
			formatValues = new String[1];
			formatValues[0] = getString(VCFLine.get(genomeRawName));
		} else {
			formatValues = Utils.split(getString(VCFLine.get(genomeRawName)), ':');
		}
		for (int i = 0; i < formatHeader.length; i++) {
			if (formatHeader[i].equals(field)) {
				return vcfFile.getFormatValue(formatValues[i], field);
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
	 * @return the map of the VCF line
	 */
	public Map<String, Object> getMappedVCFLine () {
		return VCFLine;
	}
	
	
	/**
	 * Gets the INFO header related to an ID
	 * @param id	the ID
	 * @return		the Header
	 */
	public VCFHeaderAdvancedType getInfoHeader (String id) {
		VCFHeaderAdvancedType header = null;
		List<VCFHeaderAdvancedType> headers = vcfFile.getHeader().getInfoHeader();
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
		List<VCFHeaderAdvancedType> headers = vcfFile.getHeader().getFormatHeader();
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
		List<VCFHeaderType> headers = vcfFile.getHeader().getAltHeader();
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
	 * @return the vcfFile
	 */
	public VCFFile getReader() {
		return vcfFile;
	}


	/**
	 * Casts an object to a String value and performs a trim operation
	 * @param o Object to cast
	 * @return	a String value
	 */
	private String getString (Object o) {
		return o.toString().trim();
	}
	
	
	/**
	 * @return the genomeRawName
	 */
	public String getGenomeRawName() {
		return genomeRawName;
	}


	/**
	 * Initialize the list of name of the other genome
	 */
	private void initOtherGenomeRawName () {
		otherGenomeRawName = new ArrayList<String>();
		for (String key: VCFLine.keySet()) {
			if (!key.equals(VCFColumnName.CHROM.toString()) &&
					!key.equals(VCFColumnName.POS.toString()) &&
					!key.equals(VCFColumnName.ID.toString()) &&
					!key.equals(VCFColumnName.REF.toString()) &&
					!key.equals(VCFColumnName.ALT.toString()) &&
					!key.equals(VCFColumnName.QUAL.toString()) &&
					!key.equals(VCFColumnName.FILTER.toString()) &&
					!key.equals(VCFColumnName.INFO.toString()) &&
					!key.equals(VCFColumnName.FORMAT.toString()) &&
					!key.equals(genomeRawName)) {
				otherGenomeRawName.add(key);
			}
		}
	}
	
	
	/**
	 * @return the list of genome raw name that are different than the default one
	 */
	public List<String> getOtherGenomeRawName () {
		return otherGenomeRawName;
	}
	
	
	/**
	 * @return the list of genome raw names this object defines.
	 */
	public List<String> getAllGenomeRawNames () {
		List<String> result = new ArrayList<String>();
		result.add(genomeRawName);
		for (String name: otherGenomeRawName) {
			result.add(name);
		}
		Collections.sort(result);
		return result;
	}
	
	
	/**
	 * @return true if this object contains information about other genomes, false otherwise
	 */
	public boolean containsOtherGenomeInformation () {
		if (otherGenomeRawName.size() == 0) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * Show the position information
	 */
	public void show () {
		String info = "-----------------";
		System.out.println();
		info += "Position information:\n";
		for (String key: VCFLine.keySet()) {
			info += key + ": " + VCFLine.get(key) + "\n";
		}
		info += "-----------------";
		System.out.println(info);
	}
}
