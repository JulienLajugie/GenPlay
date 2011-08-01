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
public class MGPositionInformation {

	private Chromosome chromosome;
	private Map<String, Object> VCFLine;
	private VCFReader reader;


	/**
	 * Constructor of {@link MGPositionInformation}
	 * @param chromosome 	the chromosome
	 * @param line 			the line information from the VCF file
	 * @param reader 		the VCF reader associated to the the VCF file
	 */
	public MGPositionInformation (Chromosome chromosome, Map<String, Object> line, VCFReader reader) {
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
	public String getId() {
		return VCFLine.get("ID").toString();
	}

	
	/**
	 * @return the REF field
	 */
	public String getReference() {
		return VCFLine.get("REF").toString();
	}

	
	/**
	 * @return the ALT field
	 */
	public String getAlternative() {
		return VCFLine.get("ALT").toString();
	}

	
	/**
	 * @return the QUAL field or 50.0 if it is unknown (defined as a '.')
	 */
	public Double getQuality() {
		try {
			
			return Double.valueOf(VCFLine.get("QUAL").toString());
		} catch (Exception e) {
			System.out.println("catch");
			return 50.0;
		}
	}

	
	/**
	 * @return the FILTER field
	 */
	public boolean getFilter() {
		if (VCFLine.get("FILTER").toString().equals("PASS")) {
			return true;
		}
		return false;
	}

	
	/**
	 * @return the INFO field
	 */
	public String getInfo() {
		return VCFLine.get("INFO").toString();
	}

	
	/**
	 * @param field an ID from the INFO field
	 * @return the value associated to the ID
	 */
	public Object getInfoValue(String field) {
		return reader.getInfoValues(VCFLine.get("INFO").toString(), field);
	}

	
	/**
	 * @return the FORMAT field
	 */
	public String getFormat() {
		return VCFLine.get("FORMAT").toString();
	}

	
	/**
	 * @param genomeRawName the genome raw name
	 * @return the format value for the given genome name
	 */
	public String getFormatValues(String genomeRawName) {
		return VCFLine.get(genomeRawName).toString();
	}

	
	/**
	 * @param genomeRawName the genome raw name
	 * @param field			an ID from the FORMAT field
	 * @return				the value associated to the ID
	 */
	public Object getFormatValue(String genomeRawName, String field) {
		Object result = null;
		String[] formatHeader = VCFLine.get("FORMAT").toString().split(":");
		String[] formatValues;
		if (formatHeader.length == 1) {
			formatValues = new String[1];
			//showLine();
			formatValues[0] = VCFLine.get(genomeRawName).toString();
		} else {
			formatValues = VCFLine.get(genomeRawName).toString().split(":");
		}
		for (int i = 0; i < formatHeader.length; i++) {
			if (formatHeader[i].equals(field)) {
				return reader.getFormatValue(formatValues[i], field);
			}
		}
		return result;
	}

	
	
	@SuppressWarnings("unused")
	private void showLine () {
		String info = "";
		for (String key: VCFLine.keySet()) {
			info += key + ": " + VCFLine.get(key) + " | ";
		}
		System.out.println(info);
	}
	
}
