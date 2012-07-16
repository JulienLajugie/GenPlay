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
package edu.yu.einstein.genplay.core.multiGenome.export.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.utils.VCFLineUtility;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFLine {

	private final BGZIPReader reader;
	private final String[] elements;
	private final static HashMap<String, Chromosome> chromosomeNamesMap = new HashMap<String, Chromosome>();
	private boolean hasData;			// Used during the export only. Some line can be qualified according to different constraints (variations, filters...). If the line passes those constraints, it has then the significant data.


	/**
	 * Constructor of {@link VCFLine}
	 * @param reader	the BGZIP reader
	 * @param line		the line from the VCF
	 */
	public VCFLine (BGZIPReader reader, String line) {
		this.reader = reader;
		if (line == null) {							// if null
			elements = null;						// there is no element and it is the last line
		} else if (line.isEmpty()) {				// if empty: bad reading behavior
			elements = new String[0];				// we filter setting the elements as "empty" in order to be skipped in the process
		} else {									// the line matches the requirements
			elements = Utils.splitWithTab(line);	// we split with tabulations
		}
		hasData = false;
	}





	/**
	 * Shows the elements of line
	 */
	public void showElements () {
		String info = "";
		for (int i = 0; i < elements.length; i++) {
			info += i + ": " + elements[i];
			if (i < (elements.length - 1)) {
				info += "\n";
			}
		}
		System.out.println(info);
	}


	/**
	 * @return true if the line is valid, false otherwise
	 */
	public boolean isValid () {
		if ((elements != null) && (elements.length > 10)) {
			return true;
		}
		return false;
	}


	/**
	 * @return true if it is the last line (the line has no data), false otherwise (the line has data)
	 */
	public boolean isLastLine () {
		if (elements == null) {
			return true;
		}
		return false;
	}


	/**
	 * @return the reader
	 */
	public BGZIPReader getReader() {
		return reader;
	}


	/**
	 * @return the CHROM field
	 */
	public String getCHROM () {
		return elements[0];
	}

	/**
	 * @return the POS field
	 */
	public String getPOS () {
		return elements[1];
	}

	/**
	 * @return the ID field
	 */
	public String getID () {
		return elements[2];
	}

	/**
	 * @return the REF field
	 */
	public String getREF () {
		return elements[3];
	}

	/**
	 * @return the ALT field
	 */
	public String getALT () {
		return elements[4];
	}

	/**
	 * @return the QUAL field
	 */
	public String getQUAL () {
		return elements[5];
	}

	/**
	 * @return the FILTER field
	 */
	public String getFILTER () {
		return elements[6];
	}

	/**
	 * @return the INFO field
	 */
	public String getINFO () {
		return elements[7];
	}

	/**
	 * @return the FORMAT field
	 */
	public String getFORMAT () {
		return elements[8];
	}

	/**
	 * @param index index of the field
	 * @return the field associated to the index
	 */
	public String getField (int index) {
		if (index < elements.length) {
			return elements[index];
		}
		return null;
	}


	/**
	 * @param genomeIndex	the index of the genome
	 * @param fieldIndex	the index of the field (starts from 0)
	 * @return	the object contained in the format field of a genome at the specified index.
	 */
	public Object getFormatField (int genomeIndex, int fieldIndex) {
		String format = getField(genomeIndex);
		if (format != null) {
			Object[] fields = Utils.split(format, ':');
			if (fieldIndex < fields.length) {
				return fields[fieldIndex];
			}
		}
		return null;
	}


	/**
	 * @return the chromosome related to the VCF line
	 */
	public Chromosome getChromosome () {
		Chromosome chromosome = null;
		if (chromosomeNamesMap.containsKey(getCHROM())) {
			chromosome = chromosomeNamesMap.get(getCHROM());
		} else {
			String shortLineName = getShortChromosomeName(getCHROM());
			List<Chromosome> chromosomeList = ProjectManager.getInstance().getProjectChromosome().getChromosomeList();
			for (Chromosome current: chromosomeList) {
				String shortProjectChromosomeName = getShortChromosomeName(current.getName());
				if (shortLineName.equals(shortProjectChromosomeName)) {
					chromosome = current;
				}
			}
		}
		if (chromosome != null) {
			chromosomeNamesMap.put(getCHROM(), chromosome);
		}
		return chromosome;
	}


	/**
	 * Reduce the chromosome name removing any knid of prefix.
	 * Keeps everything after the first integer
	 * @param chromosomeName the chromosome name
	 * @return	the shorted name
	 */
	private String getShortChromosomeName (String chromosomeName) {
		String shortName;
		int intOffset = Utils.getFirstIntegerOffset(chromosomeName, 0);

		if (intOffset != -1) {
			shortName = chromosomeName.substring(intOffset);
		} else {
			char lastChar = chromosomeName.charAt(chromosomeName.length() - 1);
			if ((lastChar == 'X') || (lastChar == 'Y') || (lastChar == 'M')) {
				shortName = "" + lastChar;
			} else {
				shortName = chromosomeName;
			}
		}
		return shortName;
	}


	/**
	 * Get the value associated to the header.
	 * The genome index is used only if the header is related to a FORMAT fields.
	 * @param header		the header
	 * @param genomeIndex	the index of the genome in the line
	 * @return				the value
	 */
	public Object getHeaderField (VCFHeaderType header, int genomeIndex) {
		Object result = null;
		if (header.getColumnCategory() == VCFColumnName.QUAL) {
			result = getQUAL();
		} else if (header.getColumnCategory() == VCFColumnName.INFO) {
			result = VCFLineUtility.getInfoValue(getINFO(), header);
		} else if (header.getColumnCategory() == VCFColumnName.FORMAT) {
			result = VCFLineUtility.getFormatValue(getFORMAT(), getField(genomeIndex), header);
		}
		return result;
	}


	/**
	 * @return the whole line as map (keys are column names)
	 */
	public Map<String, Object> toFullMap () {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(VCFColumnName.CHROM.toString(), getCHROM());
		map.put(VCFColumnName.POS.toString(), getPOS());
		map.put(VCFColumnName.ID.toString(), getID());
		map.put(VCFColumnName.REF.toString(), getREF());
		map.put(VCFColumnName.ALT.toString(), getALT());
		map.put(VCFColumnName.QUAL.toString(), getQUAL());
		map.put(VCFColumnName.FILTER.toString(), getFILTER());
		map.put(VCFColumnName.INFO.toString(), getINFO());
		map.put(VCFColumnName.FORMAT.toString(), getFORMAT());

		for (int i = 9; i < elements.length; i++) {
			map.put(reader.getGenomeFromIndex(i), elements[i]);
		}

		return map;
	}


	/**
	 * @return the hasData
	 */
	public boolean hasData() {
		return hasData;
	}


	/**
	 * @param hasData the hasData to set
	 */
	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}


	@Override
	public String toString () {
		String s = "";
		for (int i = 0; i < (elements.length - 1); i++) {
			s += elements[i] + "\t";
		}
		s += elements[elements.length - 1];
		return s;
	}
}
