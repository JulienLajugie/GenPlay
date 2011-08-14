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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.engine;

import java.util.Map;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.manager.multiGenomeManager.ReferenceGenomeManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;

/**
 * This class gather all common genome information contained in a line of a VCF file.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGPosition {

	private Chromosome chromosome;
	private Map<String, Object> VCFLine;
	private VCFReader reader;


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
	}


	/**
	 * @return the chromosome name
	 */
	public String getChromosomeName() {
		if (chromosome != null) {
			return chromosome.getName();
		}
		return ReferenceGenomeManager.getInstance().getReferenceName();
	}
	
	
	/**
	 * @return the ID field
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
			//System.out.println("catch");
			return 100.0;
		}
	}

	
	/**
	 * @return the FILTER field
	 */
	public boolean getFilter() {
		if (getString(VCFLine.get("FILTER")).equals("PASS")) {
			return true;
		}
		return false;
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
			
			try {
				formatValues[0] = getString(VCFLine.get(genomeRawName));
			} catch (Exception e) {
				System.out.println(genomeRawName);
				showLine();
			}
			
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
	 * Casts an object to a String value and performs a trim operation
	 * @param o Object to cast
	 * @return	a String value
	 */
	private String getString (Object o) {
		return o.toString().trim();
	}
	
	
	//@SuppressWarnings("unused")
	private void showLine () {
		String info = "";
		for (String key: VCFLine.keySet()) {
			info += key + ": " + VCFLine.get(key) + " | ";
		}
		System.out.println(info);
	}
	
}
