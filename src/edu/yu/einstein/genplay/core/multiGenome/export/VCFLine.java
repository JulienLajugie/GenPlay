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
package edu.yu.einstein.genplay.core.multiGenome.export;

import java.util.HashMap;
import java.util.Map;

import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFLine {

	private final BGZIPReader reader;
	private final String[] elements;


	protected VCFLine (BGZIPReader reader, String line) {
		this.reader = reader;
		if (line == null) {							// if null
			elements = null;						// there is no element and it is the last line
		} else if (line.isEmpty()) {				// if empty: bad reading behavior
			elements = new String[0];				// we filter setting the elements as "empty" in order to be skipped in the process
		} else {									// the line matches the requirements 
			elements = Utils.splitWithTab(line);	// we split with tabulations
		}
	}


	protected void showElements () {
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
	protected boolean isValid () {
		if (elements != null && elements.length > 10) {
			return true;
		}
		return false;
	}


	/**
	 * @return true if it is the last line (the line has no data), false otherwise (the line has data)
	 */
	protected boolean isLastLine () {
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
	protected String getCHROM () {
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
	protected String getID () {
		return elements[2];
	}

	/**
	 * @return the REF field
	 */
	protected String getREF () {
		return elements[3];
	}

	/**
	 * @return the ALT field
	 */
	protected String getALT () {
		return elements[4];
	}

	/**
	 * @return the QUAL field
	 */
	protected String getQUAL () {
		return elements[5];
	}

	/**
	 * @return the FILTER field
	 */
	protected String getFILTER () {
		return elements[6];
	}

	/**
	 * @return the INFO field
	 */
	protected String getINFO () {
		return elements[7];
	}

	/**
	 * @return the FORMAT field
	 */
	protected String getFORMAT () {
		return elements[8];
	}

	/**
	 * @param index index of the field
	 * @return the field associated to the index
	 */
	protected String getField (int index) {
		if (index < elements.length) {
			return elements[index];
		}
		return null;
	}
	
	
	/**
	 * @return the whole line as map (keys are column names)
	 */
	protected Map<String, Object> toFullMap () {
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
}
