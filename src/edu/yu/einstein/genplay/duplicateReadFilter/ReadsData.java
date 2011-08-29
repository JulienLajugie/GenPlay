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
package edu.yu.einstein.genplay.duplicateReadFilter;


/**
 * Class representing a read
 * @author Chirag Gorasia
 * @version 0.1
 */
public class ReadsData {
	
	private String 	readName;		// the name of the read
	private String 	strand;			// the strand
	private String 	chromosome;		// the chromosome
	private int 	start;			// the start position

	
	/**
	 * Creates an instance of {@link ReadsData}
	 * @param readName name of the read
	 * @param strand strand
	 * @param chromosome chromosome
	 * @param start start position
	 */
	public ReadsData(String readName, String strand, String chromosome, int start) {//, String geneSequence, String unusedCodeField, int unusedUnknownField, String mismatches) {
		this.setReadName(readName);
		this.setStrand(strand);
		this.setChromosome(chromosome);
		this.setStart(start);
	}

	
	/**
	 * @param readName the readName to set
	 */
	public void setReadName(String readName) {
		this.readName = readName;
	}

	
	/**
	 * @return the readName
	 */
	public String getReadName() {
		return readName;
	}
	

	/**
	 * @param strand the strand to set
	 */
	public void setStrand(String strand) {
		this.strand = strand;
	}

	
	/**
	 * @return the strand
	 */
	public String getStrand() {
		return strand;
	}

	
	/**
	 * @param chromosome the chromosome to set
	 */
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	
	/**
	 * @return the chromosome
	 */
	public String getChromosome() {
		return chromosome;
	}

	
	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}
	

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}
}
