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
package edu.yu.einstein.genplay.core.genome;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import edu.yu.einstein.genplay.core.Chromosome;

/**
 * This class contains assembly information
 * @author Nicolas Fourel
 */
public class Assembly implements Serializable{

	private static final long 		serialVersionUID = -1933285290898527392L;	// generated ID
	private Map<String, Chromosome>	chromosomeList;
	private String 					name;
	private Date 					date;
	private String 					indexName;
	private SimpleDateFormat 		sdf;
	private long 					genomomeLength = 0;
	
	
	/**
	 * Constructor of {@link Assembly}
	 * @param name	name of the assembly
	 * @param date	date of build of the assembly
	 */
	//protected Assembly (String name, String date) {
	public Assembly (String name, String date) {
		chromosomeList = new HashMap<String, Chromosome>();
		this.name = name;
		sdf = new SimpleDateFormat("MM yyyy");
		try {
			this.date = sdf.parse(date);
			sdf.applyPattern("yyyy MM");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		sdf.applyPattern("yyyy MM ");
		indexName = sdf.format(this.date);
		indexName = indexName.concat(name);
	}
	
	
	/**
	 * @return the list of chromosome
	 */
	public Map<String, Chromosome> getChromosomeList() {
		return chromosomeList;
	}
	
	
	/**
	 * @param chromosomeList the new chromosome list
	 */
	public void setChromosomeList(Map<String, Chromosome> chromosomeList) {
		this.chromosomeList = chromosomeList;
		computeGenomeSize();
	}


	/**
	 * Compute the size of the genome
	 */
	private synchronized void computeGenomeSize() {
		genomomeLength = 0;
		for (Chromosome currenChromosome: chromosomeList.values()) {
			genomomeLength += currenChromosome.getLength();
		}
	}
	
	
	/**
	 * @return the assembly name for display
	 */
	public String getDisplayName () {
		sdf.applyPattern("MMM yyyy (");
		return sdf.format(this.date).concat(name).concat(")");
	}
	
	
	
	/**
	 * @return the assembly name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the assembly date
	 */
	public Date getDate() {
		return date;
	}
	
	
	/**
	 * @return the full name of the assembly: name (mm/yyyy)
	 */
	public String getIndexName () {
		return indexName;
	}


	/**
	 * @return the length of the genome in bp
	 */
	public long getGenomeLength() {
		return genomomeLength;
	}
	
	
}